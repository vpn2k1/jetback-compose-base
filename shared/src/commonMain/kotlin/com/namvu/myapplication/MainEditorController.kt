package com.namvu.myapplication

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class MainEditorController(
    initialState: MainEditorState = MainEditorState(),
) {
    private val mutableState = mutableStateOf(initialState)

    val state: State<MainEditorState> = mutableState

    fun selectFeature(feature: MainFeature) {
        mutableState.value = mutableState.value.copy(
            selectedFeature = feature,
        )
    }

    fun selectBackgroundColor(option: BackgroundColorOption) {
        mutableState.value = mutableState.value.copy(
            selectedBackgroundColor = option,
        )
    }
}
