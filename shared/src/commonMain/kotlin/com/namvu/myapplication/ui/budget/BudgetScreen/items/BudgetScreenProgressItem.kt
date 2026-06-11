package com.namvu.myapplication.ui.budget.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.domain.budget.BudgetProgress
import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.ui.base.component.surface.AppCard

@Composable
internal fun BudgetScreenProgressItem(
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
                Text(progress.budget.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = progress.budget.periodMonthKey,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = if (progress.isOverBudget) "Over" else "On track",
                style = MaterialTheme.typography.labelLarge,
                color = if (progress.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(progress = { progress.progress }, modifier = Modifier.fillMaxWidth())
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
