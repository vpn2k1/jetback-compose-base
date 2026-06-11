package com.namvu.myapplication.expense.data.local

interface LocalExpenseDatabase {
    suspend fun getExpenses(): List<ExpenseRecord>
    suspend fun getExpense(id: String): ExpenseRecord?
    suspend fun upsertExpense(expense: ExpenseRecord)
    suspend fun deleteExpense(id: String)
}

class InMemoryLocalExpenseDatabase(
    seedExpenses: List<ExpenseRecord> = emptyList(),
) : LocalExpenseDatabase {
    private val expenses = seedExpenses.associateBy { it.id }.toMutableMap()

    override suspend fun getExpenses(): List<ExpenseRecord> {
        return expenses.values.sortedByDescending { it.createdAtMillis }
    }

    override suspend fun getExpense(id: String): ExpenseRecord? {
        return expenses[id]
    }

    override suspend fun upsertExpense(expense: ExpenseRecord) {
        expenses[expense.id] = expense
    }

    override suspend fun deleteExpense(id: String) {
        expenses.remove(id)
    }
}
