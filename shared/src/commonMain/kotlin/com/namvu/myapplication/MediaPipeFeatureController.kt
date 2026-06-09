package com.namvu.myapplication

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class MediaPipeFeatureController(
    initialState: MediaPipeFeatureListState = MediaPipeFeatureListState(),
) {
    private val mutableState = mutableStateOf(initialState)

    val state: State<MediaPipeFeatureListState> = mutableState

    fun selectFeature(feature: MediaPipeFeatureDefinition) {
        mutableState.value = mutableState.value.copy(
            selectedFeatureId = feature.id,
        )
    }

    fun openFeature(feature: MediaPipeFeatureDefinition) {
        mutableState.value = mutableState.value.copy(
            selectedFeatureId = feature.id,
            openedFeatureId = feature.id,
        )
    }

    fun closeFeature() {
        mutableState.value = mutableState.value.copy(
            openedFeatureId = null,
        )
    }
}
