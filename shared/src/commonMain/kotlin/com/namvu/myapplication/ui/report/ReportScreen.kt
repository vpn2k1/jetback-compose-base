package com.namvu.myapplication.ui.report

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.namvu.myapplication.expense.ExpenseJournalGraph
import com.namvu.myapplication.expense.domain.report.ReportPeriod
import com.namvu.myapplication.expense.presentation.report.ReportState
import com.namvu.myapplication.expense.presentation.report.ReportViewModel
import com.namvu.myapplication.ui.base.component.feedback.EmptyContent
import com.namvu.myapplication.ui.base.component.feedback.ErrorContent
import com.namvu.myapplication.ui.base.component.feedback.LoadingContent
import com.namvu.myapplication.ui.base.component.layout.AppScaffold
import com.namvu.myapplication.ui.report.modules.ReportScreenBodyModule

@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = remember {
        ReportViewModel(ExpenseJournalGraph.reportUseCases)
    },
) {
    ReportScreenContent(
        state = viewModel.state,
        onPeriodSelected = viewModel::onPeriodSelected,
        onRetryClick = viewModel::loadReport,
        modifier = modifier,
    )
}

@Composable
private fun ReportScreenContent(
    state: ReportState,
    onPeriodSelected: (ReportPeriod) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        title = "Reports",
        modifier = modifier,
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingContent(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
            )
            state.errorMessage != null -> ErrorContent(
                message = state.errorMessage,
                onRetryClick = onRetryClick,
                modifier = Modifier.fillMaxSize().padding(paddingValues),
            )
            state.report == null -> EmptyContent(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
            )
            else -> ReportScreenBodyModule(
                state = state,
                report = state.report,
                onPeriodSelected = onPeriodSelected,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}
