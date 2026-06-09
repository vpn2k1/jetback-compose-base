package com.namvu.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
fun MediaPipeEditorScreen(
    controller: MediaPipeEditorController = remember { MediaPipeEditorController() },
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
            PreviewArea(
                state = state,
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
                onCameraRunningChanged = controller::setCameraRunning,
                onErrorChanged = controller::setError,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun PreviewArea(
    state: MediaPipeEditorState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp)),
    ) {
        BackgroundLayer(
            selectedFeature = state.selectedFeature,
            modifier = Modifier.fillMaxSize(),
        )
        CameraLayer(
            isCameraRunning = state.isCameraRunning,
            selectedFeature = state.selectedFeature,
            modifier = Modifier.fillMaxSize(),
        )
        OverlayLayer(
            selectedFeature = state.selectedFeature,
            errorMessage = state.errorMessage,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun BackgroundLayer(
    selectedFeature: MediaPipeFeature,
    modifier: Modifier = Modifier,
) {
    val color = when (selectedFeature) {
        MediaPipeFeature.Background -> Color(0xFFE8F5E9)
        MediaPipeFeature.FaceFilter -> Color(0xFFE3F2FD)
        MediaPipeFeature.ObjectDetection -> Color(0xFFFFEBEE)
        MediaPipeFeature.PoseDetection -> Color(0xFFFFF3E0)
        MediaPipeFeature.HandTracking -> Color(0xFFF3E5F5)
        MediaPipeFeature.Export -> Color(0xFFECEFF1)
    }

    Box(modifier = modifier.background(color))
}

@Composable
fun CameraLayer(
    isCameraRunning: Boolean,
    selectedFeature: MediaPipeFeature,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC111318))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = if (isCameraRunning) "Camera placeholder running" else "Camera placeholder stopped",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Task: ${selectedFeature.label}",
                color = Color(0xFFD9DEE7),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun OverlayLayer(
    selectedFeature: MediaPipeFeature,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(16.dp),
    ) {
        Text(
            text = overlayLabel(selectedFeature),
            modifier = Modifier
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x99000000))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(10.dp),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun FeatureTabs(
    selectedFeature: MediaPipeFeature,
    onFeatureSelected: (MediaPipeFeature) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
    ) {
        items(MediaPipeFeature.entries, key = { it.name }) { feature ->
            val selected = feature == selectedFeature
            Text(
                text = feature.label,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onFeatureSelected(feature) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun FeatureBottomPanel(
    state: MediaPipeEditorState,
    onCameraRunningChanged: (Boolean) -> Unit,
    onErrorChanged: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = state.selectedFeature.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = featureDescription(state.selectedFeature),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PanelAction(
                    label = if (state.isCameraRunning) "Stop camera" else "Start camera",
                    onClick = { onCameraRunningChanged(!state.isCameraRunning) },
                )
                PanelAction(
                    label = "Clear error",
                    onClick = { onErrorChanged(null) },
                )
            }
        }
    }
}

@Composable
private fun PanelAction(
    label: String,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        style = MaterialTheme.typography.labelMedium,
    )
}

private fun overlayLabel(feature: MediaPipeFeature): String =
    when (feature) {
        MediaPipeFeature.Background -> "Image Segmenter overlay"
        MediaPipeFeature.FaceFilter -> "Face Landmarker overlay"
        MediaPipeFeature.ObjectDetection -> "Object Detector overlay"
        MediaPipeFeature.PoseDetection -> "Pose Landmarker overlay"
        MediaPipeFeature.HandTracking -> "Hand Landmarker overlay"
        MediaPipeFeature.Export -> "Export overlay"
    }

private fun featureDescription(feature: MediaPipeFeature): String =
    when (feature) {
        MediaPipeFeature.Background -> "Chuan bi engine Image Segmenter de tach nguoi va thay nen."
        MediaPipeFeature.FaceFilter -> "Chuan bi engine Face Landmarker cho landmarks, blendshapes va matrix."
        MediaPipeFeature.ObjectDetection -> "Chuan bi engine Object Detector de ve bounding boxes."
        MediaPipeFeature.PoseDetection -> "Chuan bi engine Pose Landmarker cho skeleton/body effects."
        MediaPipeFeature.HandTracking -> "Chuan bi engine Hand Landmarker cho gestures va hand effects."
        MediaPipeFeature.Export -> "Chuan bi buoc export anh/video da xu ly."
    }
