package com.namvu.myapplication.ui.demo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.ObjectDetection
import com.namvu.myapplication.ObjectDetectorCameraPreview
import com.namvu.myapplication.ObjectDetectorResult
import com.namvu.myapplication.openGalley
import com.namvu.myapplication.rememberObjectDetectorEngine
import com.namvu.myapplication.rememberStaticObjectDetector
import kotlinx.coroutines.launch
import kotlin.math.min

private enum class ObjectDetectorInputMode {
    Image,
    Camera,
}

@Composable
fun ObjectDetectorDemoScreen(onBack: () -> Unit) {
    DemoScaffold(
        title = "Object Detector",
        description = "Phát hiện vật thể trong ảnh và vẽ bounding boxes.",
        onBack = onBack,
    ) {
        ObjectDetectorDemoContent()
    }
}

@Composable
private fun ObjectDetectorDemoContent() {
    var inputMode by remember { mutableStateOf(ObjectDetectorInputMode.Image) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ObjectDetectorInputModeTabs(
            selectedMode = inputMode,
            onModeSelected = { inputMode = it },
        )

        when (inputMode) {
            ObjectDetectorInputMode.Image -> ObjectDetectorImageDemo()
            ObjectDetectorInputMode.Camera -> ObjectDetectorCameraDemo()
        }

        ObjectDetectorPipelinePanel(mode = inputMode)
    }
}

@Composable
private fun ObjectDetectorInputModeTabs(
    selectedMode: ObjectDetectorInputMode,
    onModeSelected: (ObjectDetectorInputMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ObjectDetectorInputMode.entries.forEach { mode ->
            val selected = mode == selectedMode
            Text(
                text = when (mode) {
                    ObjectDetectorInputMode.Image -> "Ảnh"
                    ObjectDetectorInputMode.Camera -> "Camera"
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
private fun ObjectDetectorImageDemo() {
    val detector = rememberStaticObjectDetector()
    val scope = rememberCoroutineScope()

    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var result by remember { mutableStateOf<ObjectDetectorResult?>(null) }
    var isDetecting by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Chọn ảnh để chạy Object Detector.") }

    val imagePicker = openGalley { bytes ->
        imageBytes = bytes
        result = null

        if (bytes == null) {
            status = "Chưa chọn ảnh."
            return@openGalley
        }

        isDetecting = true
        status = "Đang chạy MediaPipe Object Detector..."
        scope.launch {
            result = detector.detect(bytes)
            status = when {
                result == null -> "Chưa xử lý được ảnh. Android implementation mới hỗ trợ detector ở bước này."
                result?.detections?.isEmpty() == true -> "Không phát hiện vật thể nào."
                else -> "Detected ${result?.detections?.size ?: 0} objects."
            }
            isDetecting = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ObjectDetectorPreview(
            imageBytes = imageBytes,
            result = result,
            status = status,
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        )

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

        ObjectDetectionResultPanel(result = result)
    }
}

@Composable
private fun ObjectDetectorCameraDemo() {
    val engine = rememberObjectDetectorEngine()
    val result by engine.detectionResult
    val isRunning by engine.isRunning
    val error by engine.error

    DisposableEffect(Unit) {
        onDispose { engine.stop() }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF111318))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(8.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            ObjectDetectorCameraPreview(
                engine = engine,
                modifier = Modifier.fillMaxSize(),
            )
            ObjectDetectionOverlay(
                result = result,
                modifier = Modifier.fillMaxSize(),
            )
            Text(
                text = when {
                    error != null -> error ?: ""
                    result?.detections?.isNotEmpty() == true -> "Detected ${result?.detections?.size ?: 0} objects"
                    isRunning -> "Looking for objects..."
                    else -> "Starting camera..."
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0x99000000))
                    .padding(10.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        ObjectDetectionResultPanel(result = result)
    }
}

@Composable
private fun ObjectDetectorPreview(
    imageBytes: ByteArray?,
    result: ObjectDetectorResult?,
    status: String,
    modifier: Modifier = Modifier,
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
        contentAlignment = Alignment.Center,
    ) {
        if (imageBytes != null) {
            Image(
                bitmap = remember(imageBytes) { imageBytes.decodeToImageBitmap() },
                contentDescription = "Selected object detection image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
            ObjectDetectionOverlay(
                result = result,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = "Object detection preview will be here",
                color = Color(0xFFD9DEE7),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Text(
            text = status,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0x99000000))
                .padding(10.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ObjectDetectionOverlay(
    result: ObjectDetectorResult?,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val output = result ?: return@Canvas
        if (output.imageWidth <= 0 || output.imageHeight <= 0) return@Canvas

        val scale = min(
            size.width / output.imageWidth.toFloat(),
            size.height / output.imageHeight.toFloat(),
        )
        val renderedWidth = output.imageWidth * scale
        val renderedHeight = output.imageHeight * scale
        val left = (size.width - renderedWidth) / 2f
        val top = (size.height - renderedHeight) / 2f

        output.detections.forEachIndexed { index, detection ->
            val color = detectionColor(index)
            val box = detection.boundingBox
            val boxLeft = left + box.left * scale
            val boxTop = top + box.top * scale
            val boxRight = left + box.right * scale
            val boxBottom = top + box.bottom * scale

            drawRect(
                color = color,
                topLeft = Offset(boxLeft, boxTop),
                size = Size(boxRight - boxLeft, boxBottom - boxTop),
                style = Stroke(width = 4f, cap = StrokeCap.Round),
            )
        }
    }
}

@Composable
private fun ObjectDetectionResultPanel(result: ObjectDetectorResult?) {
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
                text = "ObjectDetectorResult",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Detections: ${result?.detections?.size ?: 0}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            val detections = result?.detections.orEmpty()
            if (detections.isEmpty()) {
                Text(
                    text = "No object output yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                detections.forEachIndexed { index, detection ->
                    ObjectDetectionRow(
                        index = index,
                        detection = detection,
                    )
                }
            }
        }
    }
}

@Composable
private fun ObjectDetectionRow(
    index: Int,
    detection: ObjectDetection,
) {
    val topCategory = detection.categories.maxByOrNull { it.score }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(detectionColor(index)),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = topCategory?.categoryName?.takeIf { it.isNotBlank() } ?: "Unknown object",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Score ${formatObjectScore(topCategory?.score ?: 0f)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ObjectDetectorPipelinePanel(
    mode: ObjectDetectorInputMode,
) {
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
                text = "Pipeline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            listOf(
                if (mode == ObjectDetectorInputMode.Image) {
                    "Input: ảnh từ picker trong common UI."
                } else {
                    "Input: CameraX frame từ androidMain."
                },
                "Android engine: MediaPipe ObjectDetector với EfficientDet-Lite0.",
                "Output: bounding boxes + category scores trả về commonMain.",
                "Render: Compose Canvas vẽ boxes theo kích thước preview.",
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

private fun detectionColor(index: Int): Color =
    listOf(
        Color(0xFF46D7A7),
        Color(0xFFFFD166),
        Color(0xFFEF476F),
        Color(0xFF5B8DEF),
        Color(0xFFC77DFF),
    )[index % 5]

private fun formatObjectScore(score: Float): String {
    val scaled = (score * 1000).toInt() / 10f
    return "$scaled%"
}
