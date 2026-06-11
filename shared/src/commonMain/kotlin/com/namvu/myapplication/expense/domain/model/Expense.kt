package com.namvu.myapplication.expense.domain.model

data class Expense(
    val id: String,
    val title: String,
    val amountMinor: Long,
    val categoryId: String,
    val note: String,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val syncStatus: ExpenseSyncStatus = ExpenseSyncStatus.Pending,
    val syncedAtMillis: Long? = null,
    val syncError: String? = null,
    val retryCount: Int = 0,
)
