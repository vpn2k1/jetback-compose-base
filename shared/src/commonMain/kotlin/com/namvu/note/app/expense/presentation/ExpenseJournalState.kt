package com.namvu.note.app.expense.presentation

import com.namvu.note.app.expense.domain.insight.FinancialInsight
import com.namvu.note.app.expense.domain.model.Category
import com.namvu.note.app.expense.domain.model.Expense
import com.namvu.note.app.expense.domain.model.ExpenseSyncStatus
import com.namvu.note.app.expense.domain.model.ExpenseValidationError
import com.namvu.note.app.expense.domain.sync.GoogleSyncSession

data class ExpenseJournalState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val syncMessage: String? = null,
    val categories: List<Category> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val insights: List<FinancialInsight> = emptyList(),
    val syncSession: GoogleSyncSession = GoogleSyncSession(
        account = null,
        spreadsheet = null,
    ),
    val quickEntry: String = "",
    val selectedCategoryId: String = "other",
    val editingExpenseId: String? = null,
    val titleInput: String = "",
    val amountInput: String = "",
    val noteInput: String = "",
    val validationError: ExpenseValidationError = ExpenseValidationError(),
    val deleteCandidateId: String? = null,
    val quickPreview: QuickExpensePreview? = null,
    val recentTemplates: List<ExpenseTemplate> = emptyList(),
    val duplicateCandidate: QuickExpensePreview? = null,
) {
    val hasExpenses: Boolean
        get() = expenses.isNotEmpty()

    val totalMinor: Long
        get() = expenses.sumOf { it.amountMinor }

    val pendingSyncCount: Int
        get() = expenses.count { it.syncStatus == ExpenseSyncStatus.Pending }

    val failedSyncCount: Int
        get() = expenses.count { it.syncStatus == ExpenseSyncStatus.Failed }

    val syncedCount: Int
        get() = expenses.count { it.syncStatus == ExpenseSyncStatus.Synced }

    val formTitle: String
        get() = if (editingExpenseId == null) "New expense" else "Edit expense"
}
