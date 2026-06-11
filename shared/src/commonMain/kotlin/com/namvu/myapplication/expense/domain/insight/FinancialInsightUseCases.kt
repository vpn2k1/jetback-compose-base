package com.namvu.myapplication.expense.domain.insight

import com.namvu.myapplication.expense.domain.repository.ExpenseRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FinancialInsightUseCases(
    private val repository: ExpenseRepository,
    private val engine: FinancialInsightEngine = FinancialInsightEngine(),
    private val clock: () -> Long = currentTimeMillisProvider(),
) {
    suspend fun loadInsights(): List<FinancialInsight> {
        return engine.generate(
            expenses = repository.getExpenses(),
            categories = repository.getCategories(),
            nowMillis = clock(),
        )
    }
}

@OptIn(ExperimentalTime::class)
private fun currentTimeMillisProvider(): () -> Long = {
    Clock.System.now().toEpochMilliseconds()
}
