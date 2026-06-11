package com.namvu.note.app.expense.presentation.budget

import com.namvu.note.app.expense.domain.budget.BudgetProgress
import com.namvu.note.app.expense.domain.model.ExpenseValidationError

data class BudgetState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val budgetInput: String = "",
    val validationError: ExpenseValidationError = ExpenseValidationError(),
    val budgets: List<BudgetProgress> = emptyList(),
)
