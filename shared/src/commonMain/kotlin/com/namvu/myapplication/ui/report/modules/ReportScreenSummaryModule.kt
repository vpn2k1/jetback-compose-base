package com.namvu.myapplication.ui.report.modules

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.expense.domain.report.FinancialReport
import com.namvu.myapplication.ui.base.component.surface.AppCard

@Composable
internal fun ReportScreenSummaryModule(
    report: FinancialReport,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Text(report.summary.label, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = ExpenseFormatter.formatAmount(report.summary.totalMinor),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "${report.summary.expenseCount} expenses | ${ExpenseFormatter.formatAmount(report.summary.averageDailyMinor)} daily average",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
