package com.namvu.myapplication.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.ExpenseJournalGraph
import com.namvu.myapplication.expense.domain.model.Expense
import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.expense.domain.report.CategoryBreakdownItem
import com.namvu.myapplication.expense.domain.report.FinancialReport
import com.namvu.myapplication.expense.domain.report.ReportPeriod
import com.namvu.myapplication.expense.domain.report.SpendingTrendPoint
import com.namvu.myapplication.expense.presentation.report.ReportState
import com.namvu.myapplication.expense.presentation.report.ReportViewModel
import com.namvu.myapplication.ui.base.component.feedback.EmptyContent
import com.namvu.myapplication.ui.base.component.feedback.ErrorContent
import com.namvu.myapplication.ui.base.component.feedback.LoadingContent
import com.namvu.myapplication.ui.base.component.layout.AppScaffold
import com.namvu.myapplication.ui.base.component.surface.AppCard
import com.namvu.myapplication.ui.insight.InsightCard

@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = remember {
        ReportViewModel(ExpenseJournalGraph.reportUseCases)
    },
) {
    ReportContent(
        state = viewModel.state,
        onPeriodSelected = viewModel::onPeriodSelected,
        onRetryClick = viewModel::loadReport,
        modifier = modifier,
    )
}

@Composable
private fun ReportContent(
    state: ReportState,
    onPeriodSelected: (ReportPeriod) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        title = "Reports",
        modifier = modifier,
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
            state.errorMessage != null -> ErrorContent(
                message = state.errorMessage,
                onRetryClick = onRetryClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
            state.report == null -> EmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
            else -> ReportBody(
                state = state,
                report = state.report,
                onPeriodSelected = onPeriodSelected,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun ReportBody(
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
            PeriodPicker(
                selectedPeriod = state.selectedPeriod,
                onPeriodSelected = onPeriodSelected,
            )
        }
        item {
            DashboardCards(report = report)
        }
        item {
            SummaryCard(report = report)
        }
        if (report.insights.isNotEmpty()) {
            item {
                Text(
                    text = "Financial insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            items(
                items = report.insights,
                key = { it.id },
            ) { insight ->
                InsightCard(insight = insight)
            }
        }
        item {
            CategoryBreakdownCard(items = report.categoryBreakdown)
        }
        item {
            TrendCard(
                title = "Daily spending",
                points = report.dailyTrend,
            )
        }
        item {
            TrendCard(
                title = "Monthly trend",
                points = report.monthlyTrend,
            )
        }
        item {
            Text(
                text = "Top expenses",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (report.topExpenses.isEmpty()) {
            item {
                EmptyContent(
                    title = "No report data",
                    message = "Reports update automatically when local expenses exist for this period.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                )
            }
        } else {
            items(
                items = report.topExpenses,
                key = { it.id },
            ) { expense ->
                TopExpenseRow(expense = expense)
            }
        }
    }
}

@Composable
private fun PeriodPicker(
    selectedPeriod: ReportPeriod,
    onPeriodSelected: (ReportPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(
            ReportPeriod.TODAY to "Today",
            ReportPeriod.THIS_WEEK to "Week",
            ReportPeriod.THIS_MONTH to "Month",
            ReportPeriod.THIS_YEAR to "Year",
        ).forEach { (period, label) ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { Text(text = label) },
            )
        }
    }
}

@Composable
private fun DashboardCards(
    report: FinancialReport,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Today", report.dashboard.todayTotalMinor, Modifier.weight(1f))
            MetricCard("Week", report.dashboard.weekTotalMinor, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Month", report.dashboard.monthTotalMinor, Modifier.weight(1f))
            MetricCard("Year", report.dashboard.yearTotalMinor, Modifier.weight(1f))
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    amountMinor: Long,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
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

@Composable
private fun SummaryCard(
    report: FinancialReport,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Text(
            text = report.summary.label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
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

@Composable
private fun CategoryBreakdownCard(
    items: List<CategoryBreakdownItem>,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Text(
            text = "Category breakdown",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (items.isEmpty()) {
            Text(
                text = "No category spending in this period.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.forEach { item ->
                    BreakdownRow(item = item)
                }
            }
        }
    }
}

@Composable
private fun BreakdownRow(item: CategoryBreakdownItem) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = item.categoryName, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = ExpenseFormatter.formatAmount(item.amountMinor),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        LinearProgressIndicator(
            progress = { item.share },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TrendCard(
    title: String,
    points: List<SpendingTrendPoint>,
    modifier: Modifier = Modifier,
) {
    val max = points.maxOfOrNull { it.amountMinor } ?: 0L
    AppCard(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            points.forEach { point ->
                TrendBar(
                    point = point,
                    maxAmountMinor = max,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun TrendBar(
    point: SpendingTrendPoint,
    maxAmountMinor: Long,
    modifier: Modifier = Modifier,
) {
    val heightFraction = if (maxAmountMinor == 0L) 0f else point.amountMinor.toFloat() / maxAmountMinor
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Box(
            modifier = Modifier
                .width(18.dp)
                .height((88 * heightFraction).coerceAtLeast(4f).dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primary),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = point.label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
        )
    }
}

@Composable
private fun TopExpenseRow(
    expense: Expense,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = expense.categoryId,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = ExpenseFormatter.formatAmount(expense.amountMinor),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
