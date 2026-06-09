package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier

data class GestureCategory(
    val index: Int,
    val categoryName: String,
    val displayName: String,
    val score: Float,
)

data class GestureRecognitionResult(
    val imageWidth: Int,
    val imageHeight: Int,
    val hands: List<List<HandLandmarkPoint>>,
    val worldLandmarks: List<List<HandWorldLandmarkPoint>>,
    val handednesses: List<List<HandednessCategory>>,
    val gestures: List<List<GestureCategory>>,
    val connections: List<HandLandmarkConnection>,
    val timestampMs: Long,
)

data class GestureVideoResult(
    val durationMs: Long,
    val analyzedFrames: Int,
    val previewFrameBytes: ByteArray?,
    val recognitionResult: GestureRecognitionResult?,
)

interface StaticGestureRecognizer {
    suspend fun recognize(imageBytes: ByteArray): GestureRecognitionResult?
}

interface GestureVideoRecognizer {
    suspend fun recognizeVideo(videoBytes: ByteArray): GestureVideoResult?
}

interface GestureRecognizerEngine {
    val recognitionResult: State<GestureRecognitionResult?>
    val isRunning: State<Boolean>
    val error: State<String?>
    val cameraFacing: State<HandCameraFacing>

    fun start()
    fun stop()
    fun setCameraFacing(facing: HandCameraFacing)
}

@Composable
expect fun rememberStaticGestureRecognizer(): StaticGestureRecognizer

@Composable
expect fun rememberGestureVideoRecognizer(): GestureVideoRecognizer

@Composable
expect fun rememberGestureRecognizerEngine(): GestureRecognizerEngine

@Composable
expect fun GestureRecognizerCameraPreview(
    engine: GestureRecognizerEngine,
    modifier: Modifier = Modifier,
)
