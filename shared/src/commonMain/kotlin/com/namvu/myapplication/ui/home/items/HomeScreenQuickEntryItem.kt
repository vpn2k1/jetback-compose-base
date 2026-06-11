package com.namvu.myapplication.ui.home.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.expense.presentation.ExpenseJournalState
import com.namvu.myapplication.expense.presentation.ExpenseTemplate
import com.namvu.myapplication.ui.base.component.button.AppButton
import com.namvu.myapplication.ui.base.component.input.AppTextField
import com.namvu.myapplication.ui.base.component.surface.AppCard
import com.namvu.myapplication.ui.home.modules.homeScreenPreviewText
import com.namvu.myapplication.ui.home.modules.homeScreenQuickEntryButtonText
import com.namvu.myapplication.ui.home.modules.homeScreenQuickEntryEnabled
import com.namvu.myapplication.ui.home.modules.homeScreenTemplateKey

@Composable
internal fun HomeScreenQuickEntryItem(
    state: ExpenseJournalState,
    onValueChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onTemplateClick: (ExpenseTemplate) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Text("Quick add", style = MaterialTheme.typography.titleMedium)
        Text("Type naturally, preview, save.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(10.dp))
        AppTextField(
            value = state.quickEntry,
            onValueChange = onValueChange,
            placeholder = "Coffee 45k today #drinks",
            errorText = state.validationError.titleError ?: state.validationError.amountError,
        )
        state.quickPreview?.let { preview ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(homeScreenPreviewText(preview))
        }
        if (state.recentTemplates.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            HomeScreenTemplateItem(templates = state.recentTemplates, onTemplateClick = onTemplateClick)
        }
        Spacer(modifier = Modifier.height(8.dp))
        AppButton(
            text = homeScreenQuickEntryButtonText(state.quickPreview),
            onClick = onAddClick,
            enabled = homeScreenQuickEntryEnabled(state),
            isLoading = state.isSaving,
            fullWidth = true,
        )
    }
}

@Composable
private fun HomeScreenTemplateItem(
    templates: List<ExpenseTemplate>,
    onTemplateClick: (ExpenseTemplate) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Recent templates", style = MaterialTheme.typography.labelLarge)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(templates, key = ::homeScreenTemplateKey) { template ->
                AssistChip(
                    onClick = { onTemplateClick(template) },
                    label = {
                        Text(
                            text = "${template.title} ${ExpenseFormatter.formatAmount(template.amountMinor)}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
            }
        }
    }
}
