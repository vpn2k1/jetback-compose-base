package com.namvu.myapplication.expense.presentation.budget

import com.namvu.myapplication.expense.domain.budget.BudgetProgress
import com.namvu.myapplication.expense.domain.model.ExpenseValidationError

data class BudgetState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val budgetInput: String = "",
    val validationError: ExpenseValidationError = ExpenseValidationError(),
    val budgets: List<BudgetProgress> = emptyList(),
)
