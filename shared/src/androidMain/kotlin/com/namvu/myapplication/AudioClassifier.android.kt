package com.namvu.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioRecord
import android.os.SystemClock
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.audio.audioclassifier.AudioClassifier
import com.google.mediapipe.tasks.audio.audioclassifier.AudioClassifierResult as MediaPipeAudioClassifierResult
import com.google.mediapipe.tasks.audio.core.RunningMode
import com.google.mediapipe.tasks.components.containers.AudioData
import com.google.mediapipe.tasks.core.BaseOptions
import java.io.FileNotFoundException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.sqrt

@Composable
actual fun rememberAudioClassifierEngine(): AudioClassifierEngine {
    val context = LocalContext.current
    val engine = remember(context) {
        AndroidAudioClassifierEngine(context.applicationContext)
    }
    val hasPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasPermission.value = granted
        engine.setHasPermission(granted)
    }

    LaunchedEffect(Unit) {
        engine.setHasPermission(hasPermission.value)
        if (!hasPermission.value) {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    return engine
}

private class AndroidAudioClassifierEngine(
    private val context: Context,
) : AudioClassifierEngine {
    private val modelFileName = "yamnet.tflite"
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    private var audioClassifier: AudioClassifier? = null
    private var audioRecord: AudioRecord? = null
    private var keepRunning = false

    private val mutableClassificationResult = mutableStateOf<AudioClassifierResult?>(null)
    private val mutableIsRunning = mutableStateOf(false)
    private val mutableHasPermission = mutableStateOf(false)
    private val mutableError = mutableStateOf<String?>(null)
    private val mutableVolumeLevel = mutableStateOf(0f)

    override val classificationResult: State<AudioClassifierResult?> = mutableClassificationResult
    override val isRunning: State<Boolean> = mutableIsRunning
    override val hasPermission: State<Boolean> = mutableHasPermission
    override val error: State<String?> = mutableError
    override val volumeLevel: State<Float> = mutableVolumeLevel

    internal fun setHasPermission(hasPermission: Boolean) {
        mutableHasPermission.value = hasPermission
        if (!hasPermission) {
            mutableError.value = "Cần quyền microphone để chạy Audio Classifier."
            stop()
        } else if (mutableError.value?.contains("microphone", ignoreCase = true) == true) {
            mutableError.value = null
        }
    }

    override fun start() {
        if (mutableIsRunning.value) return
        if (!mutableHasPermission.value) {
            mutableError.value = "Cần quyền microphone để chạy Audio Classifier."
            return
        }

        mutableError.value = null
        mutableIsRunning.value = true
        keepRunning = true

        executor.execute {
            runClassificationLoop()
        }
    }

    override fun stop() {
        keepRunning = false
        mutableIsRunning.value = false
        mutableVolumeLevel.value = 0f
        runCatching { audioRecord?.stop() }
        runCatching { audioRecord?.release() }
        audioRecord = null
    }

    private fun runClassificationLoop() {
        try {
            initClassifierIfNeeded()
            val classifier = audioClassifier ?: return
            val record = classifier.createAudioRecord()
            audioRecord = record
            val audioData = AudioData.create(record.format, SampleCount)

            record.startRecording()
            while (keepRunning) {
                val sampleCount = audioData.load(record)
                if (sampleCount > 0) {
                    mutableVolumeLevel.value = calculateVolumeLevel(audioData.getBuffer(), sampleCount)
                    val result = classifier.classify(audioData).toSharedAudioClassifierResult()
                    mutableClassificationResult.value = result
                    mutableError.value = null
                }
                Thread.sleep(ClassificationIntervalMs)
            }
        } catch (e: Exception) {
            mutableError.value = "Không thể chạy Audio Classifier từ microphone."
            Log.e("AudioClassifier", "Failed to classify microphone audio", e)
        } finally {
            stop()
        }
    }

    private fun initClassifierIfNeeded() {
        if (audioClassifier != null) return

        try {
            context.assets.open(modelFileName).close()
            val options = AudioClassifier.AudioClassifierOptions.builder()
                .setBaseOptions(
                    BaseOptions.builder()
                        .setModelAssetPath(modelFileName)
                        .build(),
                )
                .setRunningMode(RunningMode.AUDIO_CLIPS)
                .setMaxResults(5)
                .build()

            audioClassifier = AudioClassifier.createFromOptions(context, options)
        } catch (e: FileNotFoundException) {
            mutableError.value = "Missing MediaPipe model asset: $modelFileName"
            throw IllegalStateException("Missing MediaPipe model asset: $modelFileName", e)
        }
    }

    companion object {
        private const val SampleCount = 16_000
        private const val ClassificationIntervalMs = 50L
    }
}

private fun MediaPipeAudioClassifierResult.toSharedAudioClassifierResult(): AudioClassifierResult =
    AudioClassifierResult(
        categories = classificationResults()
            .flatMap { it.classifications() }
            .flatMap { it.categories() }
            .sortedByDescending { it.score() }
            .map { category ->
                AudioClassificationCategory(
                    index = category.index(),
                    categoryName = category.categoryName(),
                    displayName = category.displayName(),
                    score = category.score(),
                )
            },
        timestampMs = timestampMs().takeIf { it > 0 } ?: SystemClock.uptimeMillis(),
    )

private fun calculateVolumeLevel(samples: FloatArray, sampleCount: Int): Float {
    if (sampleCount <= 0) return 0f
    var sumSquares = 0.0
    val boundedCount = sampleCount.coerceAtMost(samples.size)
    for (index in 0 until boundedCount) {
        val sample = samples[index].toDouble()
        sumSquares += sample * sample
    }
    return sqrt(sumSquares / boundedCount).toFloat().coerceIn(0f, 1f)
}
