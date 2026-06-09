package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier

data class PoseLandmarkPoint(
    val x: Float,
    val y: Float,
    val z: Float,
    val visibility: Float?,
    val presence: Float?,
)

data class PoseLandmarkConnection(
    val start: Int,
    val end: Int,
)

data class PoseTrackingResult(
    val imageWidth: Int,
    val imageHeight: Int,
    val poses: List<List<PoseLandmarkPoint>>,
    val connections: List<PoseLandmarkConnection>,
    val timestampMs: Long,
)

data class PoseVideoResult(
    val durationMs: Long,
    val analyzedFrames: Int,
    val previewFrameBytes: ByteArray?,
    val trackingResult: PoseTrackingResult?,
)

interface PoseVideoLandmarker {
    suspend fun detectVideo(videoBytes: ByteArray): PoseVideoResult?
}

interface PoseLandmarkerEngine {
    val trackingResult: State<PoseTrackingResult?>
    val isRunning: State<Boolean>
    val error: State<String?>

    fun start()
    fun stop()
}

@Composable
expect fun rememberPoseVideoLandmarker(): PoseVideoLandmarker

@Composable
expect fun rememberPoseLandmarkerEngine(): PoseLandmarkerEngine

@Composable
expect fun PoseLandmarkerCameraPreview(
    engine: PoseLandmarkerEngine,
    modifier: Modifier = Modifier,
)

@Composable
expect fun openVideoPicker(result: (ByteArray?) -> Unit): () -> Unit
