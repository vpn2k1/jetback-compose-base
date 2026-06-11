package com.namvu.note.app.expense.data.local

data class ExpenseRecord(
    val id: String,
    val title: String,
    val amountMinor: Long,
    val categoryId: String,
    val note: String,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val syncStatus: String,
    val syncedAtMillis: Long?,
    val syncError: String?,
    val retryCount: Int,
)
