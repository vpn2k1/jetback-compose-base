package com.namvu.myapplication

import androidx.compose.runtime.Composable

interface ImageSegmenter {
    // Hàm mới nhận vào ảnh gốc và ảnh nền mong muốn
    suspend fun replaceBackground(
        originalImageBytes: ByteArray,
        bgImageBytes: ByteArray
    ): ByteArray?

    suspend fun replaceBackgroundWithColor(
        originalImageBytes: ByteArray,
        backgroundColorArgb: Int,
    ): ByteArray?
}

@Composable
expect fun rememberImageSegmenter(): ImageSegmenter
