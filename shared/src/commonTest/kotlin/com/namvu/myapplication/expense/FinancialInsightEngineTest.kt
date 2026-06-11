package com.namvu.myapplication.expense

import com.namvu.myapplication.expense.domain.insight.FinancialInsightEngine
import com.namvu.myapplication.expense.domain.insight.InsightSeverity
import com.namvu.myapplication.expense.domain.insight.InsightType
import com.namvu.myapplication.expense.domain.model.DefaultCategories
import com.namvu.myapplication.expense.domain.model.Expense
import com.namvu.myapplication.expense.domain.report.CivilDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FinancialInsightEngineTest {
    @Test
    fun generatesLocalFinancialInsightsFromExpenseHistory() {
        val now = CivilDate(2026, 6, 15).toEpochMillis()
        val insights = FinancialInsightEngine().generate(
            expenses = listOf(
                expense("coffee-1", "Coffee", 4_500_000L, "drinks", CivilDate(2026, 6, 2)),
                expense("coffee-2", "Latte", 5_000_000L, "drinks", CivilDate(2026, 6, 3)),
                expense("coffee-3", "Coffee", 4_000_000L, "drinks", CivilDate(2026, 6, 4)),
                expense("lunch", "Lunch", 12_000_000L, "food", CivilDate(2026, 6, 5)),
                expense("weekend", "Weekend dinner", 18_000_000L, "food", CivilDate(2026, 6, 6)),
                expense("rent", "Rent", 120_000_000L, "bills", CivilDate(2026, 6, 10)),
                expense("may", "Groceries", 20_000_000L, "shopping", CivilDate(2026, 5, 12)),
            ),
            categories = DefaultCategories.items,
            nowMillis = now,
        )

        val types = insights.map { it.type }.toSet()

        assertTrue(InsightType.SpendingComparison in types)
        assertTrue(InsightType.RepeatedSpending in types)
        assertTrue(InsightType.Forecast in types)
        assertTrue(InsightType.Anomaly in types)
        assertTrue(insights.any { it.severity == InsightSeverity.Critical || it.severity == InsightSeverity.Warning })
    }

    @Test
    fun returnsStarterInsightWhenThereAreNoExpenses() {
        val insights = FinancialInsightEngine().generate(
            expenses = emptyList(),
            categories = DefaultCategories.items,
            nowMillis = CivilDate(2026, 6, 15).toEpochMillis(),
        )

        assertEquals(1, insights.size)
        assertEquals(InsightType.Forecast, insights.first().type)
    }

    private fun expense(
        id: String,
        title: String,
        amountMinor: Long,
        categoryId: String,
        date: CivilDate,
    ): Expense {
        val createdAt = date.toEpochMillis()
        return Expense(
            id = id,
            title = title,
            amountMinor = amountMinor,
            categoryId = categoryId,
            note = "",
            createdAtMillis = createdAt,
            updatedAtMillis = createdAt,
        )
    }
}
