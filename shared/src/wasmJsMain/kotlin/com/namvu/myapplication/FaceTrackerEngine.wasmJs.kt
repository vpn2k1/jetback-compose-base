package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
actual fun rememberFaceTrackerEngine(): FaceTrackerEngine = remember {
    UnsupportedFaceTrackerEngine
}

@Composable
actual fun FaceTrackerCameraPreview(
    engine: FaceTrackerEngine,
    modifier: Modifier,
) {
}

private object UnsupportedFaceTrackerEngine : FaceTrackerEngine {
    override val trackingResult: State<FaceTrackingResult?> = mutableStateOf(null)
    override val isRunning: State<Boolean> = mutableStateOf(false)
    override val error: State<String?> = mutableStateOf("Live face tracking is only implemented on Android.")

    override fun start() = Unit
    override fun stop() = Unit
}
