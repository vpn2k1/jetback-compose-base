package com.namvu.myapplication.ui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.AudioClassifierResult
import com.namvu.myapplication.rememberAudioClassifierEngine

@Composable
fun AudioClassifierDemoScreen(onBack: () -> Unit) {
    DemoScaffold(
        title = "Audio Classifier",
        description = "Phân loại âm thanh realtime từ microphone bằng YAMNet.",
        onBack = onBack,
    ) {
        AudioClassifierDemoContent()
    }
}

@Composable
private fun AudioClassifierDemoContent() {
    val engine = rememberAudioClassifierEngine()
    val result by engine.classificationResult
    val isRunning by engine.isRunning
    val hasPermission by engine.hasPermission
    val error by engine.error
    val volumeLevel by engine.volumeLevel

    DisposableEffect(Unit) {
        onDispose { engine.stop() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AudioLivePanel(
            isRunning = isRunning,
            hasPermission = hasPermission,
            error = error,
            volumeLevel = volumeLevel,
            result = result,
        )

        Button(
            onClick = {
                if (isRunning) {
                    engine.stop()
                } else {
                    engine.start()
                }
            },
            enabled = hasPermission,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isRunning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.size(10.dp))
            }
            Text(if (isRunning) "Stop microphone" else "Start microphone")
        }

        if (!hasPermission) {
            Text(
                text = "Cấp quyền microphone để chạy live Audio Classifier.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        AudioClassifierResultPanel(result = result)

        TaskNextStepsPanel(
            nextSteps = listOf(
                "Audio UI/state nằm trong commonMain.",
                "Android dùng MediaPipe AudioClassifier với model yamnet.tflite.",
                "Microphone được đọc bằng AudioRecord do MediaPipe tạo, sau đó classify từng audio clip ngắn.",
                "Bước sau: thêm chọn file audio và decode PCM nếu cần phân tích audio có sẵn.",
            ),
        )
    }
}

@Composable
private fun AudioLivePanel(
    isRunning: Boolean,
    hasPermission: Boolean,
    error: String?,
    volumeLevel: Float,
    result: AudioClassifierResult?,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Live microphone",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = when {
                            error != null -> error
                            !hasPermission -> "Waiting for microphone permission"
                            isRunning -> "Listening and classifying..."
                            else -> "Ready"
                        },
                        color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text(
                    text = result?.categories?.firstOrNull()?.categoryName ?: "-",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            AudioLevelMeter(
                volumeLevel = volumeLevel,
                isRunning = isRunning,
            )
        }
    }
}

@Composable
private fun AudioLevelMeter(
    volumeLevel: Float,
    isRunning: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Input level",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(volumeLevel.coerceIn(0f, 1f))
                    .background(if (isRunning) Color(0xFF46D7A7) else Color(0xFF8B949E)),
            )
            Text(
                text = "${(volumeLevel.coerceIn(0f, 1f) * 100).toInt()}%",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun AudioClassifierResultPanel(result: AudioClassifierResult?) {
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
            Text(
                text = "AudioClassifierResult",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
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
                            text = "Score ${formatAudioClassifierScore(category.score)}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}

private fun formatAudioClassifierScore(score: Float): String {
    val scaled = (score * 1000).toInt() / 10f
    return "$scaled%"
}
