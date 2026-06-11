package com.namvu.myapplication.expense.domain.report

import com.namvu.myapplication.expense.domain.insight.FinancialInsight
import com.namvu.myapplication.expense.domain.model.Expense

data class ReportSummary(
    val period: ReportPeriod,
    val label: String,
    val totalMinor: Long,
    val expenseCount: Int,
    val averageDailyMinor: Long,
)

data class DashboardReports(
    val todayTotalMinor: Long,
    val weekTotalMinor: Long,
    val monthTotalMinor: Long,
    val yearTotalMinor: Long,
)

data class CategoryBreakdownItem(
    val categoryId: String,
    val categoryName: String,
    val amountMinor: Long,
    val expenseCount: Int,
    val share: Float,
)

data class SpendingTrendPoint(
    val key: String,
    val label: String,
    val amountMinor: Long,
)

data class FinancialReport(
    val summary: ReportSummary,
    val dashboard: DashboardReports,
    val categoryBreakdown: List<CategoryBreakdownItem>,
    val dailyTrend: List<SpendingTrendPoint>,
    val monthlyTrend: List<SpendingTrendPoint>,
    val topExpenses: List<Expense>,
    val insights: List<FinancialInsight> = emptyList(),
)
