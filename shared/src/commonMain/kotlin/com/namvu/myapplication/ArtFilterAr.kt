package com.namvu.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

enum class ArtFilterType {
    None,
    StarOnForehead,
    HeartOnCheek,
    MonkeyOnFace,
    Glasses,
}

data class ArtFilterOption(
    val id: String,
    val title: String,
    val description: String,
    val type: ArtFilterType,
)

val defaultArtFilters = listOf(
    ArtFilterOption(
        id = "none",
        title = "None",
        description = "Only face landmarks.",
        type = ArtFilterType.None,
    ),
    ArtFilterOption(
        id = "star",
        title = "Star",
        description = "Simple star on forehead.",
        type = ArtFilterType.StarOnForehead,
    ),
    ArtFilterOption(
        id = "heart",
        title = "Heart",
        description = "Heart on cheek.",
        type = ArtFilterType.HeartOnCheek,
    ),
    ArtFilterOption(
        id = "monkey",
        title = "Monkey",
        description = "Monkey face placeholder.",
        type = ArtFilterType.MonkeyOnFace,
    ),
    ArtFilterOption(
        id = "glasses",
        title = "Glasses",
        description = "Glasses around both eyes.",
        type = ArtFilterType.Glasses,
    ),
)

data class ArtFilterArState(
    val selectedFilter: ArtFilterType = ArtFilterType.None,
    val isCameraRunning: Boolean = false,
    val errorMessage: String? = null,
)

class ArtFilterArController {
    var state by mutableStateOf(ArtFilterArState())
        private set

    fun selectFilter(filter: ArtFilterType) {
        state = state.copy(selectedFilter = filter)
    }

    fun setCameraRunning(running: Boolean) {
        state = state.copy(isCameraRunning = running)
    }

    fun setError(message: String?) {
        state = state.copy(errorMessage = message)
    }
}

@Composable
fun ArtFilterOverlay(
    result: FaceTrackingResult?,
    selectedFilter: ArtFilterType,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val output = result ?: return@Canvas
        if (output.imageWidth <= 0 || output.imageHeight <= 0) return@Canvas
        val face = output.faces.firstOrNull() ?: return@Canvas

        val scale = min(
            size.width / output.imageWidth.toFloat(),
            size.height / output.imageHeight.toFloat(),
        )
        val renderedWidth = output.imageWidth * scale
        val renderedHeight = output.imageHeight * scale
        val left = (size.width - renderedWidth) / 2f
        val top = (size.height - renderedHeight) / 2f

        fun pointAt(index: Int): Offset? {
            val point = face.getOrNull(index) ?: return null
            return Offset(
                x = left + point.x * renderedWidth,
                y = top + point.y * renderedHeight,
            )
        }

        when (selectedFilter) {
            ArtFilterType.None -> Unit
            ArtFilterType.StarOnForehead -> {
                val forehead = pointAt(10) ?: return@Canvas
                val center = forehead - Offset(0f, renderedHeight * 0.04f)
                drawStar(
                    center = center,
                    outerRadius = renderedWidth * 0.045f,
                    innerRadius = renderedWidth * 0.021f,
                    fill = Color(0xFFFFD166),
                    stroke = Color(0xFF7C4D00),
                )
            }
            ArtFilterType.HeartOnCheek -> {
                val cheek = pointAt(234) ?: pointAt(454) ?: return@Canvas
                val heartSize = renderedWidth * 0.038f
                val path = Path().apply {
                    moveTo(cheek.x, cheek.y + heartSize * 0.72f)
                    cubicTo(
                        cheek.x - heartSize * 1.35f,
                        cheek.y - heartSize * 0.12f,
                        cheek.x - heartSize * 0.82f,
                        cheek.y - heartSize * 1.24f,
                        cheek.x,
                        cheek.y - heartSize * 0.48f,
                    )
                    cubicTo(
                        cheek.x + heartSize * 0.82f,
                        cheek.y - heartSize * 1.24f,
                        cheek.x + heartSize * 1.35f,
                        cheek.y - heartSize * 0.12f,
                        cheek.x,
                        cheek.y + heartSize * 0.72f,
                    )
                    close()
                }
                drawPath(path = path, color = Color(0xFFE91E63))
                drawPath(path = path, color = Color.White.copy(alpha = 0.85f), style = Stroke(width = 2f))
            }
            ArtFilterType.MonkeyOnFace -> {
                val nose = pointAt(1) ?: return@Canvas
                val leftCheek = pointAt(234)
                val rightCheek = pointAt(454)
                val faceRadius = if (leftCheek != null && rightCheek != null) {
                    ((rightCheek.x - leftCheek.x).coerceAtLeast(renderedWidth * 0.12f)) * 0.28f
                } else {
                    renderedWidth * 0.085f
                }
                val center = nose + Offset(0f, renderedHeight * 0.015f)
                drawCircle(color = Color(0xFF8D5524), radius = faceRadius, center = center)
                drawCircle(color = Color(0xFFC68642), radius = faceRadius * 0.62f, center = center + Offset(0f, faceRadius * 0.18f))
                drawCircle(color = Color(0xFF8D5524), radius = faceRadius * 0.42f, center = center - Offset(faceRadius * 0.82f, faceRadius * 0.2f))
                drawCircle(color = Color(0xFF8D5524), radius = faceRadius * 0.42f, center = center + Offset(faceRadius * 0.82f, -faceRadius * 0.2f))
                drawCircle(color = Color.Black, radius = faceRadius * 0.08f, center = center - Offset(faceRadius * 0.22f, faceRadius * 0.1f))
                drawCircle(color = Color.Black, radius = faceRadius * 0.08f, center = center + Offset(faceRadius * 0.22f, -faceRadius * 0.1f))
                drawOval(
                    color = Color(0xFF5D3314),
                    topLeft = center + Offset(-faceRadius * 0.18f, faceRadius * 0.12f),
                    size = Size(faceRadius * 0.36f, faceRadius * 0.22f),
                )
            }
            ArtFilterType.Glasses -> {
                val leftEye = pointAt(33) ?: return@Canvas
                val rightEye = pointAt(263) ?: return@Canvas
                val eyeDistance = (rightEye.x - leftEye.x).coerceAtLeast(renderedWidth * 0.1f)
                val radius = eyeDistance * 0.18f
                drawCircle(
                    color = Color(0xFF111318).copy(alpha = 0.65f),
                    radius = radius,
                    center = leftEye,
                    style = Stroke(width = 6f),
                )
                drawCircle(
                    color = Color(0xFF111318).copy(alpha = 0.65f),
                    radius = radius,
                    center = rightEye,
                    style = Stroke(width = 6f),
                )
                drawLine(
                    color = Color(0xFF111318).copy(alpha = 0.75f),
                    start = leftEye + Offset(radius, 0f),
                    end = rightEye - Offset(radius, 0f),
                    strokeWidth = 5f,
                )
            }
        }
    }
}

fun sampleArtFilterFaceTrackingResult(): FaceTrackingResult {
    val face = MutableList(478) { index ->
        val angle = (index / 478.0) * PI * 2.0
        FaceLandmarkPoint(
            x = (0.5 + cos(angle) * 0.18).toFloat(),
            y = (0.5 + sin(angle) * 0.25).toFloat(),
            z = 0f,
        )
    }.apply {
        this[10] = FaceLandmarkPoint(0.50f, 0.22f, 0f)
        this[1] = FaceLandmarkPoint(0.50f, 0.47f, 0f)
        this[33] = FaceLandmarkPoint(0.41f, 0.38f, 0f)
        this[263] = FaceLandmarkPoint(0.59f, 0.38f, 0f)
        this[234] = FaceLandmarkPoint(0.34f, 0.51f, 0f)
        this[454] = FaceLandmarkPoint(0.66f, 0.51f, 0f)
        this[152] = FaceLandmarkPoint(0.50f, 0.72f, 0f)
    }
    return FaceTrackingResult(
        imageWidth = 720,
        imageHeight = 960,
        faces = listOf(face),
        blendShapes = emptyList(),
        facialTransformationMatrixes = emptyList(),
        connections = listOf(
            FaceLandmarkConnection(10, 33),
            FaceLandmarkConnection(10, 263),
            FaceLandmarkConnection(33, 1),
            FaceLandmarkConnection(263, 1),
            FaceLandmarkConnection(234, 1),
            FaceLandmarkConnection(454, 1),
            FaceLandmarkConnection(1, 152),
        ),
        timestampMs = 0L,
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    fill: Color,
    stroke: Color,
) {
    val path = Path()
    repeat(10) { index ->
        val radius = if (index % 2 == 0) outerRadius else innerRadius
        val angle = -PI / 2.0 + index * PI / 5.0
        val point = Offset(
            x = center.x + cos(angle).toFloat() * radius,
            y = center.y + sin(angle).toFloat() * radius,
        )
        if (index == 0) {
            path.moveTo(point.x, point.y)
        } else {
            path.lineTo(point.x, point.y)
        }
    }
    path.close()
    drawPath(path = path, color = fill)
    drawPath(path = path, color = stroke, style = Stroke(width = 2f))
}
