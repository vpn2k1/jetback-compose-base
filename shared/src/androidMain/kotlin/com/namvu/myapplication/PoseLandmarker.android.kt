package com.namvu.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
actual fun rememberPoseVideoLandmarker(): PoseVideoLandmarker {
    val context = LocalContext.current
    return remember(context) {
        AndroidPoseVideoLandmarker(context.applicationContext)
    }
}

@Composable
actual fun rememberPoseLandmarkerEngine(): PoseLandmarkerEngine {
    val context = LocalContext.current
    return remember(context) {
        AndroidPoseLandmarkerEngine(context.applicationContext)
    }
}

@Composable
actual fun openVideoPicker(result: (ByteArray?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            result(bytes)
        } else {
            result(null)
        }
    }
    return {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
    }
}

@Composable
actual fun PoseLandmarkerCameraPreview(
    engine: PoseLandmarkerEngine,
    modifier: Modifier,
) {
    val androidEngine = engine as? AndroidPoseLandmarkerEngine
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasPermission by rememberPoseCameraPermissionState()

    if (androidEngine == null) {
        PoseUnsupportedPreview(modifier)
        return
    }

    if (!hasPermission) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF111318)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Camera permission is required for live pose tracking.",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        return
    }

    val controller = remember(context, androidEngine) {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
            setImageAnalysisAnalyzer(androidEngine.cameraExecutor) { imageProxy ->
                androidEngine.analyze(imageProxy)
            }
        }
    }

    DisposableEffect(controller, androidEngine, lifecycleOwner) {
        controller.bindToLifecycle(lifecycleOwner)
        androidEngine.start()
        onDispose {
            controller.clearImageAnalysisAnalyzer()
            controller.unbind()
            androidEngine.stop()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            PreviewView(viewContext).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                scaleType = PreviewView.ScaleType.FIT_CENTER
                this.controller = controller
            }
        },
        update = { previewView ->
            previewView.controller = controller
        },
    )
}

private class AndroidPoseVideoLandmarker(
    private val context: Context,
) : PoseVideoLandmarker {
    private val modelFileName = "pose_landmarker_lite.task"

    override suspend fun detectVideo(videoBytes: ByteArray): PoseVideoResult? =
        withContext(Dispatchers.Default) {
            val videoFile = File.createTempFile("pose_video", ".mp4", context.cacheDir)
            val retriever = MediaMetadataRetriever()
            var poseLandmarker: PoseLandmarker? = null
            try {
                context.assets.open(modelFileName).close()
                videoFile.writeBytes(videoBytes)
                retriever.setDataSource(videoFile.absolutePath)
                val durationMs = retriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull()
                    ?: 0L

                val baseOptions = BaseOptions.builder()
                    .setModelAssetPath(modelFileName)
                    .build()
                val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setRunningMode(RunningMode.VIDEO)
                    .setNumPoses(1)
                    .setMinPoseDetectionConfidence(0.5f)
                    .setMinPosePresenceConfidence(0.5f)
                    .setMinTrackingConfidence(0.5f)
                    .build()
                poseLandmarker = PoseLandmarker.createFromOptions(context, options)

                val sampleCount = 8
                var analyzedFrames = 0
                var previewBytes: ByteArray? = null
                var latestResult: PoseTrackingResult? = null

                for (index in 0 until sampleCount) {
                    val timestampMs = if (durationMs > 0L) {
                        (durationMs * index / sampleCount).coerceAtLeast(0L)
                    } else {
                        index * 100L
                    }
                    val frame = retriever.getFrameAtTime(
                        timestampMs * 1000L,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                    ) ?: continue
                    analyzedFrames++

                    val argbFrame = if (frame.config == Bitmap.Config.ARGB_8888) {
                        frame
                    } else {
                        frame.copy(Bitmap.Config.ARGB_8888, false)
                    }
                    if (previewBytes == null || index == sampleCount - 1) {
                        previewBytes = argbFrame.toJpegBytes()
                    }

                    val mpImage = BitmapImageBuilder(argbFrame).build()
                    val result = poseLandmarker.detectForVideo(mpImage, timestampMs)
                    latestResult = result.toPoseTrackingResult(
                        imageWidth = argbFrame.width,
                        imageHeight = argbFrame.height,
                    )

                    if (argbFrame !== frame) argbFrame.recycle()
                    frame.recycle()
                }

                PoseVideoResult(
                    durationMs = durationMs,
                    analyzedFrames = analyzedFrames,
                    previewFrameBytes = previewBytes,
                    trackingResult = latestResult,
                )
            } catch (e: Exception) {
                Log.e("PoseLandmarker", "Failed to analyze video", e)
                null
            } finally {
                runCatching { retriever.release() }
                poseLandmarker?.close()
                videoFile.delete()
            }
        }
}

private class AndroidPoseLandmarkerEngine(
    private val context: Context,
) : PoseLandmarkerEngine {
    private val modelFileName = "pose_landmarker_lite.task"
    private val inFlight = AtomicBoolean(false)
    private var poseLandmarker: PoseLandmarker? = null

    internal val cameraExecutor = Executors.newSingleThreadExecutor()

    private val mutableTrackingResult = mutableStateOf<PoseTrackingResult?>(null)
    private val mutableIsRunning = mutableStateOf(false)
    private val mutableError = mutableStateOf<String?>(null)

    override val trackingResult: State<PoseTrackingResult?> = mutableTrackingResult
    override val isRunning: State<Boolean> = mutableIsRunning
    override val error: State<String?> = mutableError

    override fun start() {
        mutableIsRunning.value = true
        mutableError.value = null
        initIfNeeded()
    }

    override fun stop() {
        mutableIsRunning.value = false
        inFlight.set(false)
    }

    internal fun analyze(imageProxy: ImageProxy) {
        if (!mutableIsRunning.value || !inFlight.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        try {
            initIfNeeded()
            val bitmap = imageProxy.toPoseUprightBitmap()
            val mpImage = BitmapImageBuilder(bitmap).build()
            poseLandmarker?.detectAsync(mpImage, imageProxy.imageInfo.timestamp / 1_000_000)
        } catch (e: Exception) {
            inFlight.set(false)
            mutableError.value = "Failed to process camera frame."
            Log.e("PoseLandmarker", "Failed to process camera frame", e)
        } finally {
            imageProxy.close()
        }
    }

    private fun initIfNeeded() {
        if (poseLandmarker != null) return

        try {
            context.assets.open(modelFileName).close()
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelFileName)
                .build()
            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setNumPoses(1)
                .setMinPoseDetectionConfidence(0.5f)
                .setMinPosePresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setResultListener { result, inputImage ->
                    inFlight.set(false)
                    val imageWidth = inputImage.width
                    val imageHeight = inputImage.height
                    inputImage.close()
                    mutableTrackingResult.value = result.toPoseTrackingResult(
                        imageWidth = imageWidth,
                        imageHeight = imageHeight,
                    )
                    mutableError.value = null
                }
                .setErrorListener { error ->
                    inFlight.set(false)
                    mutableError.value = error.message ?: "Pose landmarker error."
                    Log.e("PoseLandmarker", "MediaPipe pose landmarker error", error)
                }
                .build()

            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
        } catch (e: FileNotFoundException) {
            mutableError.value = "Missing MediaPipe model asset: $modelFileName"
            throw IllegalStateException("Missing MediaPipe model asset: $modelFileName", e)
        }
    }
}

@Composable
private fun rememberPoseCameraPermissionState(): State<Boolean> {
    val context = LocalContext.current
    val hasPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasPermission.value = granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission.value) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    return hasPermission
}

@Composable
private fun PoseUnsupportedPreview(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111318)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Unsupported pose landmarker engine.",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun PoseLandmarkerResult.toPoseTrackingResult(
    imageWidth: Int,
    imageHeight: Int,
): PoseTrackingResult =
    PoseTrackingResult(
        imageWidth = imageWidth,
        imageHeight = imageHeight,
        poses = landmarks().map { landmarks ->
            landmarks.map { landmark ->
                PoseLandmarkPoint(
                    x = landmark.x(),
                    y = landmark.y(),
                    z = landmark.z(),
                    visibility = landmark.visibility().orElse(null),
                    presence = landmark.presence().orElse(null),
                )
            }
        },
        connections = PoseLandmarker.POSE_LANDMARKS.map { connection ->
            PoseLandmarkConnection(
                start = connection.start(),
                end = connection.end(),
            )
        },
        timestampMs = timestampMs(),
    )

private fun Bitmap.toJpegBytes(): ByteArray =
    ByteArrayOutputStream().use { output ->
        compress(Bitmap.CompressFormat.JPEG, 85, output)
        output.toByteArray()
    }

private fun ImageProxy.toPoseUprightBitmap(): Bitmap {
    val nv21 = toPoseNv21ByteArray()
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val jpegBytes = ByteArrayOutputStream().use { output ->
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 80, output)
        output.toByteArray()
    }
    val bitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
        ?: error("Could not decode CameraX frame.")

    val rotation = imageInfo.rotationDegrees
    if (rotation == 0) return bitmap

    val matrix = Matrix().apply {
        postRotate(rotation.toFloat())
    }
    val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    bitmap.recycle()
    return rotated
}

private fun ImageProxy.toPoseNv21ByteArray(): ByteArray {
    val yPlane = planes[0]
    val uPlane = planes[1]
    val vPlane = planes[2]
    val yBuffer = yPlane.buffer
    val uBuffer = uPlane.buffer
    val vBuffer = vPlane.buffer

    val ySize = width * height
    val chromaWidth = width / 2
    val chromaHeight = height / 2
    val nv21 = ByteArray(ySize + chromaWidth * chromaHeight * 2)

    var outputOffset = 0
    for (row in 0 until height) {
        val rowOffset = row * yPlane.rowStride
        for (col in 0 until width) {
            nv21[outputOffset++] = yBuffer.get(rowOffset + col * yPlane.pixelStride)
        }
    }

    var chromaOffset = ySize
    for (row in 0 until chromaHeight) {
        val uRowOffset = row * uPlane.rowStride
        val vRowOffset = row * vPlane.rowStride
        for (col in 0 until chromaWidth) {
            nv21[chromaOffset++] = vBuffer.get(vRowOffset + col * vPlane.pixelStride)
            nv21[chromaOffset++] = uBuffer.get(uRowOffset + col * uPlane.pixelStride)
        }
    }

    return nv21
}
