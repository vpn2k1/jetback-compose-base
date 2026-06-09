package com.namvu.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.navigation.AppNavHost
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
expect fun openGalley(result: (ByteArray?) -> Unit): () -> Unit

@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavHost()
//        val segmenter = rememberImageSegmenter()
//        val scope = rememberCoroutineScope()
//
//        var originalImageBytes by remember { mutableStateOf<ByteArray?>(null) }
//        var backgroundImageBytes by remember { mutableStateOf<ByteArray?>(null) }
//        var resultImageBytes by remember { mutableStateOf<ByteArray?>(null) }
//        var isLoading by remember { mutableStateOf(false) }
//
//        // Launcher để chọn ảnh gốc (người)
//        val launcherOriginal = openGalley { bytes ->
//            if (bytes != null) {
//                originalImageBytes = bytes
//                resultImageBytes = null // Xóa kết quả cũ
//            }
//        }
//
//        // Launcher để chọn ảnh nền mới
//        val launcherBackground = openGalley { bytes ->
//            if (bytes != null) {
//                backgroundImageBytes = bytes
//                resultImageBytes = null // Xóa kết quả cũ
//            }
//        }
//
//        // Hàm xử lý trộn ảnh
//        fun processReplaceBackground() {
//            val orgBytes = originalImageBytes
//            val bgBytes = backgroundImageBytes
//            if (orgBytes != null && bgBytes != null) {
//                print("processReplaceBackground")
//                isLoading = true
//                scope.launch {
//                    resultImageBytes = segmenter.replaceBackground(orgBytes, bgBytes)
//                    isLoading = false
//                }
//            }
//        }
//
//        Column(
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.background)
//                .safeContentPadding()
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState()),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Vùng điều khiển
//            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//                Button(onClick = { launcherOriginal() }) {
//                    Text(if (originalImageBytes == null) "Chọn ảnh người" else "Đổi ảnh người")
//                }
//                Button(
//                    onClick = { launcherBackground() },
//                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
//                ) {
//                    Text(if (backgroundImageBytes == null) "Chọn ảnh nền" else "Đổi ảnh nền")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Nút thực hiện xử lý (chỉ hiện khi đủ 2 ảnh)
//            if (originalImageBytes != null && backgroundImageBytes != null && resultImageBytes == null) {
//                Button(
//                    onClick = { processReplaceBackground() },
//                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
//                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
//                ) {
//                    if (isLoading) {
//                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onTertiary, modifier = Modifier.size(20.dp))
//                        Spacer(modifier = Modifier.width(10.dp))
//                        Text("Đang xử lý...")
//                    } else {
//                        Text("Thực hiện thay nền")
//                    }
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//
//            // Vùng hiển thị kết quả
//            resultImageBytes?.let {
//                Text("Kết quả:", style = MaterialTheme.typography.titleMedium)
//                Image(
//                    bitmap = it.decodeToImageBitmap(),
//                    contentDescription = "Result",
//                    modifier = Modifier.fillMaxWidth().height(300.dp).padding(16.dp),
//                    contentScale = ContentScale.Fit
//                )
//            } ?: run {
//                // Hiển thị xem trước 2 ảnh đầu vào
//                Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
//                    originalImageBytes?.let {
//                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
//                            Text("Ảnh người")
//                            Image(bitmap = it.decodeToImageBitmap(), contentDescription = null, modifier = Modifier.height(150.dp))
//                        }
//                    }
//                    backgroundImageBytes?.let {
//                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
//                            Text("Ảnh nền mới")
//                            Image(bitmap = it.decodeToImageBitmap(), contentDescription = null, modifier = Modifier.height(150.dp))
//                        }
//                    }
//                }
//            }
//        }
    }
}

@Composable
private fun FaceTrackerLiveScreen() {
    val engine = rememberFaceTrackerEngine()
    val result by engine.trackingResult
    val isRunning by engine.isRunning
    val error by engine.error

    DisposableEffect(engine) {
        engine.start()
        onDispose { engine.stop() }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            FaceTrackerCameraPreview(
                engine = engine,
                modifier = Modifier.fillMaxSize(),
            )
            FaceLandmarkOverlay(
                result = result,
                modifier = Modifier.fillMaxSize(),
            )
            Text(
                text = when {
                    error != null -> error ?: ""
                    result?.faces?.isNotEmpty() == true ->
                        "${result?.faces?.size ?: 0} face | ${result?.blendShapes?.firstOrNull()?.size ?: 0} blendshapes | ${result?.facialTransformationMatrixes?.size ?: 0} matrix"
                    isRunning -> "Looking for a face..."
                    else -> "Starting camera..."
                },
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0x99000000))
                    .safeContentPadding()
                    .padding(12.dp),
            )
        }
    }
}

@Composable
private fun FaceLandmarkerStaticScreen() {
    val faceLandmarker = rememberStaticFaceLandmarker()
    val scope = rememberCoroutineScope()

    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var landmarks by remember { mutableStateOf<FaceLandmarkerOutput?>(null) }
    var isDetecting by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Choose a face image to detect landmarks.") }

    val galleryLauncher = openGalley { bytes ->
        imageBytes = bytes
        landmarks = null
        if (bytes == null) {
            statusText = "No image selected."
            return@openGalley
        }

        isDetecting = true
        statusText = "Detecting face landmarks..."
        scope.launch {
            val result = faceLandmarker.detect(bytes)
            landmarks = result
            statusText = when {
                result == null -> "Could not process this image."
                result.faces.isEmpty() -> "No face landmarks detected."
                result.facialTransformationMatrixes.isEmpty() ->
                    "Detected ${result.faces.size} face with ${result.faces.first().size} landmarks."
                else ->
                    "Detected ${result.faces.size} face with ${result.faces.first().size} landmarks and ${result.facialTransformationMatrixes.size} transform matrix."
            }
            isDetecting = false
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF111318)),
                contentAlignment = Alignment.Center,
            ) {
                imageBytes?.let { bytes ->
                    FaceLandmarkImageOverlay(
                        imageBytes = bytes,
                        landmarks = landmarks,
                        modifier = Modifier.fillMaxSize(),
                    )
                } ?: Text(
                    text = "No image selected",
                    color = Color(0xFFD9DEE7),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { galleryLauncher() },
                enabled = !isDetecting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isDetecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
                Text(if (imageBytes == null) "Choose Image" else "Choose Another Image")
            }
        }
    }
}

@Composable
private fun FaceLandmarkImageOverlay(
    imageBytes: ByteArray,
    landmarks: FaceLandmarkerOutput?,
    modifier: Modifier = Modifier,
) {
    val imageBitmap = remember(imageBytes) {
        imageBytes.decodeToImageBitmap()
    }

    BoxWithConstraints(modifier = modifier) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Selected face image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )

        landmarks?.let { output ->
            Canvas(modifier = Modifier.fillMaxSize()) {
                val scale = min(
                    size.width / output.imageWidth.toFloat(),
                    size.height / output.imageHeight.toFloat(),
                )
                val renderedWidth = output.imageWidth * scale
                val renderedHeight = output.imageHeight * scale
                val left = (size.width - renderedWidth) / 2f
                val top = (size.height - renderedHeight) / 2f

                output.faces.forEach { face ->
                    output.connections.forEach { connection ->
                        val start = face.getOrNull(connection.start)
                        val end = face.getOrNull(connection.end)
                        if (start != null && end != null) {
                            drawLine(
                                color = Color(0xFF46D7A7),
                                start = Offset(
                                    x = left + start.x * renderedWidth,
                                    y = top + start.y * renderedHeight,
                                ),
                                end = Offset(
                                    x = left + end.x * renderedWidth,
                                    y = top + end.y * renderedHeight,
                                ),
                                strokeWidth = 1.5f,
                                cap = StrokeCap.Round,
                            )
                        }
                    }

                    face.forEach { point ->
                        drawCircle(
                            color = Color(0xFFFFD166),
                            radius = 2.3f,
                            center = Offset(
                                x = left + point.x * renderedWidth,
                                y = top + point.y * renderedHeight,
                            ),
                        )
                    }
                }
            }
        }
    }
}
