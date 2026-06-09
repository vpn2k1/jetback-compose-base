package com.namvu.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import kotlin.math.min

@Composable
fun FaceLandmarkOverlay(
    result: FaceTrackingResult?,
    modifier: Modifier = Modifier,
    landmarkColor: Color = Color(0xFFFFD166),
    connectionColor: Color = Color(0xFF46D7A7),
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

        output.faces.forEach { face ->
            output.connections.forEach { connection ->
                val start = face.getOrNull(connection.start)
                val end = face.getOrNull(connection.end)
                if (start != null && end != null) {
                    drawLine(
                        color = connectionColor,
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
                    color = landmarkColor,
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
