package com.namvu.myapplication.ui.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.FaceLandmarkOverlay
import com.namvu.myapplication.FaceLandmarkerOutput
import com.namvu.myapplication.FaceTrackerCameraPreview
import com.namvu.myapplication.FaceTrackingResult
import com.namvu.myapplication.openGalley
import com.namvu.myapplication.rememberFaceTrackerEngine
import com.namvu.myapplication.rememberStaticFaceLandmarker
import kotlinx.coroutines.launch

private enum class FaceDemoInputMode {
    Camera,
    Image,
}

@Composable
fun FaceLandmarkerDemoScreen(onBack: () -> Unit) {
    DemoScaffold(
        title = "Face Landmarker",
        description = "Camera hoặc ảnh tĩnh với landmarks, blendshapes và facial transformation matrix.",
        onBack = onBack,
    ) {
        var inputMode by remember { mutableStateOf(FaceDemoInputMode.Camera) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FaceInputModeTabs(
                selectedMode = inputMode,
                onModeSelected = { inputMode = it },
                modifier = Modifier.fillMaxWidth(),
            )

            when (inputMode) {
                FaceDemoInputMode.Camera -> FaceCameraDemo()
                FaceDemoInputMode.Image -> FaceImageDemo()
            }

            FaceNextSteps()
        }
    }
}

@Composable
private fun FaceInputModeTabs(
    selectedMode: FaceDemoInputMode,
    onModeSelected: (FaceDemoInputMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FaceDemoInputMode.entries.forEach { mode ->
            val selected = mode == selectedMode
            Text(
                text = when (mode) {
                    FaceDemoInputMode.Camera -> "Camera"
                    FaceDemoInputMode.Image -> "Chọn ảnh"
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onModeSelected(mode) }
                    .padding(vertical = 12.dp),
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
private fun FaceCameraDemo() {
    val engine = rememberFaceTrackerEngine()
    val result by engine.trackingResult
    val isRunning by engine.isRunning
    val error by engine.error

    DisposableEffect(Unit) {
        onDispose { engine.stop() }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FacePreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            FaceTrackerCameraPreview(
                engine = engine,
                modifier = Modifier.fillMaxSize(),
            )
            FaceLandmarkOverlay(
                result = result,
                modifier = Modifier.fillMaxSize(),
            )
            FacePreviewStatus(
                text = when {
                    error != null -> error ?: ""
                    result?.faces?.isNotEmpty() == true -> "Tracking ${result?.faces?.size ?: 0} face"
                    isRunning -> "Looking for a face..."
                    else -> "Starting camera..."
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        FaceResultPanel(
            result = result,
            error = error,
        )
    }
}

@Composable
private fun FaceImageDemo() {
    val landmarker = rememberStaticFaceLandmarker()
    val scope = rememberCoroutineScope()

    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var result by remember { mutableStateOf<FaceTrackingResult?>(null) }
    var isDetecting by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Chọn ảnh khuôn mặt để detect landmarks.") }

    val imagePicker = openGalley { bytes ->
        imageBytes = bytes
        result = null

        if (bytes == null) {
            status = "Chưa chọn ảnh."
            return@openGalley
        }

        isDetecting = true
        status = "Đang detect Face Landmarker..."
        scope.launch {
            val output = landmarker.detect(bytes)
            result = output?.toFaceTrackingResult()
            status = when {
                output == null -> "Không xử lý được ảnh này."
                output.faces.isEmpty() -> "Không tìm thấy khuôn mặt."
                else -> "Detected ${output.faces.size} face, ${output.faces.firstOrNull()?.size ?: 0} landmarks."
            }
            isDetecting = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FacePreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            val bytes = imageBytes
            if (bytes != null) {
                Image(
                    bitmap = remember(bytes) { bytes.decodeToImageBitmap() },
                    contentDescription = "Selected face image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
                FaceLandmarkOverlay(
                    result = result,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Text(
                    text = "Image preview will be here",
                    color = Color(0xFFD9DEE7),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            FacePreviewStatus(
                text = status,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        Button(
            onClick = { imagePicker() },
            enabled = !isDetecting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isDetecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.size(10.dp))
            }
            Text(if (imageBytes == null) "Chọn ảnh" else "Chọn ảnh khác")
        }

        FaceResultPanel(
            result = result,
            error = null,
        )
    }
}

@Composable
private fun FacePreviewSurface(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF111318))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp),
            ),
        content = content,
    )
}

@Composable
private fun FacePreviewStatus(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0x99000000))
            .padding(10.dp),
        color = Color.White,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun FaceResultPanel(
    result: FaceTrackingResult?,
    error: String?,
) {
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
                text = "FaceLandmarkerResult",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ResultMetric("Faces", "${result?.faces?.size ?: 0}")
                ResultMetric("Landmarks", "${result?.faces?.firstOrNull()?.size ?: 0}")
                ResultMetric("Matrices", "${result?.facialTransformationMatrixes?.size ?: 0}")
            }

            BlendShapeSummary(result = result)
        }
    }
}

@Composable
private fun ResultMetric(
    label: String,
    value: String,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BlendShapeSummary(result: FaceTrackingResult?) {
    val topBlendShapes = result
        ?.blendShapes
        ?.firstOrNull()
        ?.sortedByDescending { it.score }
        ?.take(5)
        ?: emptyList()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Top blendshapes",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        if (topBlendShapes.isEmpty()) {
            Text(
                text = "No blendshape output yet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        } else {
            topBlendShapes.forEach { blendShape ->
                Text(
                    text = "${blendShape.categoryName}: ${formatScore(blendShape.score)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun FaceNextSteps() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Next implementation steps",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            listOf(
                "Tune camera frame orientation/mirroring for production.",
                "Add filter renderer using facial transformation matrix.",
                "Map blendshapes to expression controls.",
                "Move reusable result UI into a shared Face task module.",
            ).forEachIndexed { index, step ->
                Text(
                    text = "${index + 1}. $step",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun FaceLandmarkerOutput.toFaceTrackingResult(): FaceTrackingResult =
    FaceTrackingResult(
        imageWidth = imageWidth,
        imageHeight = imageHeight,
        faces = faces,
        blendShapes = blendShapes,
        facialTransformationMatrixes = facialTransformationMatrixes,
        connections = connections,
        timestampMs = 0L,
    )

private fun formatScore(score: Float): String {
    val scaled = (score * 1000).toInt() / 10f
    return "$scaled%"
}
