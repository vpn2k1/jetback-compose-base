package com.namvu.myapplication.ui.expenses.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.domain.model.Category
import com.namvu.myapplication.expense.domain.model.Expense
import com.namvu.myapplication.expense.domain.report.CivilDate
import com.namvu.myapplication.ui.base.component.feedback.EmptyContent
import com.namvu.myapplication.ui.base.component.input.AppTextField
import com.namvu.myapplication.ui.expenses.items.ExpensesScreenHistoryItem

@Composable
internal fun ExpensesScreenHistoryModule(
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
    val grouped = filteredExpenses.groupBy { CivilDate.fromEpochMillis(it.createdAtMillis).key() }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { ExpensesScreenSearchModule(query = query, onQueryChange = onQueryChange) }
        if (filteredExpenses.isEmpty()) {
            item {
                EmptyContent(
                    title = "No expenses",
                    message = "Your saved expenses will appear here grouped by date.",
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                )
            }
        } else {
            grouped.forEach { (dateKey, itemsForDate) ->
                item(key = dateKey) {
                    Text(dateKey, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                items(items = itemsForDate, key = { it.id }) { expense ->
                    ExpensesScreenHistoryItem(
                        expense = expense,
                        category = categoriesById[expense.categoryId],
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpensesScreenSearchModule(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    AppTextField(value = query, onValueChange = onQueryChange, placeholder = "Search expenses")
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AssistChip(onClick = {}, label = { Text("Date range") })
        AssistChip(onClick = {}, label = { Text("Category") })
        AssistChip(onClick = {}, label = { Text("Amount") })
    }
}
