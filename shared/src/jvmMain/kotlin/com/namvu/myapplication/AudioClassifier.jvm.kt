package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
actual fun rememberAudioClassifierEngine(): AudioClassifierEngine = remember { UnsupportedAudioClassifierEngine }

private object UnsupportedAudioClassifierEngine : AudioClassifierEngine {
    override val classificationResult: State<AudioClassifierResult?> = mutableStateOf(null)
    override val isRunning: State<Boolean> = mutableStateOf(false)
    override val hasPermission: State<Boolean> = mutableStateOf(false)
    override val error: State<String?> = mutableStateOf("Audio Classifier chỉ có Android implementation ở bước này.")
    override val volumeLevel: State<Float> = mutableStateOf(0f)

    override fun start() = Unit
    override fun stop() = Unit
}
