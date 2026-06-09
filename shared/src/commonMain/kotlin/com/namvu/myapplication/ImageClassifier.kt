package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier

data class ImageClassificationCategory(
    val index: Int,
    val categoryName: String,
    val displayName: String,
    val score: Float,
)

data class ImageClassifierResult(
    val categories: List<ImageClassificationCategory>,
    val timestampMs: Long,
    val imageWidth: Int = 0,
    val imageHeight: Int = 0,
)

data class ImageClassifierVideoResult(
    val durationMs: Long,
    val analyzedFrames: Int,
    val previewFrameBytes: ByteArray?,
    val classificationResult: ImageClassifierResult?,
)

interface StaticImageClassifier {
    suspend fun classify(imageBytes: ByteArray): ImageClassifierResult?
}

interface ImageClassifierVideoAnalyzer {
    suspend fun classifyVideo(videoBytes: ByteArray): ImageClassifierVideoResult?
}

interface ImageClassifierEngine {
    val classificationResult: State<ImageClassifierResult?>
    val isRunning: State<Boolean>
    val error: State<String?>
    val cameraFacing: State<HandCameraFacing>

    fun start()
    fun stop()
    fun setCameraFacing(facing: HandCameraFacing)
}

@Composable
expect fun rememberStaticImageClassifier(): StaticImageClassifier

@Composable
expect fun rememberImageClassifierVideoAnalyzer(): ImageClassifierVideoAnalyzer

@Composable
expect fun rememberImageClassifierEngine(): ImageClassifierEngine

@Composable
expect fun ImageClassifierCameraPreview(
    engine: ImageClassifierEngine,
    modifier: Modifier = Modifier,
)
