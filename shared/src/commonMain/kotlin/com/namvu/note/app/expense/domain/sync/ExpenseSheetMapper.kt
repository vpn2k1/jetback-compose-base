package com.namvu.note.app.expense.domain.sync

import com.namvu.note.app.expense.domain.model.Category
import com.namvu.note.app.expense.domain.model.Expense
import com.namvu.note.app.expense.domain.model.ExpenseFormatter

object ExpenseSheetMapper {
    fun monthKeyFor(epochMillis: Long): String {
        val days = floorDiv(epochMillis, MILLIS_PER_DAY)
        val date = civilFromDays(days)
        return "${date.year}-${date.month.toString().padStart(2, '0')}"
    }

    fun toRow(expense: Expense, category: Category?): GoogleSheetRow {
        return GoogleSheetRow(
            expenseId = expense.id,
            monthKey = monthKeyFor(expense.createdAtMillis),
            values = listOf(
                expense.id,
                expense.createdAtMillis.toString(),
                expense.title,
                ExpenseFormatter.formatAmount(expense.amountMinor),
                category?.name ?: expense.categoryId,
                expense.note,
                expense.updatedAtMillis.toString(),
            ),
        )
    }

    private fun floorDiv(value: Long, divisor: Long): Long {
        var result = value / divisor
        if ((value xor divisor) < 0 && result * divisor != value) {
            result--
        }
        return result
    }

    private fun civilFromDays(daysSinceEpoch: Long): CivilDate {
        var z = daysSinceEpoch + 719468
        val era = if (z >= 0) z / 146097 else (z - 146096) / 146097
        val dayOfEra = z - era * 146097
        val yearOfEra = (dayOfEra - dayOfEra / 1460 + dayOfEra / 36524 - dayOfEra / 146096) / 365
        var year = yearOfEra + era * 400
        val dayOfYear = dayOfEra - (365 * yearOfEra + yearOfEra / 4 - yearOfEra / 100)
        val monthPrime = (5 * dayOfYear + 2) / 153
        val day = dayOfYear - (153 * monthPrime + 2) / 5 + 1
        val month = monthPrime + if (monthPrime < 10) 3 else -9
        year += if (month <= 2) 1 else 0
        return CivilDate(year = year.toInt(), month = month.toInt(), day = day.toInt())
    }

    private data class CivilDate(
        val year: Int,
        val month: Int,
        val day: Int,
    )

    private const val MILLIS_PER_DAY = 86_400_000L
}
