package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier

enum class HandCameraFacing {
    Front,
    Back,
}

data class HandLandmarkPoint(
    val x: Float,
    val y: Float,
    val z: Float,
    val visibility: Float?,
    val presence: Float?,
)

data class HandWorldLandmarkPoint(
    val x: Float,
    val y: Float,
    val z: Float,
    val visibility: Float?,
    val presence: Float?,
)

data class HandLandmarkConnection(
    val start: Int,
    val end: Int,
)

data class HandednessCategory(
    val index: Int,
    val categoryName: String,
    val displayName: String,
    val score: Float,
)

data class HandTrackingResult(
    val imageWidth: Int,
    val imageHeight: Int,
    val hands: List<List<HandLandmarkPoint>>,
    val worldLandmarks: List<List<HandWorldLandmarkPoint>>,
    val handednesses: List<List<HandednessCategory>>,
    val connections: List<HandLandmarkConnection>,
    val timestampMs: Long,
)

data class HandVideoResult(
    val durationMs: Long,
    val analyzedFrames: Int,
    val previewFrameBytes: ByteArray?,
    val trackingResult: HandTrackingResult?,
)

interface StaticHandLandmarker {
    suspend fun detect(imageBytes: ByteArray): HandTrackingResult?
}

interface HandVideoLandmarker {
    suspend fun detectVideo(videoBytes: ByteArray): HandVideoResult?
}

interface HandLandmarkerEngine {
    val trackingResult: State<HandTrackingResult?>
    val isRunning: State<Boolean>
    val error: State<String?>
    val cameraFacing: State<HandCameraFacing>

    fun start()
    fun stop()
    fun setCameraFacing(facing: HandCameraFacing)
}

@Composable
expect fun rememberStaticHandLandmarker(): StaticHandLandmarker

@Composable
expect fun rememberHandVideoLandmarker(): HandVideoLandmarker

@Composable
expect fun rememberHandLandmarkerEngine(): HandLandmarkerEngine

@Composable
expect fun HandLandmarkerCameraPreview(
    engine: HandLandmarkerEngine,
    modifier: Modifier = Modifier,
)
