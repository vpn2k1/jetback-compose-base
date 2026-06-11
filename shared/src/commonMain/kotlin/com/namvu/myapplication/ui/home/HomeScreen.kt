package com.namvu.myapplication.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.ExpenseJournalGraph
import com.namvu.myapplication.expense.domain.model.Category
import com.namvu.myapplication.expense.domain.model.Expense
import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.expense.domain.model.ExpenseSyncStatus
import com.namvu.myapplication.expense.presentation.ExpenseTemplate
import com.namvu.myapplication.expense.presentation.ExpenseJournalState
import com.namvu.myapplication.expense.presentation.ExpenseJournalViewModel
import com.namvu.myapplication.expense.presentation.QuickExpensePreview
import com.namvu.myapplication.ui.base.component.button.AppButton
import com.namvu.myapplication.ui.base.component.button.AppOutlinedButton
import com.namvu.myapplication.ui.base.component.button.AppTextButton
import com.namvu.myapplication.ui.base.component.dialog.AppConfirmDialog
import com.namvu.myapplication.ui.base.component.dialog.AppDestructiveConfirmDialog
import com.namvu.myapplication.ui.base.component.feedback.EmptyContent
import com.namvu.myapplication.ui.base.component.feedback.ErrorContent
import com.namvu.myapplication.ui.base.component.feedback.LoadingContent
import com.namvu.myapplication.ui.base.component.input.AppTextField
import com.namvu.myapplication.ui.base.component.layout.AppScaffold
import com.namvu.myapplication.ui.base.component.surface.AppCard
import com.namvu.myapplication.ui.insight.InsightCard
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
    ExpenseJournalScreen(
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
private fun ExpenseJournalScreen(
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
            state.errorMessage != null && !state.hasExpenses -> ErrorContent(
                message = state.errorMessage,
                onRetryClick = onRetryClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
            else -> JournalContent(
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

@Composable
private fun JournalContent(
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
    onGoogleSignInClick: () -> Unit,
    onGoogleSignOutClick: () -> Unit,
    onConnectSpreadsheetClick: () -> Unit,
    onSyncClick: () -> Unit,
    onRetrySyncClick: () -> Unit,
    onTemplateClick: (ExpenseTemplate) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            GreetingHeader()
        }
        item {
            SummaryCard(totalMinor = state.totalMinor, expenseCount = state.expenses.size)
        }
        item {
            TodaySummaryCards(
                transactionCount = state.expenses.size,
                totalMinor = state.totalMinor,
                averageMinor = if (state.expenses.isEmpty()) 0L else state.totalMinor / state.expenses.size,
            )
        }
        if (state.insights.isNotEmpty()) {
            item {
                Text(
                    text = "Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            items(
                items = state.insights.take(2),
                key = { it.id },
            ) { insight ->
                InsightCard(insight = insight)
            }
        }
        item {
            GoogleSyncCard(
                state = state,
                onGoogleSignInClick = onGoogleSignInClick,
                onGoogleSignOutClick = onGoogleSignOutClick,
                onConnectSpreadsheetClick = onConnectSpreadsheetClick,
                onSyncClick = onSyncClick,
                onRetrySyncClick = onRetrySyncClick,
            )
        }
        item {
            QuickEntryCard(
                value = state.quickEntry,
                amountError = state.validationError.amountError,
                titleError = state.validationError.titleError,
                preview = state.quickPreview,
                templates = state.recentTemplates,
                isSaving = state.isSaving,
                onValueChange = onQuickEntryChange,
                onAddClick = onQuickAddClick,
                onTemplateClick = onTemplateClick,
            )
        }
        item {
            ExpenseFormCard(
                state = state,
                onCategorySelected = onCategorySelected,
                onTitleChange = onTitleChange,
                onAmountChange = onAmountChange,
                onNoteChange = onNoteChange,
                onSaveClick = onSaveClick,
                onStartAddClick = onStartAddClick,
            )
        }
        item {
            Text(
                text = "Recent expenses",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (state.expenses.isEmpty()) {
            item {
                EmptyContent(
                    title = "No expenses yet",
                    message = "Try Coffee 45k, Lunch 120k, or Electricity 1.2m.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                )
            }
        } else {
            items(
                items = state.expenses,
                key = { it.id },
            ) { expense ->
                ExpenseRow(
                    expense = expense,
                    category = state.categories.firstOrNull { it.id == expense.categoryId },
                    onEditClick = { onEditClick(expense.id) },
                    onDeleteClick = { onDeleteClick(expense.id) },
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    totalMinor: Long,
    expenseCount: Int,
    modifier: Modifier = Modifier,
) {
    val monthlyBudgetMinor = 1_200_000_000L
    val spentMinor = totalMinor.coerceAtMost(monthlyBudgetMinor)
    val progress = if (monthlyBudgetMinor == 0L) 0f else spentMinor.toFloat() / monthlyBudgetMinor
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Monthly Budget",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${ExpenseFormatter.formatAmount(spentMinor)} / 12,000,000 VND",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${ExpenseFormatter.formatAmount(monthlyBudgetMinor - spentMinor)} remaining | $expenseCount entries",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        androidx.compose.material3.LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = when {
                progress < 0.7f -> MaterialTheme.colorScheme.secondary
                progress < 0.9f -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.error
            },
        )
    }
}

@Composable
private fun GreetingHeader(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Good Morning, Nam",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Track money. Own your data.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HeaderCircle(text = "!")
            HeaderCircle(text = "N")
        }
    }
}

@Composable
private fun HeaderCircle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun TodaySummaryCards(
    transactionCount: Int,
    totalMinor: Long,
    averageMinor: Long,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MiniStatCard("Transactions", transactionCount.toString(), Modifier.weight(1f))
        MiniStatCard("Total spent", ExpenseFormatter.formatAmount(totalMinor), Modifier.weight(1f))
        MiniStatCard("Average", ExpenseFormatter.formatAmount(averageMinor), Modifier.weight(1f))
    }
}

@Composable
private fun MiniStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun GoogleSyncCard(
    state: ExpenseJournalState,
    onGoogleSignInClick: () -> Unit,
    onGoogleSignOutClick: () -> Unit,
    onConnectSpreadsheetClick: () -> Unit,
    onSyncClick: () -> Unit,
    onRetrySyncClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val account = state.syncSession.account
    val spreadsheet = state.syncSession.spreadsheet

    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Google Sheets sync",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = when {
                        account == null -> "Sign in to connect your user-owned spreadsheet."
                        spreadsheet == null -> "Signed in as ${account.email}. Connect Expense Journal."
                        else -> "Connected to ${spreadsheet.name}."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            SyncStatusPill(
                text = when {
                    state.failedSyncCount > 0 -> "${state.failedSyncCount} failed"
                    state.pendingSyncCount > 0 -> "${state.pendingSyncCount} pending"
                    else -> "Synced"
                },
                status = when {
                    state.failedSyncCount > 0 -> ExpenseSyncStatus.Failed
                    state.pendingSyncCount > 0 -> ExpenseSyncStatus.Pending
                    else -> ExpenseSyncStatus.Synced
                },
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
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
                        enabled = state.failedSyncCount > 0 && !state.isSyncing,
                        modifier = Modifier.weight(1f),
                    )
                    AppButton(
                        text = "Sync",
                        onClick = onSyncClick,
                        enabled = state.pendingSyncCount > 0 || state.failedSyncCount > 0,
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

@Composable
private fun QuickEntryCard(
    value: String,
    titleError: String?,
    amountError: String?,
    preview: QuickExpensePreview?,
    templates: List<ExpenseTemplate>,
    isSaving: Boolean,
    onValueChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onTemplateClick: (ExpenseTemplate) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Quick add",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Type naturally, preview, save.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "3 sec",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        AppTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = "Coffee 45k today #drinks",
            errorText = titleError ?: amountError,
        )
        if (preview != null) {
            Spacer(modifier = Modifier.height(10.dp))
            QuickPreviewCard(preview = preview)
        }
        if (templates.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Recent templates",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(
                    items = templates,
                    key = { "${it.title}-${it.amountMinor}-${it.categoryId}" },
                ) { template ->
                    AssistChip(
                        onClick = { onTemplateClick(template) },
                        label = {
                            Text(
                                text = "${template.title} ${ExpenseFormatter.formatAmount(template.amountMinor)}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        AppButton(
            text = if (preview == null) "Add expense" else "Save preview",
            onClick = onAddClick,
            enabled = value.isNotBlank() && preview != null,
            isLoading = isSaving,
            fullWidth = true,
        )
    }
}

@Composable
private fun QuickPreviewCard(
    preview: QuickExpensePreview,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = preview.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${preview.categoryName} | ${preview.suggestionReason}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = ExpenseFormatter.formatAmount(preview.amountMinor),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun ExpenseFormCard(
    state: ExpenseJournalState,
    onCategorySelected: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onStartAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.formTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            AppTextButton(
                text = "Clear",
                onClick = onStartAddClick,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        CategoryPicker(
            categories = state.categories,
            selectedCategoryId = state.selectedCategoryId,
            onCategorySelected = onCategorySelected,
        )
        Spacer(modifier = Modifier.height(10.dp))
        AppTextField(
            value = state.titleInput,
            onValueChange = onTitleChange,
            label = "Description",
            placeholder = "Lunch",
            errorText = state.validationError.titleError,
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppTextField(
            value = state.amountInput,
            onValueChange = onAmountChange,
            label = "Amount",
            placeholder = "120k",
            errorText = state.validationError.amountError,
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppTextField(
            value = state.noteInput,
            onValueChange = onNoteChange,
            label = "Note",
            singleLine = false,
            minLines = 2,
            maxLines = 4,
        )
        Spacer(modifier = Modifier.height(12.dp))
        AppOutlinedButton(
            text = if (state.editingExpenseId == null) "Save expense" else "Update expense",
            onClick = onSaveClick,
            enabled = !state.isSaving,
            isLoading = state.isSaving,
            fullWidth = true,
        )
    }
}

@Composable
private fun CategoryPicker(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = categories,
            key = { it.id },
        ) { category ->
            FilterChip(
                selected = selectedCategoryId == category.id,
                onClick = { onCategorySelected(category.id) },
                label = { Text(text = category.name) },
                leadingIcon = {
                    CategoryDot(
                        color = category.color,
                        label = category.icon,
                    )
                },
            )
        }
    }
}

@Composable
private fun ExpenseRow(
    expense: Expense,
    category: Category?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CategoryDot(
                color = category?.color ?: MaterialTheme.colorScheme.surfaceVariant,
                label = category?.icon ?: "?",
                modifier = Modifier.size(40.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = category?.name ?: "Uncategorized",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (expense.note.isNotBlank()) {
                    Text(
                        text = expense.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                SyncStatusPill(
                    text = expense.syncStatus.name,
                    status = expense.syncStatus,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = ExpenseFormatter.formatAmount(expense.amountMinor),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AssistChip(
                        onClick = onEditClick,
                        label = { Text(text = "Edit") },
                    )
                    AssistChip(
                        onClick = onDeleteClick,
                        label = { Text(text = "Delete") },
                    )
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun SyncStatusPill(
    text: String,
    status: ExpenseSyncStatus,
    modifier: Modifier = Modifier,
) {
    val color = when (status) {
        ExpenseSyncStatus.Pending -> MaterialTheme.colorScheme.tertiary
        ExpenseSyncStatus.Synced -> MaterialTheme.colorScheme.secondary
        ExpenseSyncStatus.Failed -> MaterialTheme.colorScheme.error
    }
    Text(
        text = text,
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun CategoryDot(
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }
}
