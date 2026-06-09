package com.namvu.myapplication

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class MediaPipeEditorController(
    initialState: MediaPipeEditorState = MediaPipeEditorState(),
) {
    private val mutableState = mutableStateOf(initialState)

    val state: State<MediaPipeEditorState> = mutableState

    fun selectFeature(feature: MediaPipeFeature) {
        mutableState.value = mutableState.value.copy(
            selectedFeature = feature,
            errorMessage = null,
        )
    }

    fun setCameraRunning(running: Boolean) {
        mutableState.value = mutableState.value.copy(
            isCameraRunning = running,
        )
    }

    fun setError(message: String?) {
        mutableState.value = mutableState.value.copy(
            errorMessage = message,
        )
    }
}
