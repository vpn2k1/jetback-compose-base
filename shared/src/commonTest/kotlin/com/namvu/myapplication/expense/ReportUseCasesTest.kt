package com.namvu.myapplication.expense

import com.namvu.myapplication.expense.data.budget.InMemoryBudgetRepository
import com.namvu.myapplication.expense.data.local.InMemoryLocalExpenseDatabase
import com.namvu.myapplication.expense.data.repository.LocalExpenseRepository
import com.namvu.myapplication.expense.domain.insight.FinancialInsightUseCases
import com.namvu.myapplication.expense.domain.model.ExpenseDraft
import com.namvu.myapplication.expense.domain.report.CivilDate
import com.namvu.myapplication.expense.domain.report.ReportPeriod
import com.namvu.myapplication.expense.domain.report.ReportUseCases
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReportUseCasesTest {
    @Test
    fun calculatesPeriodSummaryBreakdownTrendAndTopExpenses() = runSuspending {
        var index = 0
        val dates = listOf(
            CivilDate(2026, 6, 11).toEpochMillis(),
            CivilDate(2026, 6, 10).toEpochMillis(),
            CivilDate(2026, 5, 30).toEpochMillis(),
        )
        val repository = LocalExpenseRepository(
            database = InMemoryLocalExpenseDatabase(),
            clock = { dates[index++] },
            idGenerator = { "expense-$index" },
        )
        val reports = ReportUseCases(
            expenseRepository = repository,
            budgetRepository = InMemoryBudgetRepository(),
            insightUseCases = FinancialInsightUseCases(
                repository = repository,
                clock = { CivilDate(2026, 6, 11).toEpochMillis() },
            ),
            clock = { CivilDate(2026, 6, 11).toEpochMillis() },
        )

        repository.addExpense(ExpenseDraft("Coffee", 4_500_000L, "drinks"))
        repository.addExpense(ExpenseDraft("Lunch", 12_000_000L, "food"))
        repository.addExpense(ExpenseDraft("Fuel", 30_000_000L, "transport"))

        val report = reports.loadReport(ReportPeriod.THIS_MONTH)

        assertEquals(16_500_000L, report.summary.totalMinor)
        assertEquals(2, report.summary.expenseCount)
        assertEquals("food", report.categoryBreakdown.first().categoryId)
        assertEquals("Lunch", report.topExpenses.first().title)
        assertTrue(report.dailyTrend.isNotEmpty())
        assertEquals(6, report.monthlyTrend.size)
        assertEquals(46_500_000L, report.dashboard.yearTotalMinor)
    }

    @Test
    fun calculatesMonthlyBudgetProgressFromLocalExpenses() = runSuspending {
        val repository = LocalExpenseRepository(
            database = InMemoryLocalExpenseDatabase(),
            clock = { CivilDate(2026, 6, 11).toEpochMillis() },
            idGenerator = { "expense-1" },
        )
        val reports = ReportUseCases(
            expenseRepository = repository,
            budgetRepository = InMemoryBudgetRepository(idGenerator = { "budget-1" }),
            insightUseCases = FinancialInsightUseCases(
                repository = repository,
                clock = { CivilDate(2026, 6, 11).toEpochMillis() },
            ),
            clock = { CivilDate(2026, 6, 11).toEpochMillis() },
        )

        repository.addExpense(ExpenseDraft("Lunch", 12_000_000L, "food"))
        reports.saveMonthlyBudget(50_000_000L)

        val progress = reports.loadBudgetProgress().first()

        assertEquals("2026-06", progress.budget.periodMonthKey)
        assertEquals(12_000_000L, progress.spentMinor)
        assertEquals(38_000_000L, progress.remainingMinor)
    }
}
