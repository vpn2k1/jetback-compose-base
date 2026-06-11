package com.namvu.myapplication.ui.base.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppDimens(
    val minTouchTarget: Dp = 48.dp,
    val compactButtonHeight: Dp = 40.dp,
    val buttonHeight: Dp = 48.dp,
    val largeButtonHeight: Dp = 56.dp,
    val cardRadius: Dp = 8.dp,
    val sheetRadius: Dp = 16.dp,
    val screenMaxWidth: Dp = 720.dp,
)

val DefaultAppDimens = AppDimens()
