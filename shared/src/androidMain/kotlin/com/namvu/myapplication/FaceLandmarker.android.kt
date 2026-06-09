package com.namvu.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

@Composable
actual fun rememberStaticFaceLandmarker(): StaticFaceLandmarker {
    val context = LocalContext.current
    return remember(context) {
        AndroidStaticFaceLandmarker(context.applicationContext)
    }
}

@Composable
actual fun rememberFaceVideoLandmarker(): FaceVideoLandmarker {
    val context = LocalContext.current
    return remember(context) {
        AndroidFaceVideoLandmarker(context.applicationContext)
    }
}

private class AndroidStaticFaceLandmarker(
    private val context: Context,
) : StaticFaceLandmarker {
    private var faceLandmarker: FaceLandmarker? = null
    private val modelFileName = "face_landmarker.task"

    private fun initIfNeeded() {
        if (faceLandmarker != null) return

        try {
            context.assets.open(modelFileName).close()

            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelFileName)
                .build()
            val options = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setNumFaces(1)
                .setMinFaceDetectionConfidence(0.5f)
                .setMinFacePresenceConfidence(0.5f)
                .setOutputFaceBlendshapes(true)
                .setOutputFacialTransformationMatrixes(true)
                .build()

            faceLandmarker = FaceLandmarker.createFromOptions(context, options)
        } catch (e: FileNotFoundException) {
            throw IllegalStateException("Missing MediaPipe model asset: $modelFileName", e)
        }
    }

    override suspend fun detect(imageBytes: ByteArray): FaceLandmarkerOutput? =
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
                val result = faceLandmarker?.detect(mpImage) ?: return@withContext null
                val faces = result.faceLandmarks().map { landmarks ->
                    landmarks.map { landmark ->
                        FaceLandmarkPoint(
                            x = landmark.x(),
                            y = landmark.y(),
                            z = landmark.z(),
                        )
                    }
                }
                val facialTransformationMatrixes = result.facialTransformationMatrixes()
                    .orElse(emptyList())
                    .mapNotNull { matrix ->
                        matrix
                            .takeIf { it.size == FaceTransformationMatrix.MatrixValueCount }
                            ?.let { FaceTransformationMatrix(it.toList()) }
                    }
                val blendShapes = result.faceBlendshapes()
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

                FaceLandmarkerOutput(
                    imageWidth = argbBitmap.width,
                    imageHeight = argbBitmap.height,
                    faces = faces,
                    blendShapes = blendShapes,
                    facialTransformationMatrixes = facialTransformationMatrixes,
                    connections = FaceLandmarker.FACE_LANDMARKS_CONNECTORS.map { connection ->
                        FaceLandmarkConnection(
                            start = connection.start(),
                            end = connection.end(),
                        )
                    },
                )
            } catch (e: Exception) {
                Log.e("FaceLandmarker", "Failed to detect face landmarks", e)
                null
            } finally {
                bitmap?.recycle()
            }
        }
}

private class AndroidFaceVideoLandmarker(
    private val context: Context,
) : FaceVideoLandmarker {
    private val modelFileName = "face_landmarker.task"

    override suspend fun detectVideo(videoBytes: ByteArray): FaceVideoResult? =
        withContext(Dispatchers.Default) {
            val videoFile = File.createTempFile("face_video", ".mp4", context.cacheDir)
            val retriever = MediaMetadataRetriever()
            var faceLandmarker: FaceLandmarker? = null
            try {
                context.assets.open(modelFileName).close()
                videoFile.writeBytes(videoBytes)
                retriever.setDataSource(videoFile.absolutePath)
                val durationMs = retriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull()
                    ?: 0L

                val options = FaceLandmarker.FaceLandmarkerOptions.builder()
                    .setBaseOptions(
                        BaseOptions.builder()
                            .setModelAssetPath(modelFileName)
                            .build(),
                    )
                    .setRunningMode(RunningMode.VIDEO)
                    .setNumFaces(1)
                    .setMinFaceDetectionConfidence(0.5f)
                    .setMinFacePresenceConfidence(0.5f)
                    .setMinTrackingConfidence(0.5f)
                    .setOutputFaceBlendshapes(true)
                    .setOutputFacialTransformationMatrixes(true)
                    .build()
                faceLandmarker = FaceLandmarker.createFromOptions(context, options)

                val sampleCount = 8
                var analyzedFrames = 0
                var previewBytes: ByteArray? = null
                var latestResult: FaceTrackingResult? = null

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
                        previewBytes = argbFrame.toFaceJpegBytes()
                    }

                    val mpImage = BitmapImageBuilder(argbFrame).build()
                    latestResult = faceLandmarker.detectForVideo(mpImage, timestampMs).toFaceTrackingResult(
                        imageWidth = argbFrame.width,
                        imageHeight = argbFrame.height,
                    )
                    mpImage.close()

                    if (argbFrame !== frame) argbFrame.recycle()
                    frame.recycle()
                }

                FaceVideoResult(
                    durationMs = durationMs,
                    analyzedFrames = analyzedFrames,
                    previewFrameBytes = previewBytes,
                    trackingResult = latestResult,
                )
            } catch (e: Exception) {
                Log.e("FaceLandmarker", "Failed to detect face landmarks in video", e)
                null
            } finally {
                runCatching { retriever.release() }
                faceLandmarker?.close()
                videoFile.delete()
            }
        }
}

private fun FaceLandmarkerResult.toFaceTrackingResult(
    imageWidth: Int,
    imageHeight: Int,
): FaceTrackingResult =
    FaceTrackingResult(
        imageWidth = imageWidth,
        imageHeight = imageHeight,
        faces = faceLandmarks().map { landmarks ->
            landmarks.map { landmark ->
                FaceLandmarkPoint(
                    x = landmark.x(),
                    y = landmark.y(),
                    z = landmark.z(),
                )
            }
        },
        blendShapes = faceBlendshapes()
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
            },
        facialTransformationMatrixes = facialTransformationMatrixes()
            .orElse(emptyList())
            .mapNotNull { matrix ->
                matrix
                    .takeIf { it.size == FaceTransformationMatrix.MatrixValueCount }
                    ?.toList()
                    ?.let(::FaceTransformationMatrix)
            },
        connections = FaceLandmarker.FACE_LANDMARKS_CONNECTORS.map { connection ->
            FaceLandmarkConnection(
                start = connection.start(),
                end = connection.end(),
            )
        },
        timestampMs = timestampMs(),
    )

private fun Bitmap.toFaceJpegBytes(): ByteArray =
    ByteArrayOutputStream().use { output ->
        compress(Bitmap.CompressFormat.JPEG, 85, output)
        output.toByteArray()
    }
