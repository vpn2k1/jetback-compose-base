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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.PoseLandmarkerCameraPreview
import com.namvu.myapplication.PoseTrackingResult
import com.namvu.myapplication.PoseVideoResult
import com.namvu.myapplication.openVideoPicker
import com.namvu.myapplication.rememberPoseLandmarkerEngine
import com.namvu.myapplication.rememberPoseVideoLandmarker
import kotlinx.coroutines.launch
import kotlin.math.min

private enum class PoseInputMode {
    Camera,
    Video,
}

@Composable
fun PoseLandmarkerDemoScreen(onBack: () -> Unit) {
    DemoScaffold(
        title = "Pose Landmarker",
        description = "Camera realtime hoặc chọn video để vẽ skeleton pose.",
        onBack = onBack,
    ) {
        var inputMode by remember { mutableStateOf(PoseInputMode.Camera) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PoseInputModeTabs(
                selectedMode = inputMode,
                onModeSelected = { inputMode = it },
            )

            when (inputMode) {
                PoseInputMode.Camera -> PoseCameraDemo()
                PoseInputMode.Video -> PoseVideoDemo()
            }

            PosePipelinePanel(mode = inputMode)
        }
    }
}

@Composable
private fun PoseInputModeTabs(
    selectedMode: PoseInputMode,
    onModeSelected: (PoseInputMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PoseInputMode.entries.forEach { mode ->
            val selected = mode == selectedMode
            Text(
                text = when (mode) {
                    PoseInputMode.Camera -> "Camera"
                    PoseInputMode.Video -> "Video"
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
private fun PoseCameraDemo() {
    val engine = rememberPoseLandmarkerEngine()
    val result by engine.trackingResult
    val isRunning by engine.isRunning
    val error by engine.error

    DisposableEffect(Unit) {
        onDispose { engine.stop() }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PosePreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            PoseLandmarkerCameraPreview(
                engine = engine,
                modifier = Modifier.fillMaxSize(),
            )
            PoseSkeletonOverlay(
                result = result,
                modifier = Modifier.fillMaxSize(),
            )
            PoseStatusBar(
                text = when {
                    error != null -> error ?: ""
                    result?.poses?.isNotEmpty() == true -> "Tracking ${result?.poses?.size ?: 0} pose"
                    isRunning -> "Looking for a pose..."
                    else -> "Starting camera..."
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        PoseResultPanel(
            result = result,
            videoResult = null,
        )
    }
}

@Composable
private fun PoseVideoDemo() {
    val videoLandmarker = rememberPoseVideoLandmarker()
    val scope = rememberCoroutineScope()

    var videoResult by remember { mutableStateOf<PoseVideoResult?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Chọn video để phân tích Pose Landmarker.") }

    val picker = openVideoPicker { bytes ->
        videoResult = null
        if (bytes == null) {
            status = "Chưa chọn video."
            return@openVideoPicker
        }
        isProcessing = true
        status = "Đang lấy mẫu frame và chạy Pose Landmarker..."
        scope.launch {
            videoResult = videoLandmarker.detectVideo(bytes)
            status = when {
                videoResult == null -> "Chưa xử lý được video. Android implementation mới hỗ trợ ở bước này."
                videoResult?.trackingResult?.poses?.isEmpty() == true -> "Đã đọc video nhưng chưa thấy pose rõ."
                else -> "Analyzed ${videoResult?.analyzedFrames ?: 0} frames, duration ${videoResult?.durationMs ?: 0} ms."
            }
            isProcessing = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PosePreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            val previewBytes = videoResult?.previewFrameBytes
            if (previewBytes != null) {
                Image(
                    bitmap = remember(previewBytes) { previewBytes.decodeToImageBitmap() },
                    contentDescription = "Pose video sampled frame",
                    modifier = Modifier.fillMaxSize(),
                )
                PoseSkeletonOverlay(
                    result = videoResult?.trackingResult,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Text(
                    text = "Video frame preview will be here",
                    color = Color(0xFFD9DEE7),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            PoseStatusBar(
                text = status,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        Button(
            onClick = { picker() },
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

        PoseResultPanel(
            result = videoResult?.trackingResult,
            videoResult = videoResult,
        )
    }
}

@Composable
private fun PosePreviewSurface(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit,
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
private fun PoseSkeletonOverlay(
    result: PoseTrackingResult?,
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

        output.poses.forEach { pose ->
            output.connections.forEach { connection ->
                val start = pose.getOrNull(connection.start)
                val end = pose.getOrNull(connection.end)
                if (start != null && end != null) {
                    drawLine(
                        color = Color(0xFF46D7A7),
                        start = Offset(left + start.x * renderedWidth, top + start.y * renderedHeight),
                        end = Offset(left + end.x * renderedWidth, top + end.y * renderedHeight),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round,
                    )
                }
            }

            pose.forEach { point ->
                drawCircle(
                    color = Color(0xFFFFD166),
                    radius = 5f,
                    center = Offset(left + point.x * renderedWidth, top + point.y * renderedHeight),
                )
            }
        }
    }
}

@Composable
private fun PoseStatusBar(
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
private fun PoseResultPanel(
    result: PoseTrackingResult?,
    videoResult: PoseVideoResult?,
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
                text = "PoseLandmarkerResult",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Poses: ${result?.poses?.size ?: 0}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "Landmarks: ${result?.poses?.firstOrNull()?.size ?: 0}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
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
private fun PosePipelinePanel(mode: PoseInputMode) {
    TaskNextStepsPanel(
        nextSteps = listOf(
            if (mode == PoseInputMode.Camera) {
                "CameraX frames được xử lý trong androidMain bằng PoseLandmarker LIVE_STREAM."
            } else {
                "Video được chọn từ picker, androidMain lấy mẫu frame bằng MediaMetadataRetriever."
            },
            "PoseTrackingResult trả landmarks và connections về commonMain.",
            "Compose Canvas vẽ skeleton overlay trên preview.",
            "Bước sau: thêm timeline video và smoothing cho camera realtime.",
        ),
    )
}
