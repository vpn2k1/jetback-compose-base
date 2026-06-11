package com.namvu.myapplication.expense.domain.report

data class ReportDateRange(
    val startMillis: Long,
    val endExclusiveMillis: Long,
) {
    fun contains(epochMillis: Long): Boolean {
        return epochMillis >= startMillis && epochMillis < endExclusiveMillis
    }
}
