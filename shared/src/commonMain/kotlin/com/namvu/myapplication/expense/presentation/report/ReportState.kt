package com.namvu.myapplication.expense.presentation.report

import com.namvu.myapplication.expense.domain.report.FinancialReport
import com.namvu.myapplication.expense.domain.report.ReportPeriod

data class ReportState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedPeriod: ReportPeriod = ReportPeriod.THIS_MONTH,
    val report: FinancialReport? = null,
)
