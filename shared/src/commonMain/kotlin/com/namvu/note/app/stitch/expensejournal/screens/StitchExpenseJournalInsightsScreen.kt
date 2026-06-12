package com.namvu.note.app.stitch.expensejournal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalColors
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalInsight
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalSampleData
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalCard
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalMetric
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalPill
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalRing
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalSectionTitle
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalSparkline

@Composable
fun StitchExpenseJournalInsightsScreen(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(StitchExpenseJournalColors.Background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            StitchExpenseJournalCard {
                StitchExpenseJournalSectionTitle("Financial pulse", "Monthly")
                Spacer(Modifier.height(16.dp))
                StitchExpenseJournalSparkline(
                    points = listOf(512f, 580f, 620f, 668f, 590f, 540f, 420f),
                    color = StitchExpenseJournalColors.Secondary,
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StitchExpenseJournalMetric("This month", "$2,380", Modifier.weight(1f))
                    StitchExpenseJournalMetric("Saved", "$420", Modifier.weight(1f), StitchExpenseJournalColors.Secondary)
                }
            }
        }
        item { StitchExpenseJournalSectionTitle("Insight cards") }
        items(StitchExpenseJournalSampleData.insights, key = { it.title }) { insight ->
            StitchExpenseJournalInsightRow(insight)
        }
        item {
            StitchExpenseJournalCard {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text("Category split", color = StitchExpenseJournalColors.OnSurface, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Text("Food 34%, Transport 18%, Subscriptions 14%, Work 10%, Other 24%.", color = StitchExpenseJournalColors.OnSurfaceMuted, lineHeight = 22.sp)
                    }
                    StitchExpenseJournalRing(0.34f, StitchExpenseJournalColors.Secondary)
                }
            }
        }
        item { Spacer(Modifier.height(90.dp)) }
    }
}

@Composable
private fun StitchExpenseJournalInsightRow(insight: StitchExpenseJournalInsight) {
    StitchExpenseJournalCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(insight.title, color = StitchExpenseJournalColors.OnSurfaceMuted, fontSize = 13.sp)
                Text(insight.value, color = StitchExpenseJournalColors.OnSurface, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }
            StitchExpenseJournalPill(insight.change, insight.tone)
        }
    }
}
