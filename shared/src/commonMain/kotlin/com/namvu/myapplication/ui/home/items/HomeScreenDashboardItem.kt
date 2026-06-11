package com.namvu.myapplication.ui.home.items

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.expense.presentation.ExpenseJournalState
import com.namvu.myapplication.ui.base.component.surface.AppCard
import com.namvu.myapplication.ui.home.modules.calculateHomeScreenAverageMinor
import com.namvu.myapplication.ui.home.modules.calculateHomeScreenBudgetProgress

@Composable
internal fun HomeScreenDashboardItem(
    state: ExpenseJournalState,
    totalMinor: Long,
    modifier: Modifier = Modifier,
) {
    val expenseCount = state.expenses.size
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column {
            Text("Good Morning, Nam", style = MaterialTheme.typography.headlineSmall)
            Text("Track money. Own your data.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HomeScreenBudgetItem(totalMinor = totalMinor, expenseCount = expenseCount)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HomeScreenMiniStatItem("Transactions", expenseCount.toString(), Modifier.weight(1f))
            HomeScreenMiniStatItem("Total spent", ExpenseFormatter.formatAmount(totalMinor), Modifier.weight(1f))
            HomeScreenMiniStatItem(
                "Average",
                ExpenseFormatter.formatAmount(
                    calculateHomeScreenAverageMinor(
                        totalMinor = totalMinor,
                        expenseCount = expenseCount,
                    ),
                ),
                Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HomeScreenBudgetItem(
    totalMinor: Long,
    expenseCount: Int,
) {
    val budgetProgress = calculateHomeScreenBudgetProgress(totalMinor)
    AppCard {
        Text("Monthly Budget", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            "${ExpenseFormatter.formatAmount(budgetProgress.spentMinor)} / 12,000,000 VND",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text("${ExpenseFormatter.formatAmount(budgetProgress.remainingMinor)} remaining | $expenseCount entries")
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(progress = { budgetProgress.progress }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun HomeScreenMiniStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
