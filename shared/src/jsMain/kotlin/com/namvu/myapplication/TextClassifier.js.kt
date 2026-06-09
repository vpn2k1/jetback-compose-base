package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberStaticTextClassifier(): StaticTextClassifier = remember { UnsupportedStaticTextClassifier }

private object UnsupportedStaticTextClassifier : StaticTextClassifier {
    override suspend fun classify(text: String): TextClassifierResult? = null
}
