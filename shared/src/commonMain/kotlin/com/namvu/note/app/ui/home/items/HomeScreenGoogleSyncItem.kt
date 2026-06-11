package com.namvu.note.app.ui.home.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.namvu.note.app.expense.presentation.ExpenseJournalState
import com.namvu.note.app.ui.base.component.button.AppButton
import com.namvu.note.app.ui.base.component.button.AppOutlinedButton
import com.namvu.note.app.ui.base.component.surface.AppCard
import com.namvu.note.app.ui.home.modules.homeScreenGoogleSyncUiState

@Composable
internal fun HomeScreenGoogleSyncItem(
    state: ExpenseJournalState,
    pendingSyncCount: Int,
    failedSyncCount: Int,
    onGoogleSignInClick: () -> Unit,
    onGoogleSignOutClick: () -> Unit,
    onConnectSpreadsheetClick: () -> Unit,
    onSyncClick: () -> Unit,
    onRetrySyncClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val account = state.syncSession.account
    val spreadsheet = state.syncSession.spreadsheet
    val syncUiState = homeScreenGoogleSyncUiState(
        state = state,
        pendingSyncCount = pendingSyncCount,
        failedSyncCount = failedSyncCount,
    )
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Google Sheets sync", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = syncUiState.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            HomeScreenSyncStatusItem(
                text = syncUiState.statusText,
                status = syncUiState.status,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            when {
                account == null -> AppButton(
                    text = "Sign in",
                    onClick = onGoogleSignInClick,
                    isLoading = state.isSyncing,
                    modifier = Modifier.weight(1f),
                )
                spreadsheet == null -> {
                    AppOutlinedButton(
                        text = "Sign out",
                        onClick = onGoogleSignOutClick,
                        enabled = !state.isSyncing,
                        modifier = Modifier.weight(1f),
                    )
                    AppButton(
                        text = "Connect",
                        onClick = onConnectSpreadsheetClick,
                        isLoading = state.isSyncing,
                        modifier = Modifier.weight(1f),
                    )
                }
                else -> {
                    AppOutlinedButton(
                        text = "Retry",
                        onClick = onRetrySyncClick,
                        enabled = syncUiState.canRetry,
                        modifier = Modifier.weight(1f),
                    )
                    AppButton(
                        text = "Sync",
                        onClick = onSyncClick,
                        enabled = syncUiState.canSync,
                        isLoading = state.isSyncing,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        if (state.syncMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.syncMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        if (state.errorMessage != null && state.hasExpenses) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
