package com.namvu.note.app.stitch.expensejournal.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalColors
import com.namvu.note.app.stitch.expensejournal.stitchGlassGradient

@Composable
fun StitchExpenseJournalCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(stitchGlassGradient())
            .padding(18.dp),
        content = content,
    )
}

@Composable
fun StitchExpenseJournalMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    tone: Color = StitchExpenseJournalColors.Primary,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(StitchExpenseJournalColors.SurfaceHigh)
            .padding(14.dp),
    ) {
        Text(label, color = StitchExpenseJournalColors.OnSurfaceMuted, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Text(value, color = tone, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StitchExpenseJournalPill(
    text: String,
    tone: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(tone.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(tone),
        )
        Spacer(Modifier.width(8.dp))
        Text(text, color = tone, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun StitchExpenseJournalProgress(
    progress: Float,
    tone: Color,
    modifier: Modifier = Modifier,
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(9.dp)
            .clip(RoundedCornerShape(999.dp)),
        color = tone,
        trackColor = StitchExpenseJournalColors.Outline,
    )
}

@Composable
fun StitchExpenseJournalSparkline(
    points: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = StitchExpenseJournalColors.Primary,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp),
    ) {
        if (points.size < 2) return@Canvas
        val step = size.width / (points.lastIndex)
        val min = points.minOrNull() ?: 0f
        val max = points.maxOrNull() ?: 1f
        val range = (max - min).coerceAtLeast(1f)
        val offsets = points.mapIndexed { index, value ->
            Offset(
                x = index * step,
                y = size.height - ((value - min) / range * size.height),
            )
        }
        for (index in 0 until offsets.lastIndex) {
            drawLine(
                color = color,
                start = offsets[index],
                end = offsets[index + 1],
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
        drawCircle(color = color, radius = 7.dp.toPx(), center = offsets.last())
    }
}

@Composable
fun StitchExpenseJournalRing(
    progress: Float,
    tone: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(96.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(96.dp)) {
            drawArc(
                color = StitchExpenseJournalColors.Outline,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                color = tone,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round),
            )
        }
        Text(
            text = "${(progress * 100).toInt()}%",
            color = StitchExpenseJournalColors.OnSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun StitchExpenseJournalSectionTitle(title: String, action: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, color = StitchExpenseJournalColors.OnSurface, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        if (action != null) {
            Text(action, color = StitchExpenseJournalColors.Primary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
