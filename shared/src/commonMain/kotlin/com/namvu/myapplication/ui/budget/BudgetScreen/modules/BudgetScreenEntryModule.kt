package com.namvu.myapplication.ui.budget.modules

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.presentation.budget.BudgetState
import com.namvu.myapplication.ui.base.component.button.AppButton
import com.namvu.myapplication.ui.base.component.input.AppTextField
import com.namvu.myapplication.ui.base.component.surface.AppCard

@Composable
internal fun BudgetScreenEntryModule(
    state: BudgetState,
    onBudgetInputChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Text("Monthly budget", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Reports work offline from local expenses. This budget foundation tracks the current month.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(
            value = state.budgetInput,
            onValueChange = onBudgetInputChange,
            label = "Limit",
            placeholder = "5m",
            errorText = state.validationError.amountError,
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppButton(
            text = "Save budget",
            onClick = onSaveClick,
            enabled = state.budgetInput.isNotBlank(),
            isLoading = state.isSaving,
            fullWidth = true,
        )
    }
}
