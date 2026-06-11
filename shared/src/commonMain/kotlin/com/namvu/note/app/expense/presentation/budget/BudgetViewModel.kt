package com.namvu.note.app.expense.presentation.budget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namvu.note.app.expense.domain.model.ExpenseValidationError
import com.namvu.note.app.expense.domain.model.QuickExpenseParser
import com.namvu.note.app.expense.domain.report.ReportUseCases
import kotlinx.coroutines.launch

class BudgetViewModel(
    private val useCases: ReportUseCases,
) : ViewModel() {
    var state by mutableStateOf(BudgetState())
        private set

    init {
        loadBudgets()
    }

    fun onBudgetInputChange(value: String) {
        state = state.copy(
            budgetInput = value,
            validationError = ExpenseValidationError(),
        )
    }

    fun loadBudgets() {
        state = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { useCases.loadBudgetProgress() }
                .onSuccess { budgets ->
                    state = state.copy(
                        isLoading = false,
                        budgets = budgets,
                    )
                }
                .onFailure { throwable ->
                    state = state.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Could not load budgets",
                    )
                }
        }
    }

    fun saveMonthlyBudget() {
        val amountMinor = QuickExpenseParser.parseAmountMinor(state.budgetInput)
        if (amountMinor == null) {
            state = state.copy(
                validationError = ExpenseValidationError(amountError = "Use an amount like 5m or 2500000"),
            )
            return
        }

        state = state.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { useCases.saveMonthlyBudget(amountMinor) }
                .onSuccess {
                    val budgets = useCases.loadBudgetProgress()
                    state = state.copy(
                        isSaving = false,
                        budgetInput = "",
                        budgets = budgets,
                        validationError = ExpenseValidationError(),
                    )
                }
                .onFailure { throwable ->
                    state = state.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Could not save budget",
                    )
                }
        }
    }
}
