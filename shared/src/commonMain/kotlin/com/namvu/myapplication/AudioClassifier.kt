package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

data class AudioClassificationCategory(
    val index: Int,
    val categoryName: String,
    val displayName: String,
    val score: Float,
)

data class AudioClassifierResult(
    val categories: List<AudioClassificationCategory>,
    val timestampMs: Long,
)

interface AudioClassifierEngine {
    val classificationResult: State<AudioClassifierResult?>
    val isRunning: State<Boolean>
    val hasPermission: State<Boolean>
    val error: State<String?>
    val volumeLevel: State<Float>

    fun start()
    fun stop()
}

@Composable
expect fun rememberAudioClassifierEngine(): AudioClassifierEngine
