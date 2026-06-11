package com.namvu.myapplication.ui.report.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.expense.domain.report.FinancialReport
import com.namvu.myapplication.ui.base.component.surface.AppCard

@Composable
internal fun ReportScreenDashboardModule(
    report: FinancialReport,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReportScreenMetricItem("Today", report.dashboard.todayTotalMinor, Modifier.weight(1f))
            ReportScreenMetricItem("Week", report.dashboard.weekTotalMinor, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReportScreenMetricItem("Month", report.dashboard.monthTotalMinor, Modifier.weight(1f))
            ReportScreenMetricItem("Year", report.dashboard.yearTotalMinor, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ReportScreenMetricItem(
    label: String,
    amountMinor: Long,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier, contentPadding = PaddingValues(12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = ExpenseFormatter.formatAmount(amountMinor),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
