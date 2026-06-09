package com.namvu.myapplication.ui.demo

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.ArtFilterArController
import com.namvu.myapplication.ArtFilterOption
import com.namvu.myapplication.ArtFilterOverlay
import com.namvu.myapplication.ArtFilterType
import com.namvu.myapplication.FaceLandmarkOverlay
import com.namvu.myapplication.FaceTrackerCameraPreview
import com.namvu.myapplication.FaceTrackingResult
import com.namvu.myapplication.FaceVideoResult
import com.namvu.myapplication.defaultArtFilters
import com.namvu.myapplication.openVideoPicker
import com.namvu.myapplication.rememberFaceTrackerEngine
import com.namvu.myapplication.rememberFaceVideoLandmarker
import com.namvu.myapplication.sampleArtFilterFaceTrackingResult
import kotlinx.coroutines.launch

private enum class ArtFilterInputMode {
    Camera,
    Video,
    Sample,
}

@Composable
fun ArtFilterArDemoScreen(onBack: () -> Unit) {
    val controller = remember { ArtFilterArController() }
    val state = controller.state
    val selectedOption = defaultArtFilters.firstOrNull { it.type == state.selectedFilter }
        ?: defaultArtFilters.first()
    var inputMode by remember { mutableStateOf(ArtFilterInputMode.Camera) }

    DemoScaffold(
        title = "Art Filter AR",
        description = "AR artwork filters using face landmarks, blendshapes and facial transformation matrix.",
        onBack = onBack,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ArtFilterInputModeTabs(
                selectedMode = inputMode,
                onModeSelected = { inputMode = it },
            )

            when (inputMode) {
                ArtFilterInputMode.Camera -> ArtFilterCameraDemo(selectedFilter = state.selectedFilter)
                ArtFilterInputMode.Video -> ArtFilterVideoDemo(selectedFilter = state.selectedFilter)
                ArtFilterInputMode.Sample -> ArtFilterSampleDemo(
                    selectedFilter = state.selectedFilter,
                    selectedTitle = selectedOption.title,
                )
            }

            ArtFilterPicker(
                filters = defaultArtFilters,
                selectedFilter = state.selectedFilter,
                onFilterSelected = controller::selectFilter,
            )

            TaskNextStepsPanel(
                nextSteps = listOf(
                    "Live Camera uses the existing FaceLandmarker LIVE_STREAM engine and draws commonMain ArtFilterOverlay.",
                    "Video mode picks a video, samples frames in androidMain and maps FaceLandmarkerResult to FaceTrackingResult.",
                    "Current overlay is 2D placeholder; later it can be replaced with 3D objects/textures.",
                ),
            )
        }
    }
}

@Composable
private fun ArtFilterInputModeTabs(
    selectedMode: ArtFilterInputMode,
    onModeSelected: (ArtFilterInputMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ArtFilterInputMode.entries.forEach { mode ->
            val selected = mode == selectedMode
            Text(
                text = when (mode) {
                    ArtFilterInputMode.Camera -> "Camera"
                    ArtFilterInputMode.Video -> "Video"
                    ArtFilterInputMode.Sample -> "Sample"
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
private fun ArtFilterCameraDemo(selectedFilter: ArtFilterType) {
    val engine = rememberFaceTrackerEngine()
    val result by engine.trackingResult
    val isRunning by engine.isRunning
    val error by engine.error

    DisposableEffect(Unit) {
        onDispose { engine.stop() }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ArtFilterPreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(460.dp),
        ) {
            FaceTrackerCameraPreview(
                engine = engine,
                modifier = Modifier.fillMaxSize(),
            )
            ArtFaceOverlays(
                result = result,
                selectedFilter = selectedFilter,
                modifier = Modifier.fillMaxSize(),
            )
            ArtFilterStatusBar(
                text = when {
                    error != null -> error ?: ""
                    result?.faces?.isNotEmpty() == true -> "Tracking face with ${selectedFilter.name} filter"
                    isRunning -> "Point camera at your face"
                    else -> "Starting camera..."
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun ArtFilterVideoDemo(selectedFilter: ArtFilterType) {
    val videoLandmarker = rememberFaceVideoLandmarker()
    val scope = rememberCoroutineScope()

    var videoResult by remember { mutableStateOf<FaceVideoResult?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Chọn video có khuôn mặt để chạy Art Filter AR.") }

    val picker = openVideoPicker { bytes ->
        videoResult = null
        if (bytes == null) {
            status = "Chưa chọn video."
            return@openVideoPicker
        }

        isProcessing = true
        status = "Đang lấy mẫu frame và chạy FaceLandmarker..."
        scope.launch {
            videoResult = videoLandmarker.detectVideo(bytes)
            status = when {
                videoResult == null -> "Chưa xử lý được video. Android implementation mới hỗ trợ ở bước này."
                videoResult?.trackingResult?.faces?.isEmpty() == true -> "Đã đọc video nhưng chưa thấy mặt rõ."
                else -> "Analyzed ${videoResult?.analyzedFrames ?: 0} frames, duration ${videoResult?.durationMs ?: 0} ms."
            }
            isProcessing = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ArtFilterPreviewSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(460.dp),
        ) {
            val previewBytes = videoResult?.previewFrameBytes
            if (previewBytes != null) {
                Image(
                    bitmap = remember(previewBytes) { previewBytes.decodeToImageBitmap() },
                    contentDescription = "Art filter video sampled frame",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
                ArtFaceOverlays(
                    result = videoResult?.trackingResult,
                    selectedFilter = selectedFilter,
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
            ArtFilterStatusBar(
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
    }
}

@Composable
private fun ArtFilterSampleDemo(
    selectedFilter: ArtFilterType,
    selectedTitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ArtFilterPreview(
            selectedFilter = selectedFilter,
            selectedTitle = selectedTitle,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
        )
        ArtFilterDebugPreview(
            selectedFilter = selectedFilter,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        )
    }
}

@Composable
private fun ArtFilterPreview(
    selectedFilter: ArtFilterType,
    selectedTitle: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(previewColorForFilter(selectedFilter))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = selectedTitle,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = previewTextForFilter(selectedFilter),
                color = Color.White.copy(alpha = 0.86f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ArtFilterPreviewSurface(
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
private fun ArtFaceOverlays(
    result: FaceTrackingResult?,
    selectedFilter: ArtFilterType,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        FaceLandmarkOverlay(
            result = result,
            modifier = Modifier.fillMaxSize(),
            landmarkColor = Color(0xFFFFD166),
            connectionColor = Color(0xFF46D7A7),
        )
        ArtFilterOverlay(
            result = result,
            selectedFilter = selectedFilter,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ArtFilterStatusBar(
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
private fun ArtFilterPicker(
    filters: List<ArtFilterOption>,
    selectedFilter: ArtFilterType,
    onFilterSelected: (ArtFilterType) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Art filters",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(filters, key = { it.id }) { filter ->
                ArtFilterItem(
                    filter = filter,
                    selected = filter.type == selectedFilter,
                    onClick = { onFilterSelected(filter.type) },
                )
            }
        }
    }
}

@Composable
private fun ArtFilterItem(
    filter: ArtFilterOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp),
            ),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = if (selected) "${filter.title} selected" else filter.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = filter.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ArtFilterDebugPreview(
    selectedFilter: ArtFilterType,
    modifier: Modifier = Modifier,
) {
    val sampleResult = remember { sampleArtFilterFaceTrackingResult() }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF111318))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp),
            ),
    ) {
        FaceLandmarkOverlay(
            result = sampleResult,
            modifier = Modifier.fillMaxSize(),
            landmarkColor = Color(0xFFFFD166),
            connectionColor = Color(0xFF46D7A7),
        )
        ArtFilterOverlay(
            result = sampleResult,
            selectedFilter = selectedFilter,
            modifier = Modifier.fillMaxSize(),
        )
        Text(
            text = "Sample FaceTrackingResult overlay",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0x99000000))
                .padding(10.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun previewColorForFilter(filter: ArtFilterType): Color =
    when (filter) {
        ArtFilterType.None -> Color(0xFF1F2937)
        ArtFilterType.StarOnForehead -> Color(0xFF5B4B1F)
        ArtFilterType.HeartOnCheek -> Color(0xFF6D1B3F)
        ArtFilterType.MonkeyOnFace -> Color(0xFF5A3217)
        ArtFilterType.Glasses -> Color(0xFF1F2937)
    }

private fun previewTextForFilter(filter: ArtFilterType): String =
    when (filter) {
        ArtFilterType.None -> "No artwork filter selected. Landmarks can still be drawn for debugging."
        ArtFilterType.StarOnForehead -> "Simple star shape above landmark 10 on the forehead."
        ArtFilterType.HeartOnCheek -> "Heart placeholder anchored on cheek landmark 234."
        ArtFilterType.MonkeyOnFace -> "Monkey face placeholder centered around nose landmark 1."
        ArtFilterType.Glasses -> "Two rings around eye landmarks 33 and 263."
    }
