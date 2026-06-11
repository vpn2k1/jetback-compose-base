package com.namvu.note.app.ui.expenses

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.namvu.note.app.expense.ExpenseJournalGraph
import com.namvu.note.app.expense.presentation.ExpenseJournalViewModel
import com.namvu.note.app.ui.base.component.feedback.ErrorContent
import com.namvu.note.app.ui.base.component.feedback.LoadingContent
import com.namvu.note.app.ui.base.component.layout.AppScaffold
import com.namvu.note.app.ui.expenses.modules.ExpensesScreenHistoryModule

@Composable
fun ExpensesScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpenseJournalViewModel = remember {
        ExpenseJournalViewModel(ExpenseJournalGraph.useCases)
    },
) {
    val state = viewModel.state
    var query by remember { mutableStateOf("") }
    AppScaffold(title = "Expenses", modifier = modifier) { paddingValues ->
        when {
            state.isLoading -> LoadingContent(Modifier.fillMaxSize().padding(paddingValues))
            state.errorMessage != null && !state.hasExpenses -> ErrorContent(
                message = state.errorMessage,
                onRetryClick = viewModel::loadJournal,
                modifier = Modifier.fillMaxSize().padding(paddingValues),
            )
            else -> ExpensesScreenHistoryModule(
                expenses = state.expenses,
                categories = state.categories,
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}
