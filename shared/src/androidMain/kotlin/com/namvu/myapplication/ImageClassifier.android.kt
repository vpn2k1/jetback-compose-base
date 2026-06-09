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
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifierResult as MediaPipeImageClassifierResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
actual fun rememberStaticImageClassifier(): StaticImageClassifier {
    val context = LocalContext.current
    return remember(context) {
        AndroidStaticImageClassifier(context.applicationContext)
    }
}

@Composable
actual fun rememberImageClassifierVideoAnalyzer(): ImageClassifierVideoAnalyzer {
    val context = LocalContext.current
    return remember(context) {
        AndroidImageClassifierVideoAnalyzer(context.applicationContext)
    }
}

@Composable
actual fun rememberImageClassifierEngine(): ImageClassifierEngine {
    val context = LocalContext.current
    return remember(context) {
        AndroidImageClassifierEngine(context.applicationContext)
    }
}

@Composable
actual fun ImageClassifierCameraPreview(
    engine: ImageClassifierEngine,
    modifier: Modifier,
) {
    val androidEngine = engine as? AndroidImageClassifierEngine
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasPermission by rememberImageClassifierCameraPermissionState()
    val cameraFacing by engine.cameraFacing

    if (androidEngine == null) {
        ImageClassifierUnsupportedPreview(modifier)
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
                text = "Camera permission is required for live image classification.",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        return
    }

    val controller = remember(context, androidEngine, cameraFacing) {
        LifecycleCameraController(context).apply {
            cameraSelector = when (cameraFacing) {
                HandCameraFacing.Front -> CameraSelector.DEFAULT_FRONT_CAMERA
                HandCameraFacing.Back -> CameraSelector.DEFAULT_BACK_CAMERA
            }
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

private class AndroidStaticImageClassifier(
    private val context: Context,
) : StaticImageClassifier {
    private val modelFileName = "efficientnet_lite0.tflite"

    override suspend fun classify(imageBytes: ByteArray): ImageClassifierResult? =
        withContext(Dispatchers.Default) {
            var imageClassifier: ImageClassifier? = null
            var bitmap: Bitmap? = null
            try {
                context.assets.open(modelFileName).close()
                bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    ?: return@withContext null

                val argbBitmap = if (bitmap.config == Bitmap.Config.ARGB_8888) {
                    bitmap
                } else {
                    bitmap.copy(Bitmap.Config.ARGB_8888, false)
                }

                val options = ImageClassifier.ImageClassifierOptions.builder()
                    .setBaseOptions(
                        BaseOptions.builder()
                            .setModelAssetPath(modelFileName)
                            .build(),
                    )
                    .setRunningMode(RunningMode.IMAGE)
                    .setMaxResults(5)
                    .build()

                imageClassifier = ImageClassifier.createFromOptions(context, options)
                val mpImage = BitmapImageBuilder(argbBitmap).build()
                val result = imageClassifier.classify(mpImage).toSharedImageClassifierResult(
                    imageWidth = argbBitmap.width,
                    imageHeight = argbBitmap.height,
                )
                mpImage.close()
                if (argbBitmap !== bitmap) argbBitmap.recycle()
                result
            } catch (e: Exception) {
                Log.e("ImageClassifier", "Failed to classify image", e)
                null
            } finally {
                bitmap?.recycle()
                imageClassifier?.close()
            }
        }
}

private class AndroidImageClassifierVideoAnalyzer(
    private val context: Context,
) : ImageClassifierVideoAnalyzer {
    private val modelFileName = "efficientnet_lite0.tflite"

    override suspend fun classifyVideo(videoBytes: ByteArray): ImageClassifierVideoResult? =
        withContext(Dispatchers.Default) {
            val videoFile = File.createTempFile("image_classifier_video", ".mp4", context.cacheDir)
            val retriever = MediaMetadataRetriever()
            var imageClassifier: ImageClassifier? = null
            try {
                context.assets.open(modelFileName).close()
                videoFile.writeBytes(videoBytes)
                retriever.setDataSource(videoFile.absolutePath)
                val durationMs = retriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull()
                    ?: 0L

                val options = ImageClassifier.ImageClassifierOptions.builder()
                    .setBaseOptions(
                        BaseOptions.builder()
                            .setModelAssetPath(modelFileName)
                            .build(),
                    )
                    .setRunningMode(RunningMode.VIDEO)
                    .setMaxResults(5)
                    .build()
                imageClassifier = ImageClassifier.createFromOptions(context, options)

                val sampleCount = 8
                var analyzedFrames = 0
                var previewBytes: ByteArray? = null
                var latestResult: ImageClassifierResult? = null

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
                        previewBytes = argbFrame.toImageClassifierJpegBytes()
                    }

                    val mpImage = BitmapImageBuilder(argbFrame).build()
                    latestResult = imageClassifier.classifyForVideo(mpImage, timestampMs).toSharedImageClassifierResult(
                        imageWidth = argbFrame.width,
                        imageHeight = argbFrame.height,
                    )
                    mpImage.close()

                    if (argbFrame !== frame) argbFrame.recycle()
                    frame.recycle()
                }

                ImageClassifierVideoResult(
                    durationMs = durationMs,
                    analyzedFrames = analyzedFrames,
                    previewFrameBytes = previewBytes,
                    classificationResult = latestResult,
                )
            } catch (e: Exception) {
                Log.e("ImageClassifier", "Failed to classify video", e)
                null
            } finally {
                runCatching { retriever.release() }
                imageClassifier?.close()
                videoFile.delete()
            }
        }
}

private class AndroidImageClassifierEngine(
    private val context: Context,
) : ImageClassifierEngine {
    private val modelFileName = "efficientnet_lite0.tflite"
    private val inFlight = AtomicBoolean(false)
    private var imageClassifier: ImageClassifier? = null

    internal val cameraExecutor = Executors.newSingleThreadExecutor()

    private val mutableClassificationResult = mutableStateOf<ImageClassifierResult?>(null)
    private val mutableIsRunning = mutableStateOf(false)
    private val mutableError = mutableStateOf<String?>(null)
    private val mutableCameraFacing = mutableStateOf(HandCameraFacing.Back)

    override val classificationResult: State<ImageClassifierResult?> = mutableClassificationResult
    override val isRunning: State<Boolean> = mutableIsRunning
    override val error: State<String?> = mutableError
    override val cameraFacing: State<HandCameraFacing> = mutableCameraFacing

    override fun start() {
        mutableIsRunning.value = true
        mutableError.value = null
        initIfNeeded()
    }

    override fun stop() {
        mutableIsRunning.value = false
        inFlight.set(false)
    }

    override fun setCameraFacing(facing: HandCameraFacing) {
        mutableCameraFacing.value = facing
        mutableClassificationResult.value = null
    }

    internal fun analyze(imageProxy: ImageProxy) {
        if (!mutableIsRunning.value || !inFlight.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        try {
            initIfNeeded()
            val bitmap = imageProxy.toImageClassifierUprightBitmap()
            val mpImage = BitmapImageBuilder(bitmap).build()
            imageClassifier?.classifyAsync(mpImage, imageProxy.imageInfo.timestamp / 1_000_000)
        } catch (e: Exception) {
            inFlight.set(false)
            mutableError.value = "Failed to process camera frame."
            Log.e("ImageClassifier", "Failed to process camera frame", e)
        } finally {
            imageProxy.close()
        }
    }

    private fun initIfNeeded() {
        if (imageClassifier != null) return

        try {
            context.assets.open(modelFileName).close()
            val options = ImageClassifier.ImageClassifierOptions.builder()
                .setBaseOptions(
                    BaseOptions.builder()
                        .setModelAssetPath(modelFileName)
                        .build(),
                )
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setMaxResults(5)
                .setResultListener { result, inputImage ->
                    inFlight.set(false)
                    val imageWidth = inputImage.width
                    val imageHeight = inputImage.height
                    inputImage.close()
                    mutableClassificationResult.value = result.toSharedImageClassifierResult(
                        imageWidth = imageWidth,
                        imageHeight = imageHeight,
                    )
                    mutableError.value = null
                }
                .setErrorListener { error ->
                    inFlight.set(false)
                    mutableError.value = error.message ?: "Image classifier error."
                    Log.e("ImageClassifier", "MediaPipe image classifier error", error)
                }
                .build()

            imageClassifier = ImageClassifier.createFromOptions(context, options)
        } catch (e: FileNotFoundException) {
            mutableError.value = "Missing MediaPipe model asset: $modelFileName"
            throw IllegalStateException("Missing MediaPipe model asset: $modelFileName", e)
        }
    }
}

@Composable
private fun rememberImageClassifierCameraPermissionState(): State<Boolean> {
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
private fun ImageClassifierUnsupportedPreview(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111318)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Unsupported image classifier engine.",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun MediaPipeImageClassifierResult.toSharedImageClassifierResult(
    imageWidth: Int,
    imageHeight: Int,
): ImageClassifierResult =
    ImageClassifierResult(
        categories = classificationResult()
            .classifications()
            .flatMap { it.categories() }
            .sortedByDescending { it.score() }
            .map { category ->
                ImageClassificationCategory(
                    index = category.index(),
                    categoryName = category.categoryName(),
                    displayName = category.displayName(),
                    score = category.score(),
                )
            },
        timestampMs = timestampMs(),
        imageWidth = imageWidth,
        imageHeight = imageHeight,
    )

private fun Bitmap.toImageClassifierJpegBytes(): ByteArray =
    ByteArrayOutputStream().use { output ->
        compress(Bitmap.CompressFormat.JPEG, 85, output)
        output.toByteArray()
    }

private fun ImageProxy.toImageClassifierUprightBitmap(): Bitmap {
    val nv21 = toImageClassifierNv21ByteArray()
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

private fun ImageProxy.toImageClassifierNv21ByteArray(): ByteArray {
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
