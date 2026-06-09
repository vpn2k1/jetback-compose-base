package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImageSegmenter(): ImageSegmenter = remember {
    UnsupportedImageSegmenter
}

private object UnsupportedImageSegmenter : ImageSegmenter {
    override suspend fun replaceBackground(
        originalImageBytes: ByteArray,
        bgImageBytes: ByteArray,
    ): ByteArray? = null

    override suspend fun replaceBackgroundWithColor(
        originalImageBytes: ByteArray,
        backgroundColorArgb: Int,
    ): ByteArray? = null
}
