package com.namvu.myapplication.expense.domain.model

object ExpenseFormatter {
    fun formatAmount(amountMinor: Long): String {
        val sign = if (amountMinor < 0) "-" else ""
        val absolute = kotlin.math.abs(amountMinor)
        val whole = absolute / 100
        val cents = absolute % 100
        val grouped = whole.toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()

        return if (cents == 0L) {
            "$sign$grouped"
        } else {
            "$sign$grouped.${cents.toString().padStart(2, '0')}"
        }
    }
}
