package com.namvu.myapplication.expense.domain.report

object ReportPeriodCalculator {
    fun rangeFor(
        period: ReportPeriod,
        nowMillis: Long,
        customRange: ReportDateRange? = null,
    ): ReportDateRange {
        if (period == ReportPeriod.CUSTOM) {
            return requireNotNull(customRange) { "Custom report range is required" }
        }

        val date = CivilDate.fromEpochMillis(nowMillis)
        return when (period) {
            ReportPeriod.TODAY -> ReportDateRange(
                startMillis = date.toEpochMillis(),
                endExclusiveMillis = date.plusDays(1).toEpochMillis(),
            )
            ReportPeriod.THIS_WEEK -> {
                val weekStart = date.plusDays(-date.dayOfWeekMondayBased.toLong())
                ReportDateRange(
                    startMillis = weekStart.toEpochMillis(),
                    endExclusiveMillis = weekStart.plusDays(7).toEpochMillis(),
                )
            }
            ReportPeriod.THIS_MONTH -> ReportDateRange(
                startMillis = CivilDate(date.year, date.month, 1).toEpochMillis(),
                endExclusiveMillis = CivilDate(date.year, date.month, 1).plusMonths(1).toEpochMillis(),
            )
            ReportPeriod.THIS_YEAR -> ReportDateRange(
                startMillis = CivilDate(date.year, 1, 1).toEpochMillis(),
                endExclusiveMillis = CivilDate(date.year + 1, 1, 1).toEpochMillis(),
            )
            ReportPeriod.CUSTOM -> error("Handled above")
        }
    }

    fun labelFor(period: ReportPeriod): String {
        return when (period) {
            ReportPeriod.TODAY -> "Today"
            ReportPeriod.THIS_WEEK -> "This week"
            ReportPeriod.THIS_MONTH -> "This month"
            ReportPeriod.THIS_YEAR -> "This year"
            ReportPeriod.CUSTOM -> "Custom"
        }
    }
}

data class CivilDate(
    val year: Int,
    val month: Int,
    val day: Int,
) {
    val dayOfWeekMondayBased: Int
        get() = (((daysSinceEpoch() + 3) % 7 + 7) % 7).toInt()

    fun toEpochMillis(): Long {
        return daysSinceEpoch() * MILLIS_PER_DAY
    }

    fun plusDays(days: Long): CivilDate {
        return fromEpochDay(daysSinceEpoch() + days)
    }

    fun plusMonths(months: Int): CivilDate {
        val monthIndex = year * 12 + (month - 1) + months
        val newYear = floorDiv(monthIndex, 12)
        val newMonth = floorMod(monthIndex, 12) + 1
        return CivilDate(
            year = newYear,
            month = newMonth,
            day = minOf(day, daysInMonth(newYear, newMonth)),
        )
    }

    fun key(): String {
        return "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }

    fun monthKey(): String {
        return "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}"
    }

    private fun daysSinceEpoch(): Long {
        var adjustedYear = year
        val adjustedMonth = month
        adjustedYear -= if (adjustedMonth <= 2) 1 else 0
        val era = floorDiv(adjustedYear, 400)
        val yearOfEra = adjustedYear - era * 400
        val monthPrime = adjustedMonth + if (adjustedMonth > 2) -3 else 9
        val dayOfYear = (153 * monthPrime + 2) / 5 + day - 1
        val dayOfEra = yearOfEra * 365 + yearOfEra / 4 - yearOfEra / 100 + dayOfYear
        return era.toLong() * 146097L + dayOfEra.toLong() - 719468L
    }

    companion object {
        fun fromEpochMillis(epochMillis: Long): CivilDate {
            return fromEpochDay(floorDiv(epochMillis, MILLIS_PER_DAY))
        }

        fun fromEpochDay(epochDay: Long): CivilDate {
            var z = epochDay + 719468
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

        fun daysInMonth(year: Int, month: Int): Int {
            return when (month) {
                1, 3, 5, 7, 8, 10, 12 -> 31
                4, 6, 9, 11 -> 30
                2 -> if (isLeapYear(year)) 29 else 28
                else -> error("Invalid month: $month")
            }
        }

        private fun isLeapYear(year: Int): Boolean {
            return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
        }
    }
}

private fun floorDiv(value: Long, divisor: Long): Long {
    var result = value / divisor
    if ((value xor divisor) < 0 && result * divisor != value) {
        result--
    }
    return result
}

private fun floorDiv(value: Int, divisor: Int): Int {
    var result = value / divisor
    if ((value xor divisor) < 0 && result * divisor != value) {
        result--
    }
    return result
}

private fun floorMod(value: Int, divisor: Int): Int {
    return value - floorDiv(value, divisor) * divisor
}

private const val MILLIS_PER_DAY = 86_400_000L
