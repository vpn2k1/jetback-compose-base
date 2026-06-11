package com.namvu.myapplication.expense.domain.model

data class ExpenseValidationError(
    val titleError: String? = null,
    val amountError: String? = null,
) {
    val hasError: Boolean
        get() = titleError != null || amountError != null
}
