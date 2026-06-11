package com.namvu.note.app.ui.report.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.namvu.note.app.expense.domain.report.SpendingTrendPoint
import com.namvu.note.app.ui.base.component.surface.AppCard

@Composable
internal fun ReportScreenTrendModule(
    title: String,
    points: List<SpendingTrendPoint>,
    modifier: Modifier = Modifier,
) {
    val max = points.maxOfOrNull { it.amountMinor } ?: 0L
    AppCard(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.height(130.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            points.forEach { point ->
                ReportScreenTrendBarItem(point = point, maxAmountMinor = max, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ReportScreenTrendBarItem(
    point: SpendingTrendPoint,
    maxAmountMinor: Long,
    modifier: Modifier = Modifier,
) {
    val heightFraction = if (maxAmountMinor == 0L) 0f else point.amountMinor.toFloat() / maxAmountMinor
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
        Box(
            modifier = Modifier
                .width(18.dp)
                .height((88 * heightFraction).coerceAtLeast(4f).dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primary),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = point.label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}
