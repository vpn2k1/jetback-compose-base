package com.namvu.myapplication.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.namvu.myapplication.expense.ExpenseJournalGraph
import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.expense.presentation.ExpenseJournalState
import com.namvu.myapplication.expense.presentation.ExpenseJournalViewModel
import com.namvu.myapplication.expense.presentation.ExpenseTemplate
import com.namvu.myapplication.ui.base.component.dialog.AppConfirmDialog
import com.namvu.myapplication.ui.base.component.dialog.AppDestructiveConfirmDialog
import com.namvu.myapplication.ui.base.component.feedback.ErrorContent
import com.namvu.myapplication.ui.base.component.feedback.LoadingContent
import com.namvu.myapplication.ui.base.component.layout.AppScaffold
import com.namvu.myapplication.ui.home.items.HomeScreenBodyItem
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.home_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpenseJournalViewModel = remember {
        ExpenseJournalViewModel(ExpenseJournalGraph.useCases)
    },
) {
    HomeScreenContent(
        state = viewModel.state,
        onQuickEntryChange = viewModel::onQuickEntryChange,
        onQuickAddClick = viewModel::addQuickExpense,
        onRetryClick = viewModel::loadJournal,
        onCategorySelected = viewModel::onCategorySelected,
        onTitleChange = viewModel::onTitleChange,
        onAmountChange = viewModel::onAmountChange,
        onNoteChange = viewModel::onNoteChange,
        onSaveClick = viewModel::saveCurrentForm,
        onStartAddClick = viewModel::startAddExpense,
        onEditClick = viewModel::startEditExpense,
        onDeleteClick = viewModel::requestDeleteExpense,
        onDismissDelete = viewModel::dismissDeleteDialog,
        onConfirmDelete = viewModel::confirmDeleteExpense,
        onGoogleSignInClick = viewModel::signInToGoogle,
        onGoogleSignOutClick = viewModel::signOutFromGoogle,
        onConnectSpreadsheetClick = viewModel::connectSpreadsheet,
        onSyncClick = viewModel::syncPendingExpenses,
        onRetrySyncClick = viewModel::retryFailedSync,
        onTemplateClick = viewModel::useTemplate,
        onConfirmDuplicate = viewModel::confirmDuplicateQuickAdd,
        onDismissDuplicate = viewModel::dismissDuplicateQuickAdd,
        modifier = modifier,
    )
}

@Composable
private fun HomeScreenContent(
    state: ExpenseJournalState,
    onQuickEntryChange: (String) -> Unit,
    onQuickAddClick: () -> Unit,
    onRetryClick: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onStartAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onGoogleSignOutClick: () -> Unit,
    onConnectSpreadsheetClick: () -> Unit,
    onSyncClick: () -> Unit,
    onRetrySyncClick: () -> Unit,
    onTemplateClick: (ExpenseTemplate) -> Unit,
    onConfirmDuplicate: () -> Unit,
    onDismissDuplicate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        title = stringResource(Res.string.home_title),
        modifier = modifier,
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingContent(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
            )
            state.errorMessage != null && !state.hasExpenses -> ErrorContent(
                message = state.errorMessage,
                onRetryClick = onRetryClick,
                modifier = Modifier.fillMaxSize().padding(paddingValues),
            )
            else -> HomeScreenBodyItem(
                state = state,
                onQuickEntryChange = onQuickEntryChange,
                onQuickAddClick = onQuickAddClick,
                onCategorySelected = onCategorySelected,
                onTitleChange = onTitleChange,
                onAmountChange = onAmountChange,
                onNoteChange = onNoteChange,
                onSaveClick = onSaveClick,
                onStartAddClick = onStartAddClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onGoogleSignInClick = onGoogleSignInClick,
                onGoogleSignOutClick = onGoogleSignOutClick,
                onConnectSpreadsheetClick = onConnectSpreadsheetClick,
                onSyncClick = onSyncClick,
                onRetrySyncClick = onRetrySyncClick,
                onTemplateClick = onTemplateClick,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }

    if (state.deleteCandidateId != null) {
        AppDestructiveConfirmDialog(
            title = "Delete expense?",
            message = "This removes the entry from your local journal.",
            onConfirmClick = onConfirmDelete,
            onDismissRequest = onDismissDelete,
        )
    }
    if (state.duplicateCandidate != null) {
        AppConfirmDialog(
            title = "Add repeat expense?",
            message = "${state.duplicateCandidate.title} ${ExpenseFormatter.formatAmount(state.duplicateCandidate.amountMinor)} was added recently.",
            confirmText = "Add again",
            onConfirmClick = onConfirmDuplicate,
            onDismissRequest = onDismissDuplicate,
        )
    }
}
