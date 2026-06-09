package com.namvu.myapplication

data class MediaPipeEditorState(
    val selectedFeature: MediaPipeFeature = MediaPipeFeature.Background,
    val isCameraRunning: Boolean = false,
    val errorMessage: String? = null,
)
