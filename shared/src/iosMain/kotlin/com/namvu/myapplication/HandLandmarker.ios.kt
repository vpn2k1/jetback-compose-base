package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
actual fun rememberStaticHandLandmarker(): StaticHandLandmarker = remember { UnsupportedStaticHandLandmarker }

@Composable
actual fun rememberHandVideoLandmarker(): HandVideoLandmarker = remember { UnsupportedHandVideoLandmarker }

@Composable
actual fun rememberHandLandmarkerEngine(): HandLandmarkerEngine = remember { UnsupportedHandLandmarkerEngine }

@Composable
actual fun HandLandmarkerCameraPreview(engine: HandLandmarkerEngine, modifier: Modifier) {}

private object UnsupportedStaticHandLandmarker : StaticHandLandmarker {
    override suspend fun detect(imageBytes: ByteArray): HandTrackingResult? = null
}

private object UnsupportedHandVideoLandmarker : HandVideoLandmarker {
    override suspend fun detectVideo(videoBytes: ByteArray): HandVideoResult? = null
}

private object UnsupportedHandLandmarkerEngine : HandLandmarkerEngine {
    override val trackingResult: State<HandTrackingResult?> = mutableStateOf(null)
    override val isRunning: State<Boolean> = mutableStateOf(false)
    override val error: State<String?> = mutableStateOf("Hand Landmarker chỉ có Android implementation ở bước này.")
    override val cameraFacing: State<HandCameraFacing> = mutableStateOf(HandCameraFacing.Back)

    override fun start() = Unit
    override fun stop() = Unit
    override fun setCameraFacing(facing: HandCameraFacing) = Unit
}
