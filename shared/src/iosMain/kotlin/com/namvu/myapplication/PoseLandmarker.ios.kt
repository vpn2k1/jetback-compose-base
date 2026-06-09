package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
actual fun rememberPoseVideoLandmarker(): PoseVideoLandmarker = remember { UnsupportedPoseVideoLandmarker }

@Composable
actual fun rememberPoseLandmarkerEngine(): PoseLandmarkerEngine = remember { UnsupportedPoseLandmarkerEngine }

@Composable
actual fun PoseLandmarkerCameraPreview(engine: PoseLandmarkerEngine, modifier: Modifier) {}

@Composable
actual fun openVideoPicker(result: (ByteArray?) -> Unit): () -> Unit = { result(null) }

private object UnsupportedPoseVideoLandmarker : PoseVideoLandmarker {
    override suspend fun detectVideo(videoBytes: ByteArray): PoseVideoResult? = null
}

private object UnsupportedPoseLandmarkerEngine : PoseLandmarkerEngine {
    override val trackingResult: State<PoseTrackingResult?> = mutableStateOf(null)
    override val isRunning: State<Boolean> = mutableStateOf(false)
    override val error: State<String?> = mutableStateOf("Pose Landmarker is only implemented on Android.")
    override fun start() = Unit
    override fun stop() = Unit
}
