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
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
actual fun rememberStaticObjectDetector(): StaticObjectDetector {
    val context = LocalContext.current
    return remember(context) {
        AndroidStaticObjectDetector(context.applicationContext)
    }
}

@Composable
actual fun rememberObjectDetectorEngine(): ObjectDetectorEngine {
    val context = LocalContext.current
    return remember(context) {
        AndroidObjectDetectorEngine(context.applicationContext)
    }
}

@Composable
actual fun ObjectDetectorCameraPreview(
    engine: ObjectDetectorEngine,
    modifier: Modifier,
) {
    val androidEngine = engine as? AndroidObjectDetectorEngine
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasPermission by rememberObjectDetectorCameraPermissionState()

    if (androidEngine == null) {
        ObjectDetectorUnsupportedPreview(modifier)
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
                text = "Camera permission is required for live object detection.",
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

private class AndroidStaticObjectDetector(
    private val context: Context,
) : StaticObjectDetector {
    private var objectDetector: ObjectDetector? = null
    private val modelFileName = "efficientdet_lite0.tflite"

    private fun initIfNeeded() {
        if (objectDetector != null) return

        try {
            context.assets.open(modelFileName).close()
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelFileName)
                .build()
            val options = ObjectDetector.ObjectDetectorOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setMaxResults(8)
                .setScoreThreshold(0.35f)
                .build()

            objectDetector = ObjectDetector.createFromOptions(context, options)
        } catch (e: FileNotFoundException) {
            throw IllegalStateException("Missing MediaPipe model asset: $modelFileName", e)
        }
    }

    override suspend fun detect(imageBytes: ByteArray): ObjectDetectorResult? =
        withContext(Dispatchers.Default) {
            var bitmap: Bitmap? = null
            try {
                initIfNeeded()
                bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    ?: return@withContext null

                val argbBitmap = if (bitmap.config == Bitmap.Config.ARGB_8888) {
                    bitmap
                } else {
                    bitmap.copy(Bitmap.Config.ARGB_8888, false)
                }
                if (argbBitmap !== bitmap) {
                    bitmap.recycle()
                    bitmap = argbBitmap
                }

                val mpImage = BitmapImageBuilder(argbBitmap).build()
                val result = objectDetector?.detect(mpImage) ?: return@withContext null

                result.toObjectDetectorResult(
                    imageWidth = argbBitmap.width,
                    imageHeight = argbBitmap.height,
                )
            } catch (e: Exception) {
                Log.e("ObjectDetector", "Failed to detect objects", e)
                null
            } finally {
                bitmap?.recycle()
            }
        }
}

private class AndroidObjectDetectorEngine(
    private val context: Context,
) : ObjectDetectorEngine {
    private val modelFileName = "efficientdet_lite0.tflite"
    private val inFlight = AtomicBoolean(false)
    private var objectDetector: ObjectDetector? = null

    internal val cameraExecutor = Executors.newSingleThreadExecutor()

    private val mutableDetectionResult = mutableStateOf<ObjectDetectorResult?>(null)
    private val mutableIsRunning = mutableStateOf(false)
    private val mutableError = mutableStateOf<String?>(null)

    override val detectionResult: State<ObjectDetectorResult?> = mutableDetectionResult
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
            val bitmap = imageProxy.toObjectDetectorUprightBitmap()
            val mpImage = BitmapImageBuilder(bitmap).build()
            objectDetector?.detectAsync(mpImage, imageProxy.imageInfo.timestamp / 1_000_000)
        } catch (e: Exception) {
            inFlight.set(false)
            mutableError.value = "Failed to process camera frame."
            Log.e("ObjectDetector", "Failed to process camera frame", e)
        } finally {
            imageProxy.close()
        }
    }

    private fun initIfNeeded() {
        if (objectDetector != null) return

        try {
            context.assets.open(modelFileName).close()
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelFileName)
                .build()
            val options = ObjectDetector.ObjectDetectorOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setMaxResults(8)
                .setScoreThreshold(0.35f)
                .setResultListener { result, inputImage ->
                    inFlight.set(false)
                    val imageWidth = inputImage.width
                    val imageHeight = inputImage.height
                    inputImage.close()
                    mutableDetectionResult.value = result.toObjectDetectorResult(
                        imageWidth = imageWidth,
                        imageHeight = imageHeight,
                    )
                    mutableError.value = null
                }
                .setErrorListener { error ->
                    inFlight.set(false)
                    mutableError.value = error.message ?: "Object detector error."
                    Log.e("ObjectDetector", "MediaPipe object detector error", error)
                }
                .build()

            objectDetector = ObjectDetector.createFromOptions(context, options)
        } catch (e: FileNotFoundException) {
            mutableError.value = "Missing MediaPipe model asset: $modelFileName"
            throw IllegalStateException("Missing MediaPipe model asset: $modelFileName", e)
        }
    }
}

@Composable
private fun rememberObjectDetectorCameraPermissionState(): State<Boolean> {
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
private fun ObjectDetectorUnsupportedPreview(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111318)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Unsupported object detector engine.",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult.toObjectDetectorResult(
    imageWidth: Int,
    imageHeight: Int,
): ObjectDetectorResult =
    ObjectDetectorResult(
        imageWidth = imageWidth,
        imageHeight = imageHeight,
        detections = detections().map { detection ->
            val rect = detection.boundingBox()
            ObjectDetection(
                boundingBox = ObjectBoundingBox(
                    left = rect.left,
                    top = rect.top,
                    right = rect.right,
                    bottom = rect.bottom,
                ),
                categories = detection.categories().map { category ->
                    ObjectDetectionCategory(
                        index = category.index(),
                        categoryName = category.categoryName(),
                        displayName = category.displayName(),
                        score = category.score(),
                    )
                },
            )
        },
        timestampMs = timestampMs(),
    )

private fun ImageProxy.toObjectDetectorUprightBitmap(): Bitmap {
    val nv21 = toObjectDetectorNv21ByteArray()
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

private fun ImageProxy.toObjectDetectorNv21ByteArray(): ByteArray {
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
