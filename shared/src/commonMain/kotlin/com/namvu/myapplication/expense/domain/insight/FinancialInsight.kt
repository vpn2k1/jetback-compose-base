package com.namvu.myapplication.expense.domain.insight

data class FinancialInsight(
    val id: String,
    val type: InsightType,
    val title: String,
    val description: String,
    val severity: InsightSeverity,
    val amount: Long?,
    val percentage: Float?,
    val createdAt: Long,
)

enum class InsightType {
    SpendingComparison,
    Category,
    WeekendWeekday,
    HighestSpendingDay,
    RepeatedSpending,
    Forecast,
    Anomaly,
}

enum class InsightSeverity {
    Info,
    Positive,
    Warning,
    Critical,
}
