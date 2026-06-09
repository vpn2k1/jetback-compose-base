package com.namvu.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MainEditorScreen(
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
            BackgroundEditorPreviewArea(
                selectedBackgroundColor = state.selectedBackgroundColor,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeatureTabs(
                selectedFeature = state.selectedFeature,
                onFeatureSelected = controller::selectFeature,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeatureBottomPanel(
                state = state,
                onBackgroundColorSelected = controller::selectBackgroundColor,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun BackgroundEditorPreviewArea(
    selectedBackgroundColor: BackgroundColorOption,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        BackgroundEditorBackgroundLayer(
            selectedBackgroundColor = selectedBackgroundColor,
            modifier = Modifier.fillMaxSize(),
        )
        BackgroundEditorForegroundLayer(
            selectedBackgroundColor = selectedBackgroundColor,
            modifier = Modifier.fillMaxSize(),
        )
        BackgroundEditorOverlayLayer(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun BackgroundEditorBackgroundLayer(
    selectedBackgroundColor: BackgroundColorOption,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(selectedBackgroundColor.color),
    )
}

@Composable
fun BackgroundEditorForegroundLayer(
    selectedBackgroundColor: BackgroundColorOption,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.46f)
                .aspectRatio(0.62f)
                .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                .background(previewSubjectColor(selectedBackgroundColor.id)),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Text(
                text = selectedBackgroundColor.label,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x66000000))
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun BackgroundEditorOverlayLayer(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier)
}

@Composable
fun FeatureTabs(
    selectedFeature: MainFeature,
    onFeatureSelected: (MainFeature) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MainFeature.entries.forEach { feature ->
            val isSelected = feature == selectedFeature
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onFeatureSelected(feature) },
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(8.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = feature.label,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureBottomPanel(
    state: MainEditorState,
    onBackgroundColorSelected: (BackgroundColorOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
    ) {
        when (state.selectedFeature) {
            MainFeature.Background -> BackgroundColorPicker(
                colors = state.backgroundColors,
                selectedColor = state.selectedBackgroundColor,
                onColorSelected = onBackgroundColorSelected,
                modifier = Modifier.fillMaxWidth(),
            )

            MainFeature.FaceFilter,
            MainFeature.Sticker,
            MainFeature.Export -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.selectedFeature.label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun BackgroundColorPicker(
    colors: List<BackgroundColorOption>,
    selectedColor: BackgroundColorOption,
    onColorSelected: (BackgroundColorOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
    ) {
        items(colors, key = { it.id }) { option ->
            BackgroundColorItem(
                option = option,
                selected = option.id == selectedColor.id,
                onClick = { onColorSelected(option) },
            )
        }
    }
}

@Composable
fun BackgroundColorItem(
    option: BackgroundColorOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(option.color)
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
        }

        Text(
            text = option.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

private fun previewSubjectColor(backgroundId: String): Color =
    when (backgroundId) {
        "black", "blue", "purple", "red", "gray" -> Color(0xFFECEFF1)
        else -> Color(0xFF263238)
    }
