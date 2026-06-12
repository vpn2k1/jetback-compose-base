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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalColors
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalSampleData
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalTransaction
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalCard
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalMetric
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalPill
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalProgress
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalSectionTitle
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalSparkline

@Composable
fun StitchExpenseJournalDashboardScreen(
    onQuickAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(StitchExpenseJournalColors.Background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            StitchExpenseJournalCard {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("Available budget", color = StitchExpenseJournalColors.OnSurfaceMuted, fontSize = 13.sp)
                        Text("$5,240", color = StitchExpenseJournalColors.OnSurface, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                    }
                    StitchExpenseJournalPill("Up to date", StitchExpenseJournalColors.Secondary)
                }
                Spacer(Modifier.height(18.dp))
                StitchExpenseJournalSparkline(listOf(42f, 58f, 48f, 74f, 68f, 92f, 81f))
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StitchExpenseJournalMetric("Today", "$62", Modifier.weight(1f), StitchExpenseJournalColors.Secondary)
                    StitchExpenseJournalMetric("Month", "$1,380", Modifier.weight(1f), StitchExpenseJournalColors.Primary)
                }
            }
        }
        item {
            StitchExpenseJournalCard {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("Monthly budget", color = StitchExpenseJournalColors.OnSurfaceMuted, fontSize = 13.sp)
                        Text("$1,860 remaining", color = StitchExpenseJournalColors.OnSurface, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text("72%", color = StitchExpenseJournalColors.Tertiary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                StitchExpenseJournalProgress(0.72f, StitchExpenseJournalColors.Tertiary)
            }
        }
        item { StitchExpenseJournalSectionTitle("Recent expenses", "View all") }
        items(StitchExpenseJournalSampleData.transactions, key = { it.merchant }) { transaction ->
            StitchExpenseJournalTransactionRow(transaction)
        }
        item { Spacer(Modifier.height(90.dp)) }
    }
    FloatingActionButton(
        onClick = onQuickAddClick,
        modifier = Modifier.padding(20.dp),
        containerColor = StitchExpenseJournalColors.PrimaryContainer,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
    ) {
        Text("+", fontSize = 28.sp, fontWeight = FontWeight.Light)
    }
}

@Composable
private fun StitchExpenseJournalTransactionRow(transaction: StitchExpenseJournalTransaction) {
    StitchExpenseJournalCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = transaction.category.take(1),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(transaction.color)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            )
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(transaction.merchant, color = StitchExpenseJournalColors.OnSurface, fontWeight = FontWeight.SemiBold)
                Text("${transaction.category} - ${transaction.time}", color = StitchExpenseJournalColors.OnSurfaceMuted, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(transaction.amount, color = StitchExpenseJournalColors.OnSurface, fontWeight = FontWeight.Bold)
                Text(transaction.status, color = transaction.color, fontSize = 12.sp)
            }
        }
    }
}
