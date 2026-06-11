package com.namvu.note.app.ui.budget.modules

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.namvu.note.app.expense.presentation.budget.BudgetState
import com.namvu.note.app.ui.base.component.feedback.EmptyContent
import com.namvu.note.app.ui.budget.items.BudgetScreenProgressItem

@Composable
internal fun BudgetScreenBodyModule(
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
            BudgetScreenEntryModule(
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
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                )
            }
        } else {
            items(items = state.budgets, key = { it.budget.id }) { budget ->
                BudgetScreenProgressItem(progress = budget)
            }
        }
    }
}
