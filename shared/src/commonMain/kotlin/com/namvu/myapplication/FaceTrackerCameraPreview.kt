package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun FaceTrackerCameraPreview(
    engine: FaceTrackerEngine,
    modifier: Modifier = Modifier,
)
