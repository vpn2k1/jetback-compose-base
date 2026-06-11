package com.namvu.note.app.ui.home.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.note.app.expense.domain.model.Category
import com.namvu.note.app.expense.domain.model.Expense
import com.namvu.note.app.expense.domain.model.ExpenseFormatter
import com.namvu.note.app.ui.base.component.surface.AppCard

@Composable
internal fun HomeScreenExpenseItem(
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
            HomeScreenCategoryIconItem(
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
                HomeScreenSyncStatusItem(status = expense.syncStatus)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = ExpenseFormatter.formatAmount(expense.amountMinor),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AssistChip(onClick = onEditClick, label = { Text(text = "Edit") })
                    AssistChip(onClick = onDeleteClick, label = { Text(text = "Delete") })
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
    }
}
