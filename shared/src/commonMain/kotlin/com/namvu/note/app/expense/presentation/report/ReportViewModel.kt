package com.namvu.note.app.expense.presentation.report

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namvu.note.app.expense.domain.report.ReportPeriod
import com.namvu.note.app.expense.domain.report.ReportUseCases
import kotlinx.coroutines.launch

class ReportViewModel(
    private val useCases: ReportUseCases,
) : ViewModel() {
    var state by mutableStateOf(ReportState())
        private set

    init {
        loadReport()
    }

    fun onPeriodSelected(period: ReportPeriod) {
        if (state.selectedPeriod == period) return
        state = state.copy(selectedPeriod = period)
        loadReport()
    }

    fun loadReport() {
        state = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { useCases.loadReport(state.selectedPeriod) }
                .onSuccess { report ->
                    state = state.copy(
                        isLoading = false,
                        report = report,
                    )
                }
                .onFailure { throwable ->
                    state = state.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Could not load reports",
                    )
                }
        }
    }
}
