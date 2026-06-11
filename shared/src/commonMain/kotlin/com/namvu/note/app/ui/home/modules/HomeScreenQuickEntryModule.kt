package com.namvu.note.app.ui.home.modules

import com.namvu.note.app.expense.domain.model.ExpenseFormatter
import com.namvu.note.app.expense.presentation.ExpenseJournalState
import com.namvu.note.app.expense.presentation.ExpenseTemplate
import com.namvu.note.app.expense.presentation.QuickExpensePreview

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
