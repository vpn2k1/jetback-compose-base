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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.HandCameraFacing
import com.namvu.myapplication.ImageClassifierCameraPreview
import com.namvu.myapplication.ImageClassifierResult
import com.namvu.myapplication.ImageClassifierVideoResult
import com.namvu.myapplication.openGalley
import com.namvu.myapplication.openVideoPicker
import com.namvu.myapplication.rememberImageClassifierEngine
import com.namvu.myapplication.rememberImageClassifierVideoAnalyzer
import com.namvu.myapplication.rememberStaticImageClassifier
import kotlinx.coroutines.launch

private enum class ImageClassifierInputMode {
    Camera,
    Image,
    Video,
}

@Composable
fun ImageClassifierDemoScreen(onBack: () -> Unit) {
    DemoScaffold(
        title = "Image Classifier",
        description = "Phân loại ảnh, camera realtime hoặc video bằng EfficientNet-Lite0.",
        onBack = onBack,
    ) {
        var inputMode by remember { mutableStateOf(ImageClassifierInputMode.Camera) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ImageClassifierInputModeTabs(
                selectedMode = inputMode,
                onModeSelected = { inputMode = it },
            )

            when (inputMode) {
                ImageClassifierInputMode.Camera -> ImageClassifierCameraDemo()
                ImageClassifierInputMode.Image -> ImageClassifierImageDemo()
                ImageClassifierInputMode.Video -> ImageClassifierVideoDemo()
            }

            ImageClassifierPipelinePanel(mode = inputMode)
        }
    }
}

@Composable
private fun ImageClassifierInputModeTabs(
    selectedMode: ImageClassifierInputMode,
    onModeSelected: (ImageClassifierInputMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ImageClassifierInputMode.entries.forEach { mode ->
            val selected = mode == selectedMode
            Text(
                text = when (mode) {
                    ImageClassifierInputMode.Camera -> "Camera"
                    ImageClassifierInputMode.Image -> "Ảnh"
                    ImageClassifierInputMode.Video -> "Video"
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
private fun ImageClassifierCameraDemo() {
    val engine = rememberImageClassifierEngine()
    val result by engine.classificationResult
    val isRunning by engine.isRunning
    val error by engine.error
    val cameraFacing by engine.cameraFacing

    DisposableEffect(Unit) {
        onDispose { engine.stop() }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ImageClassifierCameraFacingTabs(
            selectedFacing = cameraFacing,
            onFacingSelected = engine::setCameraFacing,
        )

        ImageClassifierPreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            ImageClassifierCameraPreview(
                engine = engine,
                modifier = Modifier.fillMaxSize(),
            )
            ImageClassifierStatusBar(
                text = when {
                    error != null -> error ?: ""
                    result?.categories?.isNotEmpty() == true -> "Top result: ${result?.categories?.firstOrNull()?.categoryName.orEmpty()}"
                    isRunning -> "Đang phân loại camera..."
                    else -> "Đang khởi động camera..."
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        ImageClassifierResultPanel(
            result = result,
            videoResult = null,
        )
    }
}

@Composable
private fun ImageClassifierCameraFacingTabs(
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
private fun ImageClassifierImageDemo() {
    val classifier = rememberStaticImageClassifier()
    val scope = rememberCoroutineScope()

    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var result by remember { mutableStateOf<ImageClassifierResult?>(null) }
    var isClassifying by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Chọn ảnh để phân loại.") }

    val imagePicker = openGalley { bytes ->
        imageBytes = bytes
        result = null

        if (bytes == null) {
            status = "Chưa chọn ảnh."
            return@openGalley
        }

        isClassifying = true
        status = "Đang chạy MediaPipe Image Classifier..."
        scope.launch {
            val classified = classifier.classify(bytes)
            result = classified
            status = when {
                classified == null -> "Chưa xử lý được ảnh. Android implementation mới hỗ trợ classifier ở bước này."
                classified.categories.isEmpty() -> "Không có kết quả phân loại."
                else -> "Top result: ${classified.categories.firstOrNull()?.categoryName.orEmpty()}"
            }
            isClassifying = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ImageClassifierPreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            if (imageBytes != null) {
                Image(
                    bitmap = remember(imageBytes) { imageBytes!!.decodeToImageBitmap() },
                    contentDescription = "Selected classification image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            } else {
                ImageClassifierPreviewPlaceholder("Image classification preview will be here")
            }

            ImageClassifierStatusBar(
                text = status,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        Button(
            onClick = { imagePicker() },
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
            Text(if (imageBytes == null) "Chọn ảnh" else "Chọn ảnh khác")
        }

        ImageClassifierResultPanel(
            result = result,
            videoResult = null,
        )
    }
}

@Composable
private fun ImageClassifierVideoDemo() {
    val analyzer = rememberImageClassifierVideoAnalyzer()
    val scope = rememberCoroutineScope()

    var videoResult by remember { mutableStateOf<ImageClassifierVideoResult?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Chọn video để phân loại frame mẫu.") }

    val videoPicker = openVideoPicker { bytes ->
        videoResult = null
        if (bytes == null) {
            status = "Chưa chọn video."
            return@openVideoPicker
        }

        isProcessing = true
        status = "Đang lấy mẫu frame và chạy Image Classifier..."
        scope.launch {
            videoResult = analyzer.classifyVideo(bytes)
            status = when {
                videoResult == null -> "Chưa xử lý được video. Android implementation mới hỗ trợ ở bước này."
                videoResult?.classificationResult?.categories?.isEmpty() == true -> "Không có kết quả phân loại từ video."
                else -> "Analyzed ${videoResult?.analyzedFrames ?: 0} frames. Top: ${videoResult?.classificationResult?.categories?.firstOrNull()?.categoryName.orEmpty()}"
            }
            isProcessing = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ImageClassifierPreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        ) {
            val previewBytes = videoResult?.previewFrameBytes
            if (previewBytes != null) {
                Image(
                    bitmap = remember(previewBytes) { previewBytes.decodeToImageBitmap() },
                    contentDescription = "Image classifier video sampled frame",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            } else {
                ImageClassifierPreviewPlaceholder("Video frame preview will be here")
            }

            ImageClassifierStatusBar(
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

        ImageClassifierResultPanel(
            result = videoResult?.classificationResult,
            videoResult = videoResult,
        )
    }
}

@Composable
private fun ImageClassifierPreviewSurface(
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
private fun BoxScope.ImageClassifierPreviewPlaceholder(text: String) {
    Text(
        text = text,
        color = Color(0xFFD9DEE7),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.align(Alignment.Center),
    )
}

@Composable
private fun ImageClassifierStatusBar(
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
private fun ImageClassifierResultPanel(
    result: ImageClassifierResult?,
    videoResult: ImageClassifierVideoResult?,
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
                text = "ImageClassifierResult",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (result != null && result.imageWidth > 0 && result.imageHeight > 0) {
                Text(
                    text = "Input: ${result.imageWidth} x ${result.imageHeight}",
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
                            text = "Score ${formatClassifierScore(category.score)}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageClassifierPipelinePanel(mode: ImageClassifierInputMode) {
    TaskNextStepsPanel(
        nextSteps = listOf(
            when (mode) {
                ImageClassifierInputMode.Camera -> "CameraX live stream xử lý trong androidMain bằng ImageClassifier LIVE_STREAM, có đổi Front/Back."
                ImageClassifierInputMode.Image -> "Ảnh được chọn từ picker, Android chạy ImageClassifier IMAGE mode."
                ImageClassifierInputMode.Video -> "Video được chọn từ picker, Android lấy mẫu frame bằng MediaMetadataRetriever và chạy VIDEO mode."
            },
            "ImageClassifierResult trả top categories và confidence score về commonMain.",
            "UI commonMain dùng cùng result panel cho camera, ảnh và video.",
            "Bước sau: thêm threshold, allowlist/denylist hoặc model classifier khác.",
        ),
    )
}

private fun formatClassifierScore(score: Float): String {
    val scaled = (score * 1000).toInt() / 10f
    return "$scaled%"
}
