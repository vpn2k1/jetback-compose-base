package com.namvu.note.app.expense.domain.insight

import com.namvu.note.app.expense.domain.model.Category
import com.namvu.note.app.expense.domain.model.Expense
import com.namvu.note.app.expense.domain.report.CivilDate
import kotlin.math.abs
import kotlin.math.roundToInt

class FinancialInsightEngine {
    fun generate(
        expenses: List<Expense>,
        categories: List<Category>,
        nowMillis: Long,
    ): List<FinancialInsight> {
        if (expenses.isEmpty()) {
            return listOf(
                FinancialInsight(
                    id = "empty-start",
                    type = InsightType.Forecast,
                    title = "No spending pattern yet",
                    description = "Add a few expenses to unlock local spending insights.",
                    severity = InsightSeverity.Info,
                    amount = null,
                    percentage = null,
                    createdAt = nowMillis,
                ),
            )
        }

        val categoryNames = categories.associate { it.id to it.name }
        return buildList {
            spendingComparison(expenses, nowMillis)?.let(::add)
            categoryInsight(expenses, categoryNames, nowMillis)?.let(::add)
            weekendWeekdayInsight(expenses, nowMillis)?.let(::add)
            highestSpendingDayInsight(expenses, nowMillis)?.let(::add)
            repeatedCoffeeInsight(expenses, nowMillis)?.let(::add)
            monthEndForecast(expenses, nowMillis)?.let(::add)
            anomalyInsight(expenses, nowMillis)?.let(::add)
        }.sortedWith(
            compareByDescending<FinancialInsight> { it.severity.rank }
                .thenBy { it.type.name },
        )
    }

    private fun spendingComparison(expenses: List<Expense>, nowMillis: Long): FinancialInsight? {
        val now = CivilDate.fromEpochMillis(nowMillis)
        val thisMonth = CivilDate(now.year, now.month, 1)
        val previousMonth = thisMonth.plusMonths(-1)
        val thisMonthTotal = expenses.totalForMonth(thisMonth.monthKey())
        val previousMonthTotal = expenses.totalForMonth(previousMonth.monthKey())
        if (thisMonthTotal == 0L || previousMonthTotal == 0L) return null

        val difference = thisMonthTotal - previousMonthTotal
        val percentage = difference.toFloat() / previousMonthTotal
        if (abs(percentage) < 0.10f) return null

        val increased = difference > 0
        return FinancialInsight(
            id = "month-comparison-${thisMonth.monthKey()}",
            type = InsightType.SpendingComparison,
            title = if (increased) "Spending is up this month" else "Spending is down this month",
            description = if (increased) {
                "This month is ${percentage.percentText()} higher than last month."
            } else {
                "This month is ${abs(percentage).percentText()} lower than last month."
            },
            severity = if (increased) InsightSeverity.Warning else InsightSeverity.Positive,
            amount = abs(difference),
            percentage = percentage,
            createdAt = nowMillis,
        )
    }

    private fun categoryInsight(
        expenses: List<Expense>,
        categoryNames: Map<String, String>,
        nowMillis: Long,
    ): FinancialInsight? {
        val monthKey = CivilDate.fromEpochMillis(nowMillis).monthKey()
        val monthExpenses = expenses.filter { it.monthKey() == monthKey }
        val total = monthExpenses.sumOf { it.amountMinor }
        if (total == 0L) return null
        val top = monthExpenses
            .groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amountMinor } }
            .maxByOrNull { it.value } ?: return null
        val share = top.value.toFloat() / total
        if (share < 0.35f) return null

        val name = categoryNames[top.key] ?: top.key
        return FinancialInsight(
            id = "category-${monthKey}-${top.key}",
            type = InsightType.Category,
            title = "$name leads this month",
            description = "$name accounts for ${share.percentText()} of this month's spending.",
            severity = if (share >= 0.55f) InsightSeverity.Warning else InsightSeverity.Info,
            amount = top.value,
            percentage = share,
            createdAt = nowMillis,
        )
    }

    private fun weekendWeekdayInsight(expenses: List<Expense>, nowMillis: Long): FinancialInsight? {
        val monthKey = CivilDate.fromEpochMillis(nowMillis).monthKey()
        val monthExpenses = expenses.filter { it.monthKey() == monthKey }
        if (monthExpenses.size < 4) return null
        val weekend = monthExpenses
            .filter { it.isWeekend() }
            .sumOf { it.amountMinor }
        val weekday = monthExpenses
            .filterNot { it.isWeekend() }
            .sumOf { it.amountMinor }
        val total = weekend + weekday
        if (total == 0L) return null
        val weekendShare = weekend.toFloat() / total

        return FinancialInsight(
            id = "weekend-weekday-$monthKey",
            type = InsightType.WeekendWeekday,
            title = if (weekend > weekday) "Weekend spending is heavier" else "Weekday spending is heavier",
            description = if (weekend > weekday) {
                "Weekend expenses make up ${weekendShare.percentText()} of this month's spending."
            } else {
                "Weekday expenses make up ${(1f - weekendShare).percentText()} of this month's spending."
            },
            severity = if (weekendShare >= 0.45f) InsightSeverity.Warning else InsightSeverity.Info,
            amount = maxOf(weekend, weekday),
            percentage = if (weekend > weekday) weekendShare else 1f - weekendShare,
            createdAt = nowMillis,
        )
    }

    private fun highestSpendingDayInsight(expenses: List<Expense>, nowMillis: Long): FinancialInsight? {
        val monthKey = CivilDate.fromEpochMillis(nowMillis).monthKey()
        val day = expenses
            .filter { it.monthKey() == monthKey }
            .groupBy { CivilDate.fromEpochMillis(it.createdAtMillis).key() }
            .mapValues { entry -> entry.value.sumOf { it.amountMinor } }
            .maxByOrNull { it.value } ?: return null
        if (day.value == 0L) return null

        return FinancialInsight(
            id = "highest-day-${day.key}",
            type = InsightType.HighestSpendingDay,
            title = "Highest spending day",
            description = "${day.key} is currently your highest spending day this month.",
            severity = InsightSeverity.Info,
            amount = day.value,
            percentage = null,
            createdAt = nowMillis,
        )
    }

    private fun repeatedCoffeeInsight(expenses: List<Expense>, nowMillis: Long): FinancialInsight? {
        val monthKey = CivilDate.fromEpochMillis(nowMillis).monthKey()
        val coffeeExpenses = expenses.filter { expense ->
            expense.monthKey() == monthKey &&
                coffeeKeywords.any { keyword -> keyword in expense.title.lowercase() }
        }
        if (coffeeExpenses.size < 3) return null
        val total = coffeeExpenses.sumOf { it.amountMinor }
        return FinancialInsight(
            id = "coffee-$monthKey",
            type = InsightType.RepeatedSpending,
            title = "Coffee adds up",
            description = "${coffeeExpenses.size} coffee-like expenses were recorded this month.",
            severity = if (coffeeExpenses.size >= 8) InsightSeverity.Warning else InsightSeverity.Info,
            amount = total,
            percentage = null,
            createdAt = nowMillis,
        )
    }

    private fun monthEndForecast(expenses: List<Expense>, nowMillis: Long): FinancialInsight? {
        val now = CivilDate.fromEpochMillis(nowMillis)
        val monthKey = now.monthKey()
        val monthExpenses = expenses.filter { it.monthKey() == monthKey }
        val total = monthExpenses.sumOf { it.amountMinor }
        if (total == 0L || now.day <= 1) return null
        val daysInMonth = CivilDate.daysInMonth(now.year, now.month)
        val forecast = (total / now.day) * daysInMonth
        return FinancialInsight(
            id = "forecast-$monthKey",
            type = InsightType.Forecast,
            title = "Month-end forecast",
            description = "At this pace, projected month-end spending is about ${forecast.wholeAmountText()}.",
            severity = InsightSeverity.Info,
            amount = forecast,
            percentage = null,
            createdAt = nowMillis,
        )
    }

    private fun anomalyInsight(expenses: List<Expense>, nowMillis: Long): FinancialInsight? {
        val monthKey = CivilDate.fromEpochMillis(nowMillis).monthKey()
        val monthExpenses = expenses.filter { it.monthKey() == monthKey }
        if (monthExpenses.size < 4) return null
        val average = monthExpenses.sumOf { it.amountMinor } / monthExpenses.size
        if (average <= 0L) return null
        val largest = monthExpenses.maxByOrNull { it.amountMinor } ?: return null
        if (largest.amountMinor < average * 2) return null
        val ratio = largest.amountMinor.toFloat() / average

        return FinancialInsight(
            id = "anomaly-${largest.id}",
            type = InsightType.Anomaly,
            title = "Unusual expense detected",
            description = "${largest.title} is ${ratio.formatOneDecimal()}x your average expense this month.",
            severity = if (ratio >= 4f) InsightSeverity.Critical else InsightSeverity.Warning,
            amount = largest.amountMinor,
            percentage = ratio,
            createdAt = nowMillis,
        )
    }

    private fun List<Expense>.totalForMonth(monthKey: String): Long {
        return filter { it.monthKey() == monthKey }.sumOf { it.amountMinor }
    }

    private fun Expense.monthKey(): String {
        return CivilDate.fromEpochMillis(createdAtMillis).monthKey()
    }

    private fun Expense.isWeekend(): Boolean {
        val dayOfWeek = CivilDate.fromEpochMillis(createdAtMillis).dayOfWeekMondayBased
        return dayOfWeek >= 5
    }

    private fun Float.percentText(): String {
        return "${(this * 100).roundToInt()}%"
    }

    private fun Float.formatOneDecimal(): String {
        val rounded = (this * 10).roundToInt() / 10f
        return if (rounded % 1f == 0f) rounded.toInt().toString() else rounded.toString()
    }

    private fun Long.wholeAmountText(): String {
        val whole = this / 100
        return whole.toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    }

    private val InsightSeverity.rank: Int
        get() = when (this) {
            InsightSeverity.Critical -> 4
            InsightSeverity.Warning -> 3
            InsightSeverity.Positive -> 2
            InsightSeverity.Info -> 1
        }

    private companion object {
        val coffeeKeywords = listOf("coffee", "cafe", "latte", "espresso", "cappuccino")
    }
}
