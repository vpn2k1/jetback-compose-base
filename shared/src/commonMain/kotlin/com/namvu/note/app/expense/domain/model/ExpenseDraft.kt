package com.namvu.note.app.expense.domain.model

data class ExpenseDraft(
    val title: String,
    val amountMinor: Long,
    val categoryId: String,
    val note: String = "",
    val createdAtMillis: Long? = null,
)
