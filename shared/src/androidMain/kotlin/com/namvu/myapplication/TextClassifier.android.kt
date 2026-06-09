package com.namvu.myapplication

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.text.textclassifier.TextClassifier as MediaPipeTextClassifier
import com.google.mediapipe.tasks.text.textclassifier.TextClassifierResult as MediaPipeTextClassifierResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException

@Composable
actual fun rememberStaticTextClassifier(): StaticTextClassifier {
    val context = LocalContext.current
    return remember(context) {
        AndroidStaticTextClassifier(context.applicationContext)
    }
}

private class AndroidStaticTextClassifier(
    private val context: Context,
) : StaticTextClassifier {
    private val modelFileName = "bert_classifier.tflite"
    private var textClassifier: MediaPipeTextClassifier? = null

    override suspend fun classify(text: String): TextClassifierResult? =
        withContext(Dispatchers.Default) {
            try {
                initIfNeeded()
                textClassifier
                    ?.classify(text)
                    ?.toSharedTextClassifierResult()
            } catch (e: Exception) {
                Log.e("TextClassifier", "Failed to classify text", e)
                null
            }
        }

    private fun initIfNeeded() {
        if (textClassifier != null) return

        try {
            context.assets.open(modelFileName).close()
            val options = MediaPipeTextClassifier.TextClassifierOptions.builder()
                .setBaseOptions(
                    BaseOptions.builder()
                        .setModelAssetPath(modelFileName)
                        .build(),
                )
                .setMaxResults(5)
                .build()

            textClassifier = MediaPipeTextClassifier.createFromOptions(context, options)
        } catch (e: FileNotFoundException) {
            throw IllegalStateException("Missing MediaPipe model asset: $modelFileName", e)
        }
    }
}

private fun MediaPipeTextClassifierResult.toSharedTextClassifierResult(): TextClassifierResult =
    TextClassifierResult(
        categories = classificationResult()
            .classifications()
            .flatMap { it.categories() }
            .sortedByDescending { it.score() }
            .map { category ->
                TextClassificationCategory(
                    index = category.index(),
                    categoryName = category.categoryName(),
                    displayName = category.displayName(),
                    score = category.score(),
                )
            },
        timestampMs = timestampMs(),
    )
