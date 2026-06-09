package com.namvu.myapplication

import androidx.compose.ui.graphics.Color

enum class MainFeature(
    val label: String,
) {
    Background("Thay nền"),
    FaceFilter("Face filter"),
    Sticker("Sticker"),
    Export("Lưu ảnh"),
}

data class BackgroundColorOption(
    val id: String,
    val label: String,
    val color: Color,
)

data class MainEditorState(
    val selectedFeature: MainFeature = MainFeature.Background,
    val selectedBackgroundColor: BackgroundColorOption = DefaultBackgroundColors.first(),
    val backgroundColors: List<BackgroundColorOption> = DefaultBackgroundColors,
)

val DefaultBackgroundColors = listOf(
    BackgroundColorOption("white", "White", Color.White),
    BackgroundColorOption("black", "Black", Color.Black),
    BackgroundColorOption("red", "Red", Color(0xFFE53935)),
    BackgroundColorOption("green", "Green", Color(0xFF43A047)),
    BackgroundColorOption("blue", "Blue", Color(0xFF1E88E5)),
    BackgroundColorOption("yellow", "Yellow", Color(0xFFFDD835)),
    BackgroundColorOption("gray", "Gray", Color(0xFF757575)),
    BackgroundColorOption("purple", "Purple", Color(0xFF8E24AA)),
    BackgroundColorOption("pink", "Pink", Color(0xFFD81B60)),
    BackgroundColorOption("orange", "Orange", Color(0xFFFB8C00)),
)
