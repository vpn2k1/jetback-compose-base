package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraBackgroundReplacer(
    modifier: Modifier = Modifier,
    backgroundImageBytes: ByteArray?, // Ảnh nền mới muốn thay vào thay cho nền thật
    backgroundColorArgb: Int = 0xFF22C55E.toInt(),
)
