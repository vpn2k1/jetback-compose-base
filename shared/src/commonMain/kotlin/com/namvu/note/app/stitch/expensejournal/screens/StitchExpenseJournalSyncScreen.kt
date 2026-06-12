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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalColors
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalCard
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalMetric
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalPill
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalSectionTitle

@Composable
fun StitchExpenseJournalSyncScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StitchExpenseJournalColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        StitchExpenseJournalCard {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Google Sheets", color = StitchExpenseJournalColors.OnSurfaceMuted, fontSize = 13.sp)
                    Text("Connected", color = StitchExpenseJournalColors.OnSurface, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                StitchExpenseJournalPill("Live", StitchExpenseJournalColors.Secondary)
            }
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StitchExpenseJournalMetric("Rows", "142", Modifier.weight(1f), StitchExpenseJournalColors.Secondary)
                StitchExpenseJournalMetric("Pending", "5", Modifier.weight(1f), StitchExpenseJournalColors.Tertiary)
            }
        }
        StitchExpenseJournalCard {
            StitchExpenseJournalSectionTitle("Expense Journal -> Sheets")
            Spacer(Modifier.height(14.dp))
            SyncLine("Monthly ledger", "Updated 12 seconds ago", StitchExpenseJournalColors.Secondary)
            SyncLine("Offline queue", "5 entries waiting", StitchExpenseJournalColors.Tertiary)
            SyncLine("Conflict policy", "Newest local edit wins", StitchExpenseJournalColors.Primary)
        }
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(58.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StitchExpenseJournalColors.PrimaryContainer),
        ) {
            Text("Sync now", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(90.dp))
    }
}

@Composable
private fun SyncLine(title: String, subtitle: String, tone: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(title, color = StitchExpenseJournalColors.OnSurface, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = StitchExpenseJournalColors.OnSurfaceMuted, fontSize = 12.sp)
        }
        StitchExpenseJournalPill("OK", tone)
    }
}
