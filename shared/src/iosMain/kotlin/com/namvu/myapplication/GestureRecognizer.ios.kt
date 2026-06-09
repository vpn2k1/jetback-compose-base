package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
actual fun rememberStaticGestureRecognizer(): StaticGestureRecognizer = remember { UnsupportedStaticGestureRecognizer }

@Composable
actual fun rememberGestureVideoRecognizer(): GestureVideoRecognizer = remember { UnsupportedGestureVideoRecognizer }

@Composable
actual fun rememberGestureRecognizerEngine(): GestureRecognizerEngine = remember { UnsupportedGestureRecognizerEngine }

@Composable
actual fun GestureRecognizerCameraPreview(engine: GestureRecognizerEngine, modifier: Modifier) {}

private object UnsupportedStaticGestureRecognizer : StaticGestureRecognizer {
    override suspend fun recognize(imageBytes: ByteArray): GestureRecognitionResult? = null
}

private object UnsupportedGestureVideoRecognizer : GestureVideoRecognizer {
    override suspend fun recognizeVideo(videoBytes: ByteArray): GestureVideoResult? = null
}

private object UnsupportedGestureRecognizerEngine : GestureRecognizerEngine {
    override val recognitionResult: State<GestureRecognitionResult?> = mutableStateOf(null)
    override val isRunning: State<Boolean> = mutableStateOf(false)
    override val error: State<String?> = mutableStateOf("Gesture Recognizer chỉ có Android implementation ở bước này.")
    override val cameraFacing: State<HandCameraFacing> = mutableStateOf(HandCameraFacing.Back)

    override fun start() = Unit
    override fun stop() = Unit
    override fun setCameraFacing(facing: HandCameraFacing) = Unit
}
