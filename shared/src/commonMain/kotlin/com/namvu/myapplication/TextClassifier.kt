package com.namvu.myapplication

import androidx.compose.runtime.Composable

data class TextClassificationCategory(
    val index: Int,
    val categoryName: String,
    val displayName: String,
    val score: Float,
)

data class TextClassifierResult(
    val categories: List<TextClassificationCategory>,
    val timestampMs: Long,
)

interface StaticTextClassifier {
    suspend fun classify(text: String): TextClassifierResult?
}

@Composable
expect fun rememberStaticTextClassifier(): StaticTextClassifier
