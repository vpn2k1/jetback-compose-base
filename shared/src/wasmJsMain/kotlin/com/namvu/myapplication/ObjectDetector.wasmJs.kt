package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
actual fun rememberStaticObjectDetector(): StaticObjectDetector = remember {
    UnsupportedStaticObjectDetector
}

private object UnsupportedStaticObjectDetector : StaticObjectDetector {
    override suspend fun detect(imageBytes: ByteArray): ObjectDetectorResult? = null
}

@Composable
actual fun rememberObjectDetectorEngine(): ObjectDetectorEngine = remember {
    UnsupportedObjectDetectorEngine
}

@Composable
actual fun ObjectDetectorCameraPreview(
    engine: ObjectDetectorEngine,
    modifier: Modifier,
) {
}

private object UnsupportedObjectDetectorEngine : ObjectDetectorEngine {
    override val detectionResult: State<ObjectDetectorResult?> = mutableStateOf(null)
    override val isRunning: State<Boolean> = mutableStateOf(false)
    override val error: State<String?> = mutableStateOf("Live object detection is only implemented on Android.")

    override fun start() = Unit
    override fun stop() = Unit
}
