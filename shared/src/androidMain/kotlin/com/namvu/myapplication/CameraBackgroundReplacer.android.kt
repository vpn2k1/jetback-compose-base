package com.namvu.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mediapipe.framework.image.BitmapExtractor
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.ByteBufferExtractor
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.nio.ByteBuffer
import androidx.core.graphics.createBitmap

@Composable
actual fun CameraBackgroundReplacer(
    modifier: Modifier,
    backgroundImageBytes: ByteArray?,
    backgroundColorArgb: Int,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasPermission by rememberSegmenterCameraPermissionState()

    if (!hasPermission) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color(0xFF111318)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Camera permission is required for live segmentation.",
                color = androidx.compose.ui.graphics.Color.White,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        return
    }

    var processedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val rawBackgroundBitmap = remember(backgroundImageBytes) {
        backgroundImageBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    }
    val imageSegmenter = remember {
        val baseOptions = BaseOptions.builder().setModelAssetPath("deeplabv3.tflite").build()
        val options = ImageSegmenter.ImageSegmenterOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setOutputCategoryMask(true)
            .setResultListener { segmentResult, inputMPImage ->
                val categoryMask =
                    segmentResult.categoryMask().orElse(null) ?: return@setResultListener
                val maskBuffer: ByteBuffer = ByteBufferExtractor.extract(categoryMask)
                maskBuffer.rewind()
                val maskWidth = categoryMask.width
                val maskHeight = categoryMask.height

                val originalBitmap = BitmapExtractor.extract(inputMPImage)

                // Tạo ảnh nền khớp kích thước khung hình
                val scaledBgBitmap = rawBackgroundBitmap?.let {
                    createCenterCropBitmap(it, maskWidth, maskHeight)
                }

                val originalPixels = IntArray(maskWidth * maskHeight)
                val backgroundPixels = IntArray(maskWidth * maskHeight)
                val outputPixels = IntArray(maskWidth * maskHeight)

                originalBitmap.getPixels(originalPixels, 0, maskWidth, 0, 0, maskWidth, maskHeight)
                scaledBgBitmap?.getPixels(
                    backgroundPixels,
                    0,
                    maskWidth,
                    0,
                    0,
                    maskWidth,
                    maskHeight
                )

                // Trộn Pixel thời gian thực
                for (i in 0 until maskWidth * maskHeight) {
                    if (maskBuffer.hasRemaining()) {
                        val maskValue = maskBuffer.get().toInt()
                        if (maskValue > 0) {
                            outputPixels[i] = originalPixels[i] // Giữ người
                        } else {
                            outputPixels[i] = if (scaledBgBitmap != null) {
                                backgroundPixels[i]
                            } else {
                                backgroundColorArgb
                            }
                        }
                    }
                }

                val outputBmp = createBitmap(maskWidth, maskHeight)
                outputBmp.setPixels(outputPixels, 0, maskWidth, 0, 0, maskWidth, maskHeight)

                // Đẩy bitmap đã xử lý về UI Thread để hiển thị
                processedBitmap = outputBmp
                scaledBgBitmap?.recycle()
            }
            .setErrorListener { error ->
                Log.e("CameraMediaPipe", "AI Stream Error: ${error.message}")
            }
            .build()
        ImageSegmenter.createFromOptions(context, options)
    }
    LaunchedEffect(rawBackgroundBitmap) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        // Cấu hình bộ phân tích hình ảnh (ImageAnalysis)
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // Tránh đơ camera, chỉ lấy frame mới nhất
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888) // Định dạng chuẩn để chuyển đổi sang Bitmap
            .build()

        imageAnalysis.setAnalyzer(Dispatchers.Default.asExecutor()) { imageProxy ->
            val timestamp = imageProxy.imageInfo.timestamp

            // Chuyển ImageProxy của CameraX thành Bitmap
            val bitmap = imageProxy.toBitmap()

            // Xoay ngang dọc bitmap cho đúng hướng camera điện thoại (thường Camera trước bị xoay 270 độ)
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val correctedBitmap = rotateBitmap(bitmap, rotationDegrees.toFloat())

            // Đẩy frame vào MediaPipe xử lý bất đồng bộ
            val mpImage = BitmapImageBuilder(correctedBitmap).build()
            imageSegmenter.segmentAsync(mpImage, timestamp)

            imageProxy.close() // Bắt buộc phải đóng để nhận frame tiếp theo
        }

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA // Sử dụng camera trước (Selfie)

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e("CameraX", "Binding failed", e)
        }
    }

    processedBitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Live Video Camera Thay Nền",
            modifier = modifier.fillMaxSize()
        )
    } ?: Box(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color(0xFF111318)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Starting live segmentation...",
            color = androidx.compose.ui.graphics.Color.White,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun rememberSegmenterCameraPermissionState(): State<Boolean> {
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
fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    // Nếu dùng camera trước, bạn có thể lật gương bằng cách thêm: matrix.postScale(-1f, 1f)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}

fun createCenterCropBitmap(src: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
    val targetBitmap = createBitmap(targetWidth, targetHeight)
    val canvas = Canvas(targetBitmap)
    val srcRatio = src.width.toFloat() / src.height.toFloat()
    val targetRatio = targetWidth.toFloat() / targetHeight.toFloat()
    val drawRect = if (srcRatio > targetRatio) {
        val finalWidth = (src.height * targetRatio).toInt()
        val left = (src.width - finalWidth) / 2
        android.graphics.Rect(left, 0, left + finalWidth, src.height)
    } else {
        val finalHeight = (src.width / targetRatio).toInt()
        val top = (src.height - finalHeight) / 2
        android.graphics.Rect(0, top, src.width, top + finalHeight)
    }
    canvas.drawBitmap(src, drawRect, android.graphics.Rect(0, 0, targetWidth, targetHeight), null)
    return targetBitmap
}
