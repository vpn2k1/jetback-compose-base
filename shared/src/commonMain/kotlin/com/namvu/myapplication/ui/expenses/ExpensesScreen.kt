package com.namvu.myapplication.ui.expenses

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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.ExpenseJournalGraph
import com.namvu.myapplication.expense.domain.model.Category
import com.namvu.myapplication.expense.domain.model.Expense
import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.expense.domain.report.CivilDate
import com.namvu.myapplication.expense.presentation.ExpenseJournalViewModel
import com.namvu.myapplication.ui.base.component.feedback.EmptyContent
import com.namvu.myapplication.ui.base.component.feedback.ErrorContent
import com.namvu.myapplication.ui.base.component.feedback.LoadingContent
import com.namvu.myapplication.ui.base.component.input.AppTextField
import com.namvu.myapplication.ui.base.component.layout.AppScaffold
import com.namvu.myapplication.ui.base.component.surface.AppCard

@Composable
fun ExpensesScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpenseJournalViewModel = remember {
        ExpenseJournalViewModel(ExpenseJournalGraph.useCases)
    },
) {
    val state = viewModel.state
    var query by remember { mutableStateOf("") }
    AppScaffold(
        title = "Expenses",
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
                onRetryClick = viewModel::loadJournal,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
            else -> ExpenseHistoryContent(
                expenses = state.expenses,
                categories = state.categories,
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun ExpenseHistoryContent(
    expenses: List<Expense>,
    categories: List<Category>,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredExpenses = expenses.filter { expense ->
        query.isBlank() ||
            expense.title.contains(query, ignoreCase = true) ||
            expense.note.contains(query, ignoreCase = true) ||
            expense.categoryId.contains(query, ignoreCase = true)
    }
    val categoriesById = categories.associateBy { it.id }
    val grouped = filteredExpenses.groupBy { expense ->
        CivilDate.fromEpochMillis(expense.createdAtMillis).key()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AppTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = "Search expenses",
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("Date range") })
                AssistChip(onClick = {}, label = { Text("Category") })
                AssistChip(onClick = {}, label = { Text("Amount") })
            }
        }
        if (filteredExpenses.isEmpty()) {
            item {
                EmptyContent(
                    title = "No expenses",
                    message = "Your saved expenses will appear here grouped by date.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                )
            }
        } else {
            grouped.forEach { (dateKey, itemsForDate) ->
                item(key = dateKey) {
                    Text(
                        text = dateLabel(dateKey),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                items(
                    items = itemsForDate,
                    key = { it.id },
                ) { expense ->
                    ExpenseHistoryRow(
                        expense = expense,
                        category = categoriesById[expense.categoryId],
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseHistoryRow(
    expense: Expense,
    category: Category?,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = category?.icon ?: "E",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(0.12f),
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
                    text = category?.name ?: expense.categoryId,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${ExpenseFormatter.formatAmount(expense.amountMinor)}₫",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

private fun dateLabel(dateKey: String): String {
    return when {
        dateKey.isBlank() -> "Unknown"
        else -> dateKey
    }
}
