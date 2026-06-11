package com.namvu.myapplication.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.ExpenseJournalGraph
import com.namvu.myapplication.expense.domain.budget.BudgetProgress
import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.expense.presentation.budget.BudgetState
import com.namvu.myapplication.expense.presentation.budget.BudgetViewModel
import com.namvu.myapplication.ui.base.component.button.AppButton
import com.namvu.myapplication.ui.base.component.feedback.EmptyContent
import com.namvu.myapplication.ui.base.component.feedback.ErrorContent
import com.namvu.myapplication.ui.base.component.feedback.LoadingContent
import com.namvu.myapplication.ui.base.component.input.AppTextField
import com.namvu.myapplication.ui.base.component.layout.AppScaffold
import com.namvu.myapplication.ui.base.component.surface.AppCard

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    viewModel: BudgetViewModel = remember {
        BudgetViewModel(ExpenseJournalGraph.reportUseCases)
    },
) {
    BudgetContent(
        state = viewModel.state,
        onBudgetInputChange = viewModel::onBudgetInputChange,
        onSaveClick = viewModel::saveMonthlyBudget,
        onRetryClick = viewModel::loadBudgets,
        modifier = modifier,
    )
}

@Composable
private fun BudgetContent(
    state: BudgetState,
    onBudgetInputChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        title = "Budgets",
        modifier = modifier,
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
            state.errorMessage != null -> ErrorContent(
                message = state.errorMessage,
                onRetryClick = onRetryClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
            else -> BudgetBody(
                state = state,
                onBudgetInputChange = onBudgetInputChange,
                onSaveClick = onSaveClick,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun BudgetBody(
    state: BudgetState,
    onBudgetInputChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            BudgetEntryCard(
                state = state,
                onBudgetInputChange = onBudgetInputChange,
                onSaveClick = onSaveClick,
            )
        }
        item {
            Text(
                text = "Budget progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (state.budgets.isEmpty()) {
            item {
                EmptyContent(
                    title = "No budget yet",
                    message = "Set a monthly limit to start tracking spending against a budget.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                )
            }
        } else {
            items(
                items = state.budgets,
                key = { it.budget.id },
            ) { budget ->
                BudgetProgressCard(progress = budget)
            }
        }
    }
}

@Composable
private fun BudgetEntryCard(
    state: BudgetState,
    onBudgetInputChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Text(
            text = "Monthly budget",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Reports work offline from local expenses. This budget foundation tracks the current month.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(
            value = state.budgetInput,
            onValueChange = onBudgetInputChange,
            label = "Limit",
            placeholder = "5m",
            errorText = state.validationError.amountError,
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppButton(
            text = "Save budget",
            onClick = onSaveClick,
            enabled = state.budgetInput.isNotBlank(),
            isLoading = state.isSaving,
            fullWidth = true,
        )
    }
}

@Composable
private fun BudgetProgressCard(
    progress: BudgetProgress,
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
                    text = progress.budget.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = progress.budget.periodMonthKey,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = if (progress.isOverBudget) "Over" else "On track",
                style = MaterialTheme.typography.labelLarge,
                color = if (progress.isOverBudget) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { progress.progress },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${ExpenseFormatter.formatAmount(progress.spentMinor)} spent of ${ExpenseFormatter.formatAmount(progress.budget.amountMinor)}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "${ExpenseFormatter.formatAmount(progress.remainingMinor)} remaining",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
