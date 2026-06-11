package com.namvu.myapplication.ui.report.modules

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
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.expense.domain.report.CategoryBreakdownItem
import com.namvu.myapplication.ui.base.component.surface.AppCard

@Composable
internal fun ReportScreenCategoryBreakdownModule(
    items: List<CategoryBreakdownItem>,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Text("Category breakdown", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        if (items.isEmpty()) {
            Text("No category spending in this period.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.forEach { item -> ReportScreenBreakdownItem(item = item) }
            }
        }
    }
}

@Composable
private fun ReportScreenBreakdownItem(item: CategoryBreakdownItem) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = item.categoryName, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = ExpenseFormatter.formatAmount(item.amountMinor),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        LinearProgressIndicator(progress = { item.share }, modifier = Modifier.fillMaxWidth())
    }
}
