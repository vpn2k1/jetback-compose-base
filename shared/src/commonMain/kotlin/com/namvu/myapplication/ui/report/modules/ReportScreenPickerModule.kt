package com.namvu.myapplication.ui.report.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.expense.domain.report.ReportPeriod

@Composable
internal fun ReportScreenPeriodPickerModule(
    selectedPeriod: ReportPeriod,
    onPeriodSelected: (ReportPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(
            ReportPeriod.TODAY to "Today",
            ReportPeriod.THIS_WEEK to "Week",
            ReportPeriod.THIS_MONTH to "Month",
            ReportPeriod.THIS_YEAR to "Year",
        ).forEach { (period, label) ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { Text(text = label) },
            )
        }
    }
}
