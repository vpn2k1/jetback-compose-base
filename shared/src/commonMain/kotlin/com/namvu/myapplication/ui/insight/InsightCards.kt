package com.namvu.myapplication.ui.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.domain.insight.FinancialInsight
import com.namvu.myapplication.expense.domain.insight.InsightSeverity
import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.ui.base.component.surface.AppCard

@Composable
fun InsightCard(
    insight: FinancialInsight,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = severityLabel(insight.severity),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(severityColor(insight.severity).copy(alpha = 0.14f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelSmall,
                color = severityColor(insight.severity),
                fontWeight = FontWeight.SemiBold,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = insight.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                InsightMetricLine(insight = insight)
            }
        }
    }
}

@Composable
private fun InsightMetricLine(insight: FinancialInsight) {
    val amountText = insight.amount?.let { ExpenseFormatter.formatAmount(it) }
    val percentageText = insight.percentage?.let { percentage ->
        if (percentage > 1f) {
            "${((percentage * 10).toInt() / 10f)}x"
        } else {
            "${(percentage * 100).toInt()}%"
        }
    }
    val metric = listOfNotNull(amountText, percentageText).joinToString(" | ")
    if (metric.isNotBlank()) {
        Text(
            text = metric,
            style = MaterialTheme.typography.labelMedium,
            color = severityColor(insight.severity),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun severityColor(severity: InsightSeverity) = when (severity) {
    InsightSeverity.Info -> MaterialTheme.colorScheme.primary
    InsightSeverity.Positive -> MaterialTheme.colorScheme.secondary
    InsightSeverity.Warning -> MaterialTheme.colorScheme.tertiary
    InsightSeverity.Critical -> MaterialTheme.colorScheme.error
}

private fun severityLabel(severity: InsightSeverity): String {
    return when (severity) {
        InsightSeverity.Info -> "Info"
        InsightSeverity.Positive -> "Good"
        InsightSeverity.Warning -> "Watch"
        InsightSeverity.Critical -> "High"
    }
}
