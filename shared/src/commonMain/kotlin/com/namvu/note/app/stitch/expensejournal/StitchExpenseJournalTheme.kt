package com.namvu.note.app.stitch.expensejournal

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object StitchExpenseJournalColors {
    val Background = Color(0xFF131315)
    val SurfaceLow = Color(0xFF1C1B1D)
    val Surface = Color(0xFF201F22)
    val SurfaceHigh = Color(0xFF2A2A2C)
    val OnSurface = Color(0xFFE5E1E4)
    val OnSurfaceMuted = Color(0xFFC7C4D8)
    val Outline = Color(0xFF464555)
    val Primary = Color(0xFFC3C0FF)
    val PrimaryContainer = Color(0xFF4F46E5)
    val Secondary = Color(0xFF4EDEA3)
    val Tertiary = Color(0xFFFFB95F)
    val Danger = Color(0xFFFFB4AB)
}

fun stitchPrimaryGradient(): Brush = Brush.linearGradient(
    colors = listOf(
        StitchExpenseJournalColors.PrimaryContainer,
        Color(0xFF7C72FF),
    ),
)

fun stitchGlassGradient(): Brush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF2A2A2C),
        Color(0xFF1C1B1D),
    ),
)
