package com.namvu.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.ByteBufferExtractor
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenter as MPImageSegmenter
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenter.ImageSegmenterOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.nio.ByteBuffer

@Composable
actual fun rememberImageSegmenter(): ImageSegmenter {
    val context = LocalContext.current
    return remember(context) {
        AndroidImageSegmenter(context.applicationContext)
    }
}

class AndroidImageSegmenter(private val context: Context) : ImageSegmenter {
    private var mpImageSegmenter: MPImageSegmenter? = null
    private val modelFileName = "deeplabv3.tflite" // Sử dụng model vừa tải về từ tác vụ Gradle

    private fun initSegmenterIfNeeded() {
        if (mpImageSegmenter != null) return
        try {
            context.assets.open(modelFileName).close() // Check file tồn tại chủ động

            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelFileName)
                .build()
            val options = ImageSegmenterOptions.builder()
                .setBaseOptions(baseOptions)
                .setOutputCategoryMask(true)
                .build()

            mpImageSegmenter = MPImageSegmenter.createFromOptions(context, options)
            Log.d("MediaPipeComplete", "MediaPipe khởi tạo thành công luồng nền.")
        } catch (e: FileNotFoundException) {
            throw IllegalStateException("Lỗi: Không tìm thấy file model $modelFileName trong Assets.", e)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun replaceBackground(
        originalImageBytes: ByteArray,
        bgImageBytes: ByteArray
    ): ByteArray? = withContext(Dispatchers.Default) {
        var rawOriginalBitmap: Bitmap? = null
        var rawBackgroundBitmap: Bitmap? = null
        var scaledOriginalBitmap: Bitmap? = null
        var scaledBackgroundBitmap: Bitmap? = null
        var outputBitmap: Bitmap? = null

        try {
            initSegmenterIfNeeded()
            if (mpImageSegmenter == null) return@withContext null

            rawOriginalBitmap = BitmapFactory.decodeByteArray(originalImageBytes, 0, originalImageBytes.size) ?: return@withContext null
            // Giới hạn độ phân giải tối đa 1024px để tránh lỗi tràn bộ nhớ RAM (OOM)
            scaledOriginalBitmap = scaleBitmapIfNeeded(rawOriginalBitmap, maxDimension = 1024)

            val width = scaledOriginalBitmap.width
            val height = scaledOriginalBitmap.height

            rawBackgroundBitmap = BitmapFactory.decodeByteArray(bgImageBytes, 0, bgImageBytes.size) ?: return@withContext null
            scaledBackgroundBitmap = createCenterCropBitmap(rawBackgroundBitmap, width, height)

            // Tiến hành Segmentation qua MediaPipe
            val mpImage = BitmapImageBuilder(scaledOriginalBitmap).build()
            val segmentResult = mpImageSegmenter?.segment(mpImage) ?: return@withContext null
            val categoryMask = segmentResult.categoryMask().orElse(null) ?: return@withContext null

            val maskBuffer: ByteBuffer = ByteBufferExtractor.extract(categoryMask)
            maskBuffer.rewind()

            val originalPixels = IntArray(width * height)
            val backgroundPixels = IntArray(width * height)
            val outputPixels = IntArray(width * height)

            scaledOriginalBitmap.getPixels(originalPixels, 0, width, 0, 0, width, height)
            scaledBackgroundBitmap.getPixels(backgroundPixels, 0, width, 0, 0, width, height)

            // Logic trộn ảnh: Đối với DeepLabV3, giá trị mask > 0 biểu thị vật thể/người, bằng 0 biểu thị nền phẳng
            for (i in 0 until width * height) {
                val maskValue = maskBuffer.get().toInt()
                if (maskValue > 0) {
                    outputPixels[i] = originalPixels[i]
                } else {
                    outputPixels[i] = backgroundPixels[i]
                }
            }

            outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            outputBitmap.setPixels(outputPixels, 0, width, 0, 0, width, height)

            val outputStream = ByteArrayOutputStream()
            outputBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            return@withContext outputStream.toByteArray()

        } catch (e: Exception) {
            Log.e("MediaPipeComplete", "Lỗi xử lý hình ảnh: ", e)
            return@withContext null
        } finally {
            // Dọn dẹp dứt điểm vùng nhớ tránh rò rỉ RAM (Memory Leak)
            rawOriginalBitmap?.recycle()
            rawBackgroundBitmap?.recycle()
            if (scaledOriginalBitmap != rawOriginalBitmap) scaledOriginalBitmap?.recycle()
            if (scaledBackgroundBitmap != rawBackgroundBitmap) scaledBackgroundBitmap?.recycle()
            outputBitmap?.recycle()
        }
    }

    override suspend fun replaceBackgroundWithColor(
        originalImageBytes: ByteArray,
        backgroundColorArgb: Int,
    ): ByteArray? = withContext(Dispatchers.Default) {
        val backgroundBitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
        try {
            backgroundBitmap.eraseColor(backgroundColorArgb)
            val outputStream = ByteArrayOutputStream()
            backgroundBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            replaceBackground(originalImageBytes, outputStream.toByteArray())
        } finally {
            backgroundBitmap.recycle()
        }
    }

    private fun scaleBitmapIfNeeded(src: Bitmap, maxDimension: Int): Bitmap {
        if (src.width <= maxDimension && src.height <= maxDimension) return src
        val ratio = src.width.toFloat() / src.height.toFloat()
        val newWidth = if (src.width > src.height) maxDimension else (maxDimension * ratio).toInt()
        val newHeight = if (src.width > src.height) (maxDimension / ratio).toInt() else maxDimension
        return Bitmap.createScaledBitmap(src, newWidth, newHeight, true)
    }

    private fun createCenterCropBitmap(src: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
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
}
