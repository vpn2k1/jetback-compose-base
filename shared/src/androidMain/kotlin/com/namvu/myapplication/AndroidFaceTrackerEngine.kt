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
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
actual fun rememberFaceTrackerEngine(): FaceTrackerEngine {
    val context = LocalContext.current
    return remember(context) {
        AndroidFaceTrackerEngine(context.applicationContext)
    }
}

@Composable
actual fun FaceTrackerCameraPreview(
    engine: FaceTrackerEngine,
    modifier: Modifier,
) {
    val androidEngine = engine as? AndroidFaceTrackerEngine
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasPermission by rememberCameraPermissionState()

    if (androidEngine == null) {
        UnsupportedPreview(modifier)
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
                text = "Camera permission is required for live face tracking.",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        return
    }

    val controller = remember(context, androidEngine) {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
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

class AndroidFaceTrackerEngine(
    private val context: Context,
) : FaceTrackerEngine {
    private val modelFileName = "face_landmarker.task"
    private val inFlight = AtomicBoolean(false)
    private var faceLandmarker: FaceLandmarker? = null

    internal val cameraExecutor = Executors.newSingleThreadExecutor()

    private val mutableTrackingResult = mutableStateOf<FaceTrackingResult?>(null)
    private val mutableIsRunning = mutableStateOf(false)
    private val mutableError = mutableStateOf<String?>(null)

    override val trackingResult: State<FaceTrackingResult?> = mutableTrackingResult
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
            val bitmap = imageProxy.toUprightBitmap()
            val mpImage = BitmapImageBuilder(bitmap).build()
            faceLandmarker?.detectAsync(mpImage, imageProxy.imageInfo.timestamp / 1_000_000)
        } catch (e: Exception) {
            inFlight.set(false)
            mutableError.value = "Failed to process camera frame."
            Log.e("AndroidFaceTracker", "Failed to process camera frame", e)
        } finally {
            imageProxy.close()
        }
    }

    private fun initIfNeeded() {
        if (faceLandmarker != null) return

        try {
            context.assets.open(modelFileName).close()
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelFileName)
                .build()

            val options = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setNumFaces(1)
                .setMinFaceDetectionConfidence(0.5f)
                .setMinFacePresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setOutputFaceBlendshapes(true)
                .setOutputFacialTransformationMatrixes(true)
                .setResultListener { result, inputImage ->
                    inFlight.set(false)
                    val imageWidth = inputImage.width
                    val imageHeight = inputImage.height
                    inputImage.close()
                    mutableTrackingResult.value = result.toFaceTrackingResult(
                        imageWidth = imageWidth,
                        imageHeight = imageHeight,
                        mirrorX = true,
                    )
                    mutableError.value = null
                }
                .setErrorListener { error ->
                    inFlight.set(false)
                    mutableError.value = error.message ?: "Face tracker error."
                    Log.e("AndroidFaceTracker", "MediaPipe face tracker error", error)
                }
                .build()

            faceLandmarker = FaceLandmarker.createFromOptions(context, options)
        } catch (e: FileNotFoundException) {
            mutableError.value = "Missing MediaPipe model asset: $modelFileName"
            throw IllegalStateException("Missing MediaPipe model asset: $modelFileName", e)
        }
    }
}

@Composable
private fun rememberCameraPermissionState(): State<Boolean> {
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
private fun UnsupportedPreview(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111318)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Unsupported face tracker engine.",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun FaceLandmarkerResult.toFaceTrackingResult(
    imageWidth: Int,
    imageHeight: Int,
    mirrorX: Boolean,
): FaceTrackingResult {
    val faces = faceLandmarks().map { landmarks ->
        landmarks.map { landmark ->
            FaceLandmarkPoint(
                x = if (mirrorX) 1f - landmark.x() else landmark.x(),
                y = landmark.y(),
                z = landmark.z(),
            )
        }
    }
    val blendShapes = faceBlendshapes()
        .orElse(emptyList())
        .map { categories ->
            categories.map { category ->
                FaceBlendShape(
                    index = category.index(),
                    categoryName = category.categoryName(),
                    displayName = category.displayName(),
                    score = category.score(),
                )
            }
        }
    val matrices = facialTransformationMatrixes()
        .orElse(emptyList())
        .mapNotNull { matrix ->
            matrix
                .takeIf { it.size == FaceTransformationMatrix.MatrixValueCount }
                ?.toList()
                ?.let(::FaceTransformationMatrix)
        }

    return FaceTrackingResult(
        imageWidth = imageWidth,
        imageHeight = imageHeight,
        faces = faces,
        blendShapes = blendShapes,
        facialTransformationMatrixes = matrices,
        connections = FaceLandmarker.FACE_LANDMARKS_CONNECTORS.map { connection ->
            FaceLandmarkConnection(
                start = connection.start(),
                end = connection.end(),
            )
        },
        timestampMs = timestampMs(),
    )
}

private fun ImageProxy.toUprightBitmap(): Bitmap {
    val nv21 = toNv21ByteArray()
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

private fun ImageProxy.toNv21ByteArray(): ByteArray {
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
