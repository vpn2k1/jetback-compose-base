package com.namvu.myapplication.ui.budget

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.namvu.myapplication.expense.ExpenseJournalGraph
import com.namvu.myapplication.expense.presentation.budget.BudgetState
import com.namvu.myapplication.expense.presentation.budget.BudgetViewModel
import com.namvu.myapplication.ui.base.component.feedback.ErrorContent
import com.namvu.myapplication.ui.base.component.feedback.LoadingContent
import com.namvu.myapplication.ui.base.component.layout.AppScaffold
import com.namvu.myapplication.ui.budget.modules.BudgetScreenBodyModule

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    viewModel: BudgetViewModel = remember {
        BudgetViewModel(ExpenseJournalGraph.reportUseCases)
    },
) {
    BudgetScreenContent(
        state = viewModel.state,
        onBudgetInputChange = viewModel::onBudgetInputChange,
        onSaveClick = viewModel::saveMonthlyBudget,
        onRetryClick = viewModel::loadBudgets,
        modifier = modifier,
    )
}

@Composable
private fun BudgetScreenContent(
    state: BudgetState,
    onBudgetInputChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(title = "Budgets", modifier = modifier) { paddingValues ->
        when {
            state.isLoading -> LoadingContent(Modifier.fillMaxSize().padding(paddingValues))
            state.errorMessage != null -> ErrorContent(
                message = state.errorMessage,
                onRetryClick = onRetryClick,
                modifier = Modifier.fillMaxSize().padding(paddingValues),
            )
            else -> BudgetScreenBodyModule(
                state = state,
                onBudgetInputChange = onBudgetInputChange,
                onSaveClick = onSaveClick,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}
