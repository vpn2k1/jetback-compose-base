package com.namvu.note.app.expense.domain.repository

import com.namvu.note.app.expense.domain.model.Category
import com.namvu.note.app.expense.domain.model.Expense
import com.namvu.note.app.expense.domain.model.ExpenseDraft

interface ExpenseRepository {
    suspend fun getCategories(): List<Category>
    suspend fun getExpenses(): List<Expense>
    suspend fun getPendingSyncExpenses(): List<Expense>
    suspend fun addExpense(draft: ExpenseDraft): Expense
    suspend fun updateExpense(id: String, draft: ExpenseDraft): Expense
    suspend fun deleteExpense(id: String)
    suspend fun markExpenseSynced(id: String, syncedAtMillis: Long)
    suspend fun markExpenseSyncFailed(id: String, message: String)
    suspend fun resetFailedExpenseSync()
}
