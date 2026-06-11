package com.namvu.note.app.ui.home.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.namvu.note.app.expense.domain.model.Category
import com.namvu.note.app.expense.presentation.ExpenseJournalState
import com.namvu.note.app.ui.base.component.button.AppOutlinedButton
import com.namvu.note.app.ui.base.component.button.AppTextButton
import com.namvu.note.app.ui.base.component.input.AppTextField
import com.namvu.note.app.ui.base.component.surface.AppCard
import com.namvu.note.app.ui.home.modules.homeScreenExpenseFormSaveText

@Composable
internal fun HomeScreenExpenseFormItem(
    state: ExpenseJournalState,
    onCategorySelected: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onStartAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.formTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            AppTextButton(text = "Clear", onClick = onStartAddClick)
        }
        Spacer(modifier = Modifier.height(10.dp))
        HomeScreenCategoryPickerItem(
            categories = state.categories,
            selectedCategoryId = state.selectedCategoryId,
            onCategorySelected = onCategorySelected,
        )
        Spacer(modifier = Modifier.height(10.dp))
        AppTextField(
            value = state.titleInput,
            onValueChange = onTitleChange,
            label = "Description",
            placeholder = "Lunch",
            errorText = state.validationError.titleError,
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppTextField(
            value = state.amountInput,
            onValueChange = onAmountChange,
            label = "Amount",
            placeholder = "120k",
            errorText = state.validationError.amountError,
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppTextField(
            value = state.noteInput,
            onValueChange = onNoteChange,
            label = "Note",
            singleLine = false,
            minLines = 2,
            maxLines = 4,
        )
        Spacer(modifier = Modifier.height(12.dp))
        AppOutlinedButton(
            text = homeScreenExpenseFormSaveText(state.editingExpenseId),
            onClick = onSaveClick,
            enabled = !state.isSaving,
            isLoading = state.isSaving,
            fullWidth = true,
        )
    }
}

@Composable
private fun HomeScreenCategoryPickerItem(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(categories, key = { it.id }) { category ->
            FilterChip(
                selected = selectedCategoryId == category.id,
                onClick = { onCategorySelected(category.id) },
                label = { Text(text = category.name) },
                leadingIcon = {
                    HomeScreenCategoryIconItem(
                        color = category.color,
                        label = category.icon,
                    )
                },
            )
        }
    }
}
