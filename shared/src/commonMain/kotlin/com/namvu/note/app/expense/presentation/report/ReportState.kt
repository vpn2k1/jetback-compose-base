package com.namvu.note.app.expense.presentation.report

import com.namvu.note.app.expense.domain.report.FinancialReport
import com.namvu.note.app.expense.domain.report.ReportPeriod

data class ReportState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedPeriod: ReportPeriod = ReportPeriod.THIS_MONTH,
    val report: FinancialReport? = null,
)
