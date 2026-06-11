package com.namvu.note.app.expense

import com.namvu.note.app.expense.domain.report.CivilDate
import com.namvu.note.app.expense.domain.report.ReportPeriod
import com.namvu.note.app.expense.domain.report.ReportPeriodCalculator
import kotlin.test.Test
import kotlin.test.assertEquals

class ReportPeriodCalculatorTest {
    @Test
    fun calculatesTodayRange() {
        val now = CivilDate(2026, 6, 11).toEpochMillis() + 12 * 60 * 60 * 1000

        val range = ReportPeriodCalculator.rangeFor(ReportPeriod.TODAY, now)

        assertEquals(CivilDate(2026, 6, 11).toEpochMillis(), range.startMillis)
        assertEquals(CivilDate(2026, 6, 12).toEpochMillis(), range.endExclusiveMillis)
    }

    @Test
    fun calculatesWeekStartingMonday() {
        val now = CivilDate(2026, 6, 11).toEpochMillis()

        val range = ReportPeriodCalculator.rangeFor(ReportPeriod.THIS_WEEK, now)

        assertEquals(CivilDate(2026, 6, 8).toEpochMillis(), range.startMillis)
        assertEquals(CivilDate(2026, 6, 15).toEpochMillis(), range.endExclusiveMillis)
    }

    @Test
    fun calculatesMonthAndYearRanges() {
        val now = CivilDate(2026, 6, 11).toEpochMillis()

        val month = ReportPeriodCalculator.rangeFor(ReportPeriod.THIS_MONTH, now)
        val year = ReportPeriodCalculator.rangeFor(ReportPeriod.THIS_YEAR, now)

        assertEquals(CivilDate(2026, 6, 1).toEpochMillis(), month.startMillis)
        assertEquals(CivilDate(2026, 7, 1).toEpochMillis(), month.endExclusiveMillis)
        assertEquals(CivilDate(2026, 1, 1).toEpochMillis(), year.startMillis)
        assertEquals(CivilDate(2027, 1, 1).toEpochMillis(), year.endExclusiveMillis)
    }
}
