package com.namvu.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun MediaPipeFeatureDetailScreen(
    feature: MediaPipeFeatureDefinition,
    onBack: () -> Unit,
) {
    when (feature.id) {
        "image_segmentation" -> ImageSegmentationFeatureScreen(
            feature = feature,
            onBack = onBack,
        )

        else -> MediaPipeFeaturePlaceholderScreen(
            feature = feature,
            onBack = onBack,
        )
    }
}

@Composable
fun ImageSegmentationFeatureScreen(
    feature: MediaPipeFeatureDefinition,
    onBack: () -> Unit,
    controller: MainEditorController = remember { MainEditorController() },
) {
    val state by controller.state

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            FeatureDetailTopBar(
                feature = feature,
                onBack = onBack,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            BackgroundEditorPreviewArea(
                selectedBackgroundColor = state.selectedBackgroundColor,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            PipelineInfoPanel(
                title = "Image Segmenter pipeline",
                lines = listOf(
                    "1. Input: camera frame hoac anh nguoi.",
                    "2. Engine sau nay: MediaPipe Image Segmenter tao mask.",
                    "3. Output tam thoi: foreground placeholder tren nen mau.",
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
                tonalElevation = 2.dp,
            ) {
                BackgroundColorPicker(
                    colors = state.backgroundColors,
                    selectedColor = state.selectedBackgroundColor,
                    onColorSelected = controller::selectBackgroundColor,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun MediaPipeFeaturePlaceholderScreen(
    feature: MediaPipeFeatureDefinition,
    onBack: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(16.dp),
        ) {
            FeatureDetailTopBar(
                feature = feature,
                onBack = onBack,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(feature.accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Man nay da san san khung dieu huong. Buoc tiep theo se them UI/state rieng va engine MediaPipe.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureDetailTopBar(
    feature: MediaPipeFeatureDefinition,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "<",
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onBack)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        FeatureAccentDot(color = feature.accentColor)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${feature.inputType} -> ${feature.outputType}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun PipelineInfoPanel(
    title: String,
    lines: List<String>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            lines.forEach { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
