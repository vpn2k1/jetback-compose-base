package com.namvu.note.app.ui.home.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.namvu.note.app.expense.domain.model.ExpenseSyncStatus
import com.namvu.note.app.expense.presentation.ExpenseJournalState
import com.namvu.note.app.expense.presentation.ExpenseTemplate
import com.namvu.note.app.ui.base.component.feedback.EmptyContent
import com.namvu.note.app.ui.insight.InsightCard

@Composable
internal fun HomeScreenBodyItem(
    state: ExpenseJournalState,
    onQuickEntryChange: (String) -> Unit,
    onQuickAddClick: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onStartAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onTemplateClick: (ExpenseTemplate) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onGoogleSignOutClick: () -> Unit,
    onConnectSpreadsheetClick: () -> Unit,
    onSyncClick: () -> Unit,
    onRetrySyncClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categoriesById = remember(state.categories) {
        state.categories.associateBy { it.id }
    }
    val totalMinor = remember(state.expenses) {
        state.expenses.sumOf { it.amountMinor }
    }
    val pendingSyncCount = remember(state.expenses) {
        state.expenses.count { it.syncStatus == ExpenseSyncStatus.Pending }
    }
    val failedSyncCount = remember(state.expenses) {
        state.expenses.count { it.syncStatus == ExpenseSyncStatus.Failed }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HomeScreenDashboardItem(
                state = state,
                totalMinor = totalMinor,
            )
        }
        if (state.insights.isNotEmpty()) {
            item { Text("Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            items(state.insights.take(2), key = { it.id }) { InsightCard(insight = it) }
        }
        item {
            HomeScreenGoogleSyncItem(
                state = state,
                pendingSyncCount = pendingSyncCount,
                failedSyncCount = failedSyncCount,
                onGoogleSignInClick = onGoogleSignInClick,
                onGoogleSignOutClick = onGoogleSignOutClick,
                onConnectSpreadsheetClick = onConnectSpreadsheetClick,
                onSyncClick = onSyncClick,
                onRetrySyncClick = onRetrySyncClick,
            )
        }
        item {
            HomeScreenQuickEntryItem(
                state = state,
                onValueChange = onQuickEntryChange,
                onAddClick = onQuickAddClick,
                onTemplateClick = onTemplateClick,
            )
        }
        item {
            HomeScreenExpenseFormItem(
                state = state,
                onCategorySelected = onCategorySelected,
                onTitleChange = onTitleChange,
                onAmountChange = onAmountChange,
                onNoteChange = onNoteChange,
                onSaveClick = onSaveClick,
                onStartAddClick = onStartAddClick,
            )
        }
        item { Text("Recent expenses", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
        if (state.expenses.isEmpty()) {
            item {
                EmptyContent(
                    title = "No expenses yet",
                    message = "Try Coffee 45k, Lunch 120k, or Electricity 1.2m.",
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                )
            }
        } else {
            items(state.expenses, key = { it.id }) { expense ->
                HomeScreenExpenseItem(
                    expense = expense,
                    category = categoriesById[expense.categoryId],
                    onEditClick = { onEditClick(expense.id) },
                    onDeleteClick = { onDeleteClick(expense.id) },
                )
            }
        }
    }
}
