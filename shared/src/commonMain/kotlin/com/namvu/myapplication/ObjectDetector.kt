package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier

data class ObjectBoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
)

data class ObjectDetectionCategory(
    val index: Int,
    val categoryName: String,
    val displayName: String,
    val score: Float,
)

data class ObjectDetection(
    val boundingBox: ObjectBoundingBox,
    val categories: List<ObjectDetectionCategory>,
)

data class ObjectDetectorResult(
    val imageWidth: Int,
    val imageHeight: Int,
    val detections: List<ObjectDetection>,
    val timestampMs: Long,
)

interface StaticObjectDetector {
    suspend fun detect(imageBytes: ByteArray): ObjectDetectorResult?
}

interface ObjectDetectorEngine {
    val detectionResult: State<ObjectDetectorResult?>
    val isRunning: State<Boolean>
    val error: State<String?>

    fun start()
    fun stop()
}

@Composable
expect fun rememberStaticObjectDetector(): StaticObjectDetector

@Composable
expect fun rememberObjectDetectorEngine(): ObjectDetectorEngine

@Composable
expect fun ObjectDetectorCameraPreview(
    engine: ObjectDetectorEngine,
    modifier: Modifier = Modifier,
)
