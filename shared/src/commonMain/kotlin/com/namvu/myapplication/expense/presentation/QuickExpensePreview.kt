package com.namvu.myapplication.expense.presentation

data class QuickExpensePreview(
    val title: String,
    val amountMinor: Long,
    val categoryId: String,
    val categoryName: String,
    val suggestionReason: String,
    val createdAtMillis: Long? = null,
)

data class ExpenseTemplate(
    val title: String,
    val amountMinor: Long,
    val categoryId: String,
    val categoryName: String,
    val quickText: String,
)
