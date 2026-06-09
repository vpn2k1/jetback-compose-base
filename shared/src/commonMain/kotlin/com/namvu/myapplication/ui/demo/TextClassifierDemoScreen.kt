package com.namvu.myapplication.ui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.TextClassifierResult
import com.namvu.myapplication.rememberStaticTextClassifier
import kotlinx.coroutines.launch

private val sampleTexts = listOf(
    "I love this product. It works great and feels very polished.",
    "The experience was disappointing and I would not recommend it.",
    "The package arrived yesterday and I opened it this morning.",
    "This app is fast, useful, and easy to understand.",
)

@Composable
fun TextClassifierDemoScreen(onBack: () -> Unit) {
    DemoScaffold(
        title = "Text Classifier",
        description = "Phân loại văn bản bằng MediaPipe Text Classifier.",
        onBack = onBack,
    ) {
        TextClassifierDemoContent()
    }
}

@Composable
private fun TextClassifierDemoContent() {
    val classifier = rememberStaticTextClassifier()
    val scope = rememberCoroutineScope()

    var inputText by remember { mutableStateOf(sampleTexts.first()) }
    var result by remember { mutableStateOf<TextClassifierResult?>(null) }
    var isClassifying by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Nhập text hoặc chọn sample để phân loại.") }

    fun classifyCurrentText() {
        val text = inputText.trim()
        if (text.isBlank()) {
            status = "Text không được để trống."
            result = null
            return
        }

        isClassifying = true
        status = "Đang chạy MediaPipe Text Classifier..."
        scope.launch {
            val classified = classifier.classify(text)
            result = classified
            status = when {
                classified == null -> "Chưa xử lý được text. Android implementation mới hỗ trợ classifier ở bước này."
                classified.categories.isEmpty() -> "Không có kết quả phân loại."
                else -> "Top result: ${classified.categories.firstOrNull()?.categoryName.orEmpty()}"
            }
            isClassifying = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextClassifierInputPanel(
            inputText = inputText,
            onInputTextChange = {
                inputText = it
                result = null
            },
            onSampleSelected = {
                inputText = it
                result = null
                status = "Sample đã chọn. Bấm Classify để chạy."
            },
        )

        Button(
            onClick = ::classifyCurrentText,
            enabled = !isClassifying,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isClassifying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.size(10.dp))
            }
            Text("Classify")
        }

        Text(
            text = status,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )

        TextClassifierResultPanel(result = result)

        TaskNextStepsPanel(
            nextSteps = listOf(
                "Text input và UI state nằm trong commonMain.",
                "Android dùng MediaPipe TextClassifier với model bert_classifier.tflite.",
                "TextClassifierResult trả category scores về commonMain.",
                "Bước sau: thêm allowlist/denylist, score threshold hoặc custom text classifier.",
            ),
        )
    }
}

@Composable
private fun TextClassifierInputPanel(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSampleSelected: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Input text",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                minLines = 5,
                maxLines = 8,
                label = { Text("Text") },
            )
            Text(
                text = "Samples",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                sampleTexts.forEachIndexed { index, sample ->
                    Text(
                        text = "Sample ${index + 1}",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(8.dp),
                            )
                            .clickable { onSampleSelected(sample) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun TextClassifierResultPanel(result: TextClassifierResult?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "TextClassifierResult",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                if (result != null) {
                    Text(
                        text = "${result.timestampMs} ms",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            val categories = result?.categories.orEmpty()
            if (categories.isEmpty()) {
                Text(
                    text = "No classification output yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            } else {
                categories.take(5).forEach { category ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = category.categoryName.ifBlank { "Unknown" },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        LinearProgressIndicator(
                            progress = { category.score.coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            text = "Score ${formatTextClassifierScore(category.score)}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}

private fun formatTextClassifierScore(score: Float): String {
    val scaled = (score * 1000).toInt() / 10f
    return "$scaled%"
}
