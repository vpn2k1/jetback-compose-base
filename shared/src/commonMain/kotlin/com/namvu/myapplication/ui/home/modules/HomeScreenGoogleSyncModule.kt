package com.namvu.myapplication.ui.home.modules

import com.namvu.myapplication.expense.domain.model.ExpenseSyncStatus
import com.namvu.myapplication.expense.presentation.ExpenseJournalState

internal data class HomeScreenGoogleSyncUiState(
    val description: String,
    val statusText: String,
    val status: ExpenseSyncStatus,
    val canRetry: Boolean,
    val canSync: Boolean,
)

internal fun homeScreenGoogleSyncUiState(
    state: ExpenseJournalState,
    pendingSyncCount: Int,
    failedSyncCount: Int,
): HomeScreenGoogleSyncUiState {
    val account = state.syncSession.account
    val spreadsheet = state.syncSession.spreadsheet
    return HomeScreenGoogleSyncUiState(
        description = when {
            account == null -> "Sign in to connect your user-owned spreadsheet."
            spreadsheet == null -> "Signed in as ${account.email}. Connect Expense Journal."
            else -> "Connected to ${spreadsheet.name}."
        },
        statusText = when {
            failedSyncCount > 0 -> "$failedSyncCount failed"
            pendingSyncCount > 0 -> "$pendingSyncCount pending"
            else -> "Synced"
        },
        status = when {
            failedSyncCount > 0 -> ExpenseSyncStatus.Failed
            pendingSyncCount > 0 -> ExpenseSyncStatus.Pending
            else -> ExpenseSyncStatus.Synced
        },
        canRetry = failedSyncCount > 0 && !state.isSyncing,
        canSync = pendingSyncCount > 0 || failedSyncCount > 0,
    )
}
