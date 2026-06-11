package com.namvu.note.app.expense.domain.budget

data class Budget(
    val id: String,
    val name: String,
    val periodMonthKey: String,
    val amountMinor: Long,
    val categoryId: String? = null,
)

data class BudgetDraft(
    val name: String,
    val periodMonthKey: String,
    val amountMinor: Long,
    val categoryId: String? = null,
)

data class BudgetProgress(
    val budget: Budget,
    val spentMinor: Long,
) {
    val remainingMinor: Long
        get() = budget.amountMinor - spentMinor

    val progress: Float
        get() = if (budget.amountMinor <= 0L) 0f else (spentMinor.toFloat() / budget.amountMinor).coerceAtMost(1f)

    val isOverBudget: Boolean
        get() = spentMinor > budget.amountMinor
}
