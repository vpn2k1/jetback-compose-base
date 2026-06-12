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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalBudget
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalColors
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalSampleData
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalCard
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalProgress
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalRing
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalSectionTitle

@Composable
fun StitchExpenseJournalBudgetsScreen(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(StitchExpenseJournalColors.Background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            StitchExpenseJournalCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("You have", color = StitchExpenseJournalColors.OnSurfaceMuted, fontSize = 13.sp)
                        Text("$540 left this week", color = StitchExpenseJournalColors.OnSurface, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Social budget is approaching its limit.", color = StitchExpenseJournalColors.OnSurfaceMuted)
                    }
                    StitchExpenseJournalRing(0.76f, StitchExpenseJournalColors.Tertiary)
                }
            }
        }
        item { StitchExpenseJournalSectionTitle("Budgets & goals", "Add goal") }
        items(StitchExpenseJournalSampleData.budgets, key = { it.name }) { budget ->
            StitchExpenseJournalBudgetRow(budget)
        }
        item {
            StitchExpenseJournalCard {
                Text("Calm warning", color = StitchExpenseJournalColors.Tertiary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("You have spent 82% of Social. Move one dinner to home cooking to save about $45.", color = StitchExpenseJournalColors.OnSurface, lineHeight = 22.sp)
            }
        }
        item { Spacer(Modifier.height(90.dp)) }
    }
}

@Composable
private fun StitchExpenseJournalBudgetRow(budget: StitchExpenseJournalBudget) {
    StitchExpenseJournalCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(budget.name, color = StitchExpenseJournalColors.OnSurface, fontWeight = FontWeight.SemiBold)
                Text("${budget.spent} of ${budget.limit}", color = StitchExpenseJournalColors.OnSurfaceMuted, fontSize = 12.sp)
            }
            Text("${(budget.progress * 100).toInt()}%", color = budget.tone, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))
        StitchExpenseJournalProgress(budget.progress, budget.tone)
    }
}
