package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
actual fun rememberStaticImageClassifier(): StaticImageClassifier = remember { UnsupportedStaticImageClassifier }

@Composable
actual fun rememberImageClassifierVideoAnalyzer(): ImageClassifierVideoAnalyzer = remember { UnsupportedImageClassifierVideoAnalyzer }

@Composable
actual fun rememberImageClassifierEngine(): ImageClassifierEngine = remember { UnsupportedImageClassifierEngine }

@Composable
actual fun ImageClassifierCameraPreview(engine: ImageClassifierEngine, modifier: Modifier) {}

private object UnsupportedStaticImageClassifier : StaticImageClassifier {
    override suspend fun classify(imageBytes: ByteArray): ImageClassifierResult? = null
}

private object UnsupportedImageClassifierVideoAnalyzer : ImageClassifierVideoAnalyzer {
    override suspend fun classifyVideo(videoBytes: ByteArray): ImageClassifierVideoResult? = null
}

private object UnsupportedImageClassifierEngine : ImageClassifierEngine {
    override val classificationResult: State<ImageClassifierResult?> = mutableStateOf(null)
    override val isRunning: State<Boolean> = mutableStateOf(false)
    override val error: State<String?> = mutableStateOf("Image Classifier chỉ có Android implementation ở bước này.")
    override val cameraFacing: State<HandCameraFacing> = mutableStateOf(HandCameraFacing.Back)

    override fun start() = Unit
    override fun stop() = Unit
    override fun setCameraFacing(facing: HandCameraFacing) = Unit
}
