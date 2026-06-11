package com.namvu.note.app.expense.domain.usecase

import com.namvu.note.app.expense.domain.insight.FinancialInsightUseCases
import com.namvu.note.app.expense.domain.model.ExpenseDraft
import com.namvu.note.app.expense.domain.model.ExpenseValidationError
import com.namvu.note.app.expense.domain.repository.ExpenseRepository

class ExpenseUseCases(
    private val repository: ExpenseRepository,
    private val googleSyncUseCases: GoogleSyncUseCases,
    private val insightUseCases: FinancialInsightUseCases,
) {
    suspend fun loadJournal(): ExpenseJournal {
        return ExpenseJournal(
            categories = repository.getCategories(),
            expenses = repository.getExpenses(),
            insights = insightUseCases.loadInsights(),
            syncSession = googleSyncUseCases.loadSession(),
        )
    }

    suspend fun addExpense(draft: ExpenseDraft) {
        validateDraft(draft).throwIfInvalid()
        repository.addExpense(draft)
    }

    suspend fun updateExpense(id: String, draft: ExpenseDraft) {
        validateDraft(draft).throwIfInvalid()
        repository.updateExpense(id, draft)
    }

    suspend fun deleteExpense(id: String) {
        repository.deleteExpense(id)
    }

    suspend fun signInToGoogle(): ExpenseJournal {
        googleSyncUseCases.signIn()
        return loadJournal()
    }

    suspend fun signOutFromGoogle(): ExpenseJournal {
        googleSyncUseCases.signOut()
        return loadJournal()
    }

    suspend fun connectSpreadsheet(): ExpenseJournal {
        googleSyncUseCases.connectExpenseJournalSpreadsheet()
        return loadJournal()
    }

    suspend fun syncPendingExpenses(): ExpenseJournal {
        googleSyncUseCases.syncPendingExpenses()
        return loadJournal()
    }

    suspend fun retryFailedSync(): ExpenseJournal {
        googleSyncUseCases.retryFailedSync()
        return loadJournal()
    }

    fun validateDraft(draft: ExpenseDraft): ExpenseValidationError {
        return ExpenseValidationError(
            titleError = if (draft.title.isBlank()) "Add a description" else null,
            amountError = if (draft.amountMinor <= 0L) "Add an amount" else null,
        )
    }
}

private fun ExpenseValidationError.throwIfInvalid() {
    require(!hasError) { "Expense draft is invalid" }
}
