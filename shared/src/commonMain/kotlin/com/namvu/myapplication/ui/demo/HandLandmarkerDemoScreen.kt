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
import com.namvu.myapplication.HandCameraFacing
import com.namvu.myapplication.HandLandmarkerCameraPreview
import com.namvu.myapplication.HandTrackingResult
import com.namvu.myapplication.HandVideoResult
import com.namvu.myapplication.openGalley
import com.namvu.myapplication.openVideoPicker
import com.namvu.myapplication.rememberHandLandmarkerEngine
import com.namvu.myapplication.rememberHandVideoLandmarker
import com.namvu.myapplication.rememberStaticHandLandmarker
import kotlinx.coroutines.launch
import kotlin.math.min

private enum class HandInputMode {
    Camera,
    Image,
    Video,
}

@Composable
fun HandLandmarkerDemoScreen(onBack: () -> Unit) {
    DemoScaffold(
        title = "Hand Landmarker",
        description = "Live camera, ảnh hoặc video với 21 landmarks mỗi bàn tay.",
        onBack = onBack,
    ) {
        var inputMode by remember { mutableStateOf(HandInputMode.Camera) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HandInputModeTabs(
                selectedMode = inputMode,
                onModeSelected = { inputMode = it },
            )

            when (inputMode) {
                HandInputMode.Camera -> HandCameraDemo()
                HandInputMode.Image -> HandImageDemo()
                HandInputMode.Video -> HandVideoDemo()
            }

            HandPipelinePanel(mode = inputMode)
        }
    }
}

@Composable
private fun HandInputModeTabs(
    selectedMode: HandInputMode,
    onModeSelected: (HandInputMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        HandInputMode.entries.forEach { mode ->
            val selected = mode == selectedMode
            Text(
                text = when (mode) {
                    HandInputMode.Camera -> "Camera"
                    HandInputMode.Image -> "Ảnh"
                    HandInputMode.Video -> "Video"
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
private fun HandCameraDemo() {
    val engine = rememberHandLandmarkerEngine()
    val result by engine.trackingResult
    val isRunning by engine.isRunning
    val error by engine.error
    val cameraFacing by engine.cameraFacing

    DisposableEffect(Unit) {
        onDispose { engine.stop() }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HandCameraFacingTabs(
            selectedFacing = cameraFacing,
            onFacingSelected = engine::setCameraFacing,
        )

        HandPreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            HandLandmarkerCameraPreview(
                engine = engine,
                modifier = Modifier.fillMaxSize(),
            )
            HandLandmarkOverlay(
                result = result,
                modifier = Modifier.fillMaxSize(),
            )
            HandStatusBar(
                text = when {
                    error != null -> error ?: ""
                    result?.hands?.isNotEmpty() == true -> "Tracking ${result?.hands?.size ?: 0} hand(s)"
                    isRunning -> "Đưa bàn tay vào camera..."
                    else -> "Đang khởi động camera..."
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        HandResultPanel(
            result = result,
            videoResult = null,
        )
    }
}

@Composable
private fun HandCameraFacingTabs(
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
private fun HandImageDemo() {
    val landmarker = rememberStaticHandLandmarker()
    val scope = rememberCoroutineScope()

    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var result by remember { mutableStateOf<HandTrackingResult?>(null) }
    var isDetecting by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Chọn ảnh có bàn tay để detect landmarks.") }

    val imagePicker = openGalley { bytes ->
        imageBytes = bytes
        result = null
        if (bytes == null) {
            status = "Chưa chọn ảnh."
            return@openGalley
        }

        isDetecting = true
        status = "Đang chạy MediaPipe Hand Landmarker..."
        scope.launch {
            result = landmarker.detect(bytes)
            status = when {
                result == null -> "Chưa xử lý được ảnh. Android implementation mới hỗ trợ ở bước này."
                result?.hands?.isEmpty() == true -> "Không phát hiện bàn tay rõ trong ảnh."
                else -> "Detected ${result?.hands?.size ?: 0} hand(s)."
            }
            isDetecting = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HandPreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            if (imageBytes != null) {
                Image(
                    bitmap = remember(imageBytes) { imageBytes!!.decodeToImageBitmap() },
                    contentDescription = "Selected hand image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
                HandLandmarkOverlay(
                    result = result,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                HandPreviewPlaceholder("Image preview will be here")
            }
            HandStatusBar(
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

        HandResultPanel(
            result = result,
            videoResult = null,
        )
    }
}

@Composable
private fun HandVideoDemo() {
    val landmarker = rememberHandVideoLandmarker()
    val scope = rememberCoroutineScope()

    var videoResult by remember { mutableStateOf<HandVideoResult?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Chọn video có bàn tay để phân tích.") }

    val videoPicker = openVideoPicker { bytes ->
        videoResult = null
        if (bytes == null) {
            status = "Chưa chọn video."
            return@openVideoPicker
        }

        isProcessing = true
        status = "Đang lấy mẫu frame và chạy Hand Landmarker..."
        scope.launch {
            videoResult = landmarker.detectVideo(bytes)
            status = when {
                videoResult == null -> "Chưa xử lý được video. Android implementation mới hỗ trợ ở bước này."
                videoResult?.trackingResult?.hands?.isEmpty() == true -> "Đã đọc video nhưng chưa thấy bàn tay rõ."
                else -> "Analyzed ${videoResult?.analyzedFrames ?: 0} frames, duration ${videoResult?.durationMs ?: 0} ms."
            }
            isProcessing = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HandPreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            val previewBytes = videoResult?.previewFrameBytes
            if (previewBytes != null) {
                Image(
                    bitmap = remember(previewBytes) { previewBytes.decodeToImageBitmap() },
                    contentDescription = "Hand video sampled frame",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
                HandLandmarkOverlay(
                    result = videoResult?.trackingResult,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                HandPreviewPlaceholder("Video frame preview will be here")
            }
            HandStatusBar(
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

        HandResultPanel(
            result = videoResult?.trackingResult,
            videoResult = videoResult,
        )
    }
}

@Composable
private fun HandPreviewSurface(
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
private fun BoxScope.HandPreviewPlaceholder(text: String) {
    Text(
        text = text,
        color = Color(0xFFD9DEE7),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.align(Alignment.Center),
    )
}

@Composable
private fun HandLandmarkOverlay(
    result: HandTrackingResult?,
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
private fun HandStatusBar(
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
private fun HandResultPanel(
    result: HandTrackingResult?,
    videoResult: HandVideoResult?,
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
                text = "HandLandmarkerResult",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Hands: ${result?.hands?.size ?: 0}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "Landmarks per hand: ${result?.hands?.firstOrNull()?.size ?: 0}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
            val handedness = result?.handednesses
                ?.mapIndexed { index, categories ->
                    val best = categories.maxByOrNull { it.score }
                    "Hand ${index + 1}: ${best?.categoryName ?: "-"} ${best?.score?.let { "(${(it * 100).toInt()}%)" } ?: ""}"
                }
                ?.joinToString(separator = "\n")
            if (!handedness.isNullOrBlank()) {
                Text(
                    text = handedness,
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

@Composable
private fun HandPipelinePanel(mode: HandInputMode) {
    TaskNextStepsPanel(
        nextSteps = listOf(
            when (mode) {
                HandInputMode.Camera -> "CameraX live stream xử lý trong androidMain bằng HandLandmarker LIVE_STREAM, có đổi Front/Back."
                HandInputMode.Image -> "Ảnh được chọn từ picker, Android chạy HandLandmarker IMAGE mode."
                HandInputMode.Video -> "Video được chọn từ picker, Android lấy mẫu frame bằng MediaMetadataRetriever và chạy VIDEO mode."
            },
            "HandTrackingResult trả landmarks, world landmarks, handedness và connections về commonMain.",
            "Compose Canvas share UI vẽ palm/finger skeleton overlay.",
            "Bước sau: thêm Gesture Recognizer hoặc smoothing cho tracking realtime.",
        ),
    )
}
