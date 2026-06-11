package com.namvu.myapplication.expense.domain.report

import com.namvu.myapplication.expense.domain.budget.BudgetDraft
import com.namvu.myapplication.expense.domain.budget.BudgetProgress
import com.namvu.myapplication.expense.domain.budget.BudgetRepository
import com.namvu.myapplication.expense.domain.insight.FinancialInsightUseCases
import com.namvu.myapplication.expense.domain.model.Expense
import com.namvu.myapplication.expense.domain.repository.ExpenseRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ReportUseCases(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository,
    private val insightUseCases: FinancialInsightUseCases,
    private val clock: () -> Long = currentTimeMillisProvider(),
) {
    suspend fun loadReport(
        period: ReportPeriod,
        customRange: ReportDateRange? = null,
    ): FinancialReport {
        val now = clock()
        val expenses = expenseRepository.getExpenses()
        val categories = expenseRepository.getCategories().associateBy { it.id }
        val range = ReportPeriodCalculator.rangeFor(period, now, customRange)
        val periodExpenses = expenses.filter { range.contains(it.createdAtMillis) }
        val total = periodExpenses.sumOf { it.amountMinor }
        val dayCount = maxOf(1L, (range.endExclusiveMillis - range.startMillis) / MILLIS_PER_DAY)

        return FinancialReport(
            summary = ReportSummary(
                period = period,
                label = ReportPeriodCalculator.labelFor(period),
                totalMinor = total,
                expenseCount = periodExpenses.size,
                averageDailyMinor = total / dayCount,
            ),
            dashboard = loadDashboard(expenses, now),
            categoryBreakdown = periodExpenses
                .groupBy { it.categoryId }
                .map { (categoryId, groupedExpenses) ->
                    val amount = groupedExpenses.sumOf { it.amountMinor }
                    CategoryBreakdownItem(
                        categoryId = categoryId,
                        categoryName = categories[categoryId]?.name ?: categoryId,
                        amountMinor = amount,
                        expenseCount = groupedExpenses.size,
                        share = if (total == 0L) 0f else amount.toFloat() / total,
                    )
                }
                .sortedByDescending { it.amountMinor },
            dailyTrend = dailyTrend(periodExpenses, range),
            monthlyTrend = monthlyTrend(expenses, now),
            topExpenses = periodExpenses.sortedByDescending { it.amountMinor }.take(5),
            insights = insightUseCases.loadInsights(),
        )
    }

    suspend fun loadBudgetProgress(): List<BudgetProgress> {
        val expenses = expenseRepository.getExpenses()
        return budgetRepository.getBudgets().map { budget ->
            val spent = expenses
                .filter { expense ->
                    val monthMatches = CivilDate.fromEpochMillis(expense.createdAtMillis).monthKey() == budget.periodMonthKey
                    val categoryMatches = budget.categoryId == null || expense.categoryId == budget.categoryId
                    monthMatches && categoryMatches
                }
                .sumOf { it.amountMinor }

            BudgetProgress(
                budget = budget,
                spentMinor = spent,
            )
        }
    }

    suspend fun saveMonthlyBudget(amountMinor: Long) {
        val nowMonth = CivilDate.fromEpochMillis(clock()).monthKey()
        budgetRepository.upsertBudget(
            BudgetDraft(
                name = "Monthly budget",
                periodMonthKey = nowMonth,
                amountMinor = amountMinor,
            ),
        )
    }

    private fun loadDashboard(expenses: List<Expense>, now: Long): DashboardReports {
        return DashboardReports(
            todayTotalMinor = totalFor(expenses, ReportPeriod.TODAY, now),
            weekTotalMinor = totalFor(expenses, ReportPeriod.THIS_WEEK, now),
            monthTotalMinor = totalFor(expenses, ReportPeriod.THIS_MONTH, now),
            yearTotalMinor = totalFor(expenses, ReportPeriod.THIS_YEAR, now),
        )
    }

    private fun totalFor(expenses: List<Expense>, period: ReportPeriod, now: Long): Long {
        val range = ReportPeriodCalculator.rangeFor(period, now)
        return expenses.filter { range.contains(it.createdAtMillis) }.sumOf { it.amountMinor }
    }

    private fun dailyTrend(expenses: List<Expense>, range: ReportDateRange): List<SpendingTrendPoint> {
        val grouped = expenses.groupBy { CivilDate.fromEpochMillis(it.createdAtMillis).key() }
        val dayCount = ((range.endExclusiveMillis - range.startMillis) / MILLIS_PER_DAY)
            .coerceAtMost(31)
            .toInt()

        return List(dayCount) { index ->
            val date = CivilDate.fromEpochMillis(range.startMillis).plusDays(index.toLong())
            val key = date.key()
            SpendingTrendPoint(
                key = key,
                label = "${date.month}/${date.day}",
                amountMinor = grouped[key].orEmpty().sumOf { it.amountMinor },
            )
        }
    }

    private fun monthlyTrend(expenses: List<Expense>, now: Long): List<SpendingTrendPoint> {
        val currentMonth = CivilDate.fromEpochMillis(now)
        val grouped = expenses.groupBy { CivilDate.fromEpochMillis(it.createdAtMillis).monthKey() }
        return (5 downTo 0).map { offset ->
            val month = CivilDate(currentMonth.year, currentMonth.month, 1).plusMonths(-offset)
            val key = month.monthKey()
            SpendingTrendPoint(
                key = key,
                label = "${month.month}/${month.year.toString().takeLast(2)}",
                amountMinor = grouped[key].orEmpty().sumOf { it.amountMinor },
            )
        }
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
}

@OptIn(ExperimentalTime::class)
private fun currentTimeMillisProvider(): () -> Long = {
    Clock.System.now().toEpochMilliseconds()
}
