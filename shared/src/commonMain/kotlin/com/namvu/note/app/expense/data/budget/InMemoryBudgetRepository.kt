package com.namvu.note.app.expense.data.budget

import com.namvu.note.app.expense.domain.budget.Budget
import com.namvu.note.app.expense.domain.budget.BudgetDraft
import com.namvu.note.app.expense.domain.budget.BudgetRepository
import kotlin.random.Random

class InMemoryBudgetRepository(
    seedBudgets: List<Budget> = emptyList(),
    private val idGenerator: () -> String = { "budget-${Random.nextLong().toString().replace("-", "")}" },
) : BudgetRepository {
    private val budgets = seedBudgets.associateBy { it.id }.toMutableMap()

    override suspend fun getBudgets(): List<Budget> {
        return budgets.values.sortedWith(compareBy<Budget> { it.periodMonthKey }.thenBy { it.name })
    }

    override suspend fun upsertBudget(draft: BudgetDraft): Budget {
        val existing = budgets.values.firstOrNull {
            it.periodMonthKey == draft.periodMonthKey && it.categoryId == draft.categoryId
        }
        val budget = Budget(
            id = existing?.id ?: idGenerator(),
            name = draft.name.trim(),
            periodMonthKey = draft.periodMonthKey,
            amountMinor = draft.amountMinor,
            categoryId = draft.categoryId,
        )
        budgets[budget.id] = budget
        return budget
    }

    override suspend fun deleteBudget(id: String) {
        budgets.remove(id)
    }
}
