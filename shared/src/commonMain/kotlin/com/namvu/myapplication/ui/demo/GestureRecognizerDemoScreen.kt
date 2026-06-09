package com.namvu.myapplication.ui.demo

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.GestureRecognitionResult
import com.namvu.myapplication.GestureRecognizerCameraPreview
import com.namvu.myapplication.GestureVideoResult
import com.namvu.myapplication.HandCameraFacing
import com.namvu.myapplication.openGalley
import com.namvu.myapplication.openVideoPicker
import com.namvu.myapplication.rememberGestureRecognizerEngine
import com.namvu.myapplication.rememberGestureVideoRecognizer
import com.namvu.myapplication.rememberStaticGestureRecognizer
import kotlinx.coroutines.launch
import kotlin.math.min

private enum class GestureInputMode {
    Camera,
    Image,
    Video,
}

@Composable
fun GestureRecognizerDemoScreen(onBack: () -> Unit) {
    DemoScaffold(
        title = "Gesture Recognizer",
        description = "Live camera, ảnh hoặc video để nhận diện hand gestures.",
        onBack = onBack,
    ) {
        var inputMode by remember { mutableStateOf(GestureInputMode.Camera) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GestureInputModeTabs(
                selectedMode = inputMode,
                onModeSelected = { inputMode = it },
            )

            when (inputMode) {
                GestureInputMode.Camera -> GestureCameraDemo()
                GestureInputMode.Image -> GestureImageDemo()
                GestureInputMode.Video -> GestureVideoDemo()
            }

            GesturePipelinePanel(mode = inputMode)
        }
    }
}

@Composable
private fun GestureInputModeTabs(
    selectedMode: GestureInputMode,
    onModeSelected: (GestureInputMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GestureInputMode.entries.forEach { mode ->
            val selected = mode == selectedMode
            Text(
                text = when (mode) {
                    GestureInputMode.Camera -> "Camera"
                    GestureInputMode.Image -> "Ảnh"
                    GestureInputMode.Video -> "Video"
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
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun GestureCameraDemo() {
    val engine = rememberGestureRecognizerEngine()
    val result by engine.recognitionResult
    val isRunning by engine.isRunning
    val error by engine.error
    val cameraFacing by engine.cameraFacing

    DisposableEffect(Unit) {
        onDispose { engine.stop() }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GestureCameraFacingTabs(
            selectedFacing = cameraFacing,
            onFacingSelected = engine::setCameraFacing,
        )

        GesturePreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            GestureRecognizerCameraPreview(
                engine = engine,
                modifier = Modifier.fillMaxSize(),
            )
            GestureLandmarkOverlay(
                result = result,
                modifier = Modifier.fillMaxSize(),
            )
            GestureStatusBar(
                text = result?.bestGestureText()?.takeIf { error == null } ?: when {
                    error != null -> error ?: ""
                    isRunning -> "Đưa bàn tay vào camera..."
                    else -> "Đang khởi động camera..."
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        GestureResultPanel(
            result = result,
            videoResult = null,
        )
    }
}

@Composable
private fun GestureCameraFacingTabs(
    selectedFacing: HandCameraFacing,
    onFacingSelected: (HandCameraFacing) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        HandCameraFacing.entries.forEach { facing ->
            val selected = facing == selectedFacing
            Text(
                text = when (facing) {
                    HandCameraFacing.Front -> "Front"
                    HandCameraFacing.Back -> "Back"
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selected) Color(0xFF1F6FEB) else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onFacingSelected(facing) }
                    .padding(vertical = 10.dp),
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun GestureImageDemo() {
    val recognizer = rememberStaticGestureRecognizer()
    val scope = rememberCoroutineScope()

    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var result by remember { mutableStateOf<GestureRecognitionResult?>(null) }
    var isRecognizing by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Chọn ảnh có bàn tay để nhận diện gesture.") }

    val imagePicker = openGalley { bytes ->
        imageBytes = bytes
        result = null
        if (bytes == null) {
            status = "Chưa chọn ảnh."
            return@openGalley
        }

        isRecognizing = true
        status = "Đang chạy MediaPipe Gesture Recognizer..."
        scope.launch {
            val recognized = recognizer.recognize(bytes)
            result = recognized
            status = when {
                recognized == null -> "Chưa xử lý được ảnh. Android implementation mới hỗ trợ ở bước này."
                recognized.gestures.flatten().isEmpty() -> "Không nhận diện được gesture rõ trong ảnh."
                else -> recognized.bestGestureText() ?: "Detected gesture."
            }
            isRecognizing = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GesturePreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            if (imageBytes != null) {
                Image(
                    bitmap = remember(imageBytes) { imageBytes!!.decodeToImageBitmap() },
                    contentDescription = "Selected gesture image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
                GestureLandmarkOverlay(
                    result = result,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                GesturePreviewPlaceholder("Image preview will be here")
            }
            GestureStatusBar(
                text = status,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        Button(
            onClick = { imagePicker() },
            enabled = !isRecognizing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isRecognizing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.size(10.dp))
            }
            Text(if (imageBytes == null) "Chọn ảnh" else "Chọn ảnh khác")
        }

        GestureResultPanel(
            result = result,
            videoResult = null,
        )
    }
}

@Composable
private fun GestureVideoDemo() {
    val recognizer = rememberGestureVideoRecognizer()
    val scope = rememberCoroutineScope()

    var videoResult by remember { mutableStateOf<GestureVideoResult?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Chọn video có gesture tay để phân tích.") }

    val videoPicker = openVideoPicker { bytes ->
        videoResult = null
        if (bytes == null) {
            status = "Chưa chọn video."
            return@openVideoPicker
        }

        isProcessing = true
        status = "Đang lấy mẫu frame và chạy Gesture Recognizer..."
        scope.launch {
            videoResult = recognizer.recognizeVideo(bytes)
            status = when {
                videoResult == null -> "Chưa xử lý được video. Android implementation mới hỗ trợ ở bước này."
                videoResult?.recognitionResult?.gestures?.flatten()?.isEmpty() == true -> "Đã đọc video nhưng chưa nhận diện được gesture rõ."
                else -> "Analyzed ${videoResult?.analyzedFrames ?: 0} frames. ${videoResult?.recognitionResult?.bestGestureText().orEmpty()}"
            }
            isProcessing = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GesturePreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            val previewBytes = videoResult?.previewFrameBytes
            if (previewBytes != null) {
                Image(
                    bitmap = remember(previewBytes) { previewBytes.decodeToImageBitmap() },
                    contentDescription = "Gesture video sampled frame",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
                GestureLandmarkOverlay(
                    result = videoResult?.recognitionResult,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                GesturePreviewPlaceholder("Video frame preview will be here")
            }
            GestureStatusBar(
                text = status,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        Button(
            onClick = { videoPicker() },
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.size(10.dp))
            }
            Text(if (videoResult == null) "Chọn video" else "Chọn video khác")
        }

        GestureResultPanel(
            result = videoResult?.recognitionResult,
            videoResult = videoResult,
        )
    }
}

@Composable
private fun GesturePreviewSurface(
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
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Composable
private fun BoxScope.GesturePreviewPlaceholder(text: String) {
    Text(
        text = text,
        color = Color(0xFFD9DEE7),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.align(Alignment.Center),
    )
}

@Composable
private fun GestureLandmarkOverlay(
    result: GestureRecognitionResult?,
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

        output.hands.forEachIndexed { index, hand ->
            val lineColor = if (index % 2 == 0) Color(0xFF46D7A7) else Color(0xFF8AB4F8)
            output.connections.forEach { connection ->
                val start = hand.getOrNull(connection.start)
                val end = hand.getOrNull(connection.end)
                if (start != null && end != null) {
                    drawLine(
                        color = lineColor,
                        start = Offset(left + start.x * renderedWidth, top + start.y * renderedHeight),
                        end = Offset(left + end.x * renderedWidth, top + end.y * renderedHeight),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round,
                    )
                }
            }

            hand.forEachIndexed { landmarkIndex, point ->
                drawCircle(
                    color = if (landmarkIndex == 0) Color(0xFFFF6B6B) else Color(0xFFFFD166),
                    radius = if (landmarkIndex == 0) 7f else 5f,
                    center = Offset(left + point.x * renderedWidth, top + point.y * renderedHeight),
                )
            }
        }
    }
}

@Composable
private fun GestureStatusBar(
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
private fun GestureResultPanel(
    result: GestureRecognitionResult?,
    videoResult: GestureVideoResult?,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "GestureRecognizerResult",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Hands: ${result?.hands?.size ?: 0}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "Best gesture: ${result?.bestGestureText() ?: "-"}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
            val gestures = result?.gestures
                ?.mapIndexed { index, categories ->
                    val top = categories.sortedByDescending { it.score }.take(3)
                    "Hand ${index + 1}: " + top.joinToString { "${it.categoryName} ${(it.score * 100).toInt()}%" }
                }
                ?.joinToString(separator = "\n")
            if (!gestures.isNullOrBlank()) {
                Text(
                    text = gestures,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (videoResult != null) {
                Text(
                    text = "Video: ${videoResult.analyzedFrames} sampled frames, ${videoResult.durationMs} ms",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun GestureRecognitionResult.bestGestureText(): String? {
    val best = gestures.flatten().maxByOrNull { it.score } ?: return null
    return "${best.categoryName} (${(best.score * 100).toInt()}%)"
}

@Composable
private fun GesturePipelinePanel(mode: GestureInputMode) {
    TaskNextStepsPanel(
        nextSteps = listOf(
            when (mode) {
                GestureInputMode.Camera -> "CameraX live stream xử lý trong androidMain bằng GestureRecognizer LIVE_STREAM, có đổi Front/Back."
                GestureInputMode.Image -> "Ảnh được chọn từ picker, Android chạy GestureRecognizer IMAGE mode."
                GestureInputMode.Video -> "Video được chọn từ picker, Android lấy mẫu frame bằng MediaMetadataRetriever và chạy VIDEO mode."
            },
            "GestureRecognitionResult trả gestures, handedness, hand landmarks và connections về commonMain.",
            "Compose Canvas share UI vẽ hand skeleton overlay kèm label gesture/confidence.",
            "Bước sau: map gesture thành action UI hoặc thêm custom gesture classifier.",
        ),
    )
}
