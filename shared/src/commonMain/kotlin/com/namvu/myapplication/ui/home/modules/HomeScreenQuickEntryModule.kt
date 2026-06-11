package com.namvu.myapplication.ui.home.modules

import com.namvu.myapplication.expense.domain.model.ExpenseFormatter
import com.namvu.myapplication.expense.presentation.ExpenseJournalState
import com.namvu.myapplication.expense.presentation.ExpenseTemplate
import com.namvu.myapplication.expense.presentation.QuickExpensePreview

internal fun homeScreenPreviewText(preview: QuickExpensePreview): String {
    return "${preview.title} | ${preview.categoryName} | ${ExpenseFormatter.formatAmount(preview.amountMinor)}"
}

internal fun homeScreenQuickEntryButtonText(preview: QuickExpensePreview?): String {
    return if (preview == null) "Add expense" else "Save preview"
}

internal fun homeScreenQuickEntryEnabled(state: ExpenseJournalState): Boolean {
    return state.quickEntry.isNotBlank() && state.quickPreview != null
}

internal fun homeScreenTemplateKey(template: ExpenseTemplate): String {
    return "${template.title}-${template.amountMinor}-${template.categoryId}"
}
