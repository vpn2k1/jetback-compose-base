package com.namvu.note.app.expense.domain.budget

interface BudgetRepository {
    suspend fun getBudgets(): List<Budget>
    suspend fun upsertBudget(draft: BudgetDraft): Budget
    suspend fun deleteBudget(id: String)
}
