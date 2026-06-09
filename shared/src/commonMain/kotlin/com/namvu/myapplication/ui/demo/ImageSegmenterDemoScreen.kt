package com.namvu.myapplication.ui.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.namvu.myapplication.CameraBackgroundReplacer
import com.namvu.myapplication.openGalley
import com.namvu.myapplication.rememberImageSegmenter
import kotlinx.coroutines.launch

private enum class SegmenterInputMode {
    Image,
    Camera,
}

private data class SegmenterColorOption(
    val id: String,
    val label: String,
    val color: Color,
    val argb: Int,
)

private val segmenterColors = listOf(
    SegmenterColorOption("green", "Green", Color(0xFF22C55E), 0xFF22C55E.toInt()),
    SegmenterColorOption("blue", "Blue", Color(0xFF2563EB), 0xFF2563EB.toInt()),
    SegmenterColorOption("pink", "Pink", Color(0xFFEC4899), 0xFFEC4899.toInt()),
    SegmenterColorOption("purple", "Purple", Color(0xFF7C3AED), 0xFF7C3AED.toInt()),
    SegmenterColorOption("yellow", "Yellow", Color(0xFFFACC15), 0xFFFACC15.toInt()),
    SegmenterColorOption("black", "Black", Color.Black, 0xFF000000.toInt()),
    SegmenterColorOption("white", "White", Color.White, 0xFFFFFFFF.toInt()),
)

@Composable
fun ImageSegmenterDemoScreen(onBack: () -> Unit) {
    DemoScaffold(
        title = "Image Segmenter",
        description = "Tách người khỏi nền và thay nền bằng màu.",
        onBack = onBack,
    ) {
        ImageSegmenterDemoContent()
    }
}

@Composable
private fun ImageSegmenterDemoContent() {
    var inputMode by remember { mutableStateOf(SegmenterInputMode.Image) }
    var selectedColor by remember { mutableStateOf(segmenterColors.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SegmenterInputModeTabs(
            selectedMode = inputMode,
            onModeSelected = { inputMode = it },
        )

        when (inputMode) {
            SegmenterInputMode.Image -> ImageSegmenterImageDemo(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it },
            )

            SegmenterInputMode.Camera -> ImageSegmenterCameraDemo(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it },
            )
        }

        ImageSegmenterPipelinePanel(
            mode = inputMode,
        )
    }
}

@Composable
private fun SegmenterInputModeTabs(
    selectedMode: SegmenterInputMode,
    onModeSelected: (SegmenterInputMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SegmenterInputMode.entries.forEach { mode ->
            val selected = mode == selectedMode
            Text(
                text = when (mode) {
                    SegmenterInputMode.Image -> "Ảnh"
                    SegmenterInputMode.Camera -> "Camera"
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
private fun ImageSegmenterImageDemo(
    selectedColor: SegmenterColorOption,
    onColorSelected: (SegmenterColorOption) -> Unit,
) {
    val segmenter = rememberImageSegmenter()
    val scope = rememberCoroutineScope()

    var originalImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var resultImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Chọn ảnh có người để bắt đầu Image Segmenter.") }

    fun runSegmentation(bytes: ByteArray, color: SegmenterColorOption) {
        isProcessing = true
        status = "Đang chạy MediaPipe Image Segmenter..."
        scope.launch {
            resultImageBytes = segmenter.replaceBackgroundWithColor(
                originalImageBytes = bytes,
                backgroundColorArgb = color.argb,
            )
            status = if (resultImageBytes != null) {
                "Đã thay nền bằng màu ${color.label}."
            } else {
                "Chưa xử lý được ảnh. Android implementation mới hỗ trợ MediaPipe ở bước này."
            }
            isProcessing = false
        }
    }

    val imagePicker = openGalley { bytes ->
        originalImageBytes = bytes
        resultImageBytes = null
        if (bytes == null) {
            status = "Chưa chọn ảnh."
            return@openGalley
        }
        runSegmentation(bytes, selectedColor)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ImageSegmenterPreview(
            originalImageBytes = originalImageBytes,
            resultImageBytes = resultImageBytes,
            selectedColor = selectedColor,
            status = status,
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { imagePicker() },
                enabled = !isProcessing,
                modifier = Modifier.weight(1f),
            ) {
                Text(if (originalImageBytes == null) "Chọn ảnh" else "Chọn ảnh khác")
            }

            Button(
                onClick = {
                    originalImageBytes?.let { runSegmentation(it, selectedColor) }
                },
                enabled = originalImageBytes != null && !isProcessing,
                modifier = Modifier.weight(1f),
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
                Text("Xử lý")
            }
        }

        SegmenterColorPicker(
            colors = segmenterColors,
            selectedColor = selectedColor,
            onColorSelected = { color ->
                onColorSelected(color)
                originalImageBytes?.let { runSegmentation(it, color) }
            },
            enabled = !isProcessing,
        )
    }
}

@Composable
private fun ImageSegmenterCameraDemo(
    selectedColor: SegmenterColorOption,
    onColorSelected: (SegmenterColorOption) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(selectedColor.color)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(8.dp),
                ),
        ) {
            CameraBackgroundReplacer(
                modifier = Modifier.fillMaxSize(),
                backgroundImageBytes = null,
                backgroundColorArgb = selectedColor.argb,
            )
            Text(
                text = "Live Image Segmenter - nền ${selectedColor.label}",
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

        SegmenterColorPicker(
            colors = segmenterColors,
            selectedColor = selectedColor,
            onColorSelected = onColorSelected,
            enabled = true,
        )
    }
}

@Composable
private fun ImageSegmenterPreview(
    originalImageBytes: ByteArray?,
    resultImageBytes: ByteArray?,
    selectedColor: SegmenterColorOption,
    status: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(selectedColor.color)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        when {
            resultImageBytes != null -> Image(
                bitmap = remember(resultImageBytes) { resultImageBytes.decodeToImageBitmap() },
                contentDescription = "Segmented result",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )

            originalImageBytes != null -> Image(
                bitmap = remember(originalImageBytes) { originalImageBytes.decodeToImageBitmap() },
                contentDescription = "Original image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )

            else -> Text(
                text = "Segmentation preview will be here",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
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
private fun SegmenterColorPicker(
    colors: List<SegmenterColorOption>,
    selectedColor: SegmenterColorOption,
    onColorSelected: (SegmenterColorOption) -> Unit,
    enabled: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Chọn màu nền",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(colors, key = { it.id }) { option ->
                    SegmenterColorItem(
                        option = option,
                        selected = option.id == selectedColor.id,
                        enabled = enabled,
                        onClick = { onColorSelected(option) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SegmenterColorItem(
    option: SegmenterColorOption,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
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
            maxLines = 1,
        )
    }
}

@Composable
private fun ImageSegmenterPipelinePanel(
    mode: SegmenterInputMode,
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
                if (mode == SegmenterInputMode.Image) {
                    "Input: ảnh người từ picker trong common UI."
                } else {
                    "Input: CameraX frame từ androidMain."
                },
                "Android engine: MediaPipe ImageSegmenter tạo category mask.",
                "Compositor: giữ foreground, thay background bằng màu đã chọn.",
                if (mode == SegmenterInputMode.Image) {
                    "Output: ảnh JPEG trả về commonMain để hiển thị."
                } else {
                    "Output: stream bitmap đã thay nền hiển thị realtime."
                },
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
