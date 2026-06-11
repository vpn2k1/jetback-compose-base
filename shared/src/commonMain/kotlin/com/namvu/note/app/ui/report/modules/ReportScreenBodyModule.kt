package com.namvu.note.app.ui.report.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.namvu.note.app.expense.domain.report.FinancialReport
import com.namvu.note.app.expense.domain.report.ReportPeriod
import com.namvu.note.app.expense.presentation.report.ReportState
import com.namvu.note.app.ui.base.component.feedback.EmptyContent
import com.namvu.note.app.ui.insight.InsightCard
import com.namvu.note.app.ui.report.items.ReportScreenTopExpenseItem

@Composable
internal fun ReportScreenBodyModule(
    state: ReportState,
    report: FinancialReport,
    onPeriodSelected: (ReportPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ReportScreenPeriodPickerModule(
                selectedPeriod = state.selectedPeriod,
                onPeriodSelected = onPeriodSelected,
            )
        }
        item { ReportScreenDashboardModule(report = report) }
        item { ReportScreenSummaryModule(report = report) }
        if (report.insights.isNotEmpty()) {
            item { ReportScreenSectionTitleModule(title = "Financial insights") }
            items(report.insights, key = { it.id }) { insight -> InsightCard(insight = insight) }
        }
        item { ReportScreenCategoryBreakdownModule(items = report.categoryBreakdown) }
        item { ReportScreenTrendModule(title = "Daily spending", points = report.dailyTrend) }
        item { ReportScreenTrendModule(title = "Monthly trend", points = report.monthlyTrend) }
        item { ReportScreenSectionTitleModule(title = "Top expenses") }
        if (report.topExpenses.isEmpty()) {
            item {
                EmptyContent(
                    title = "No report data",
                    message = "Reports update automatically when local expenses exist for this period.",
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                )
            }
        } else {
            items(report.topExpenses, key = { it.id }) { expense ->
                ReportScreenTopExpenseItem(expense = expense)
            }
        }
    }
}

@Composable
private fun ReportScreenSectionTitleModule(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
}
