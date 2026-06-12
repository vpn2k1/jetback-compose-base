package com.namvu.note.app.stitch.expensejournal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalColors
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalCard
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalPill
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalSectionTitle

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StitchExpenseJournalEntryScreen(
    modifier: Modifier = Modifier,
) {
    var category by remember { mutableStateOf("Food") }
    val categories = listOf("Food", "Transport", "Work", "Travel", "Groceries", "Social")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StitchExpenseJournalColors.Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        StitchExpenseJournalCard {
            Text("Fast transaction entry", color = StitchExpenseJournalColors.OnSurfaceMuted, fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
            Text("$ 12.50", color = StitchExpenseJournalColors.OnSurface, fontSize = 50.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("Lunch with Sarah", color = StitchExpenseJournalColors.OnSurfaceMuted, fontSize = 15.sp)
        }
        StitchExpenseJournalSectionTitle("Category")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            categories.forEach { item ->
                StitchExpenseJournalPill(
                    text = item,
                    tone = if (item == category) StitchExpenseJournalColors.Secondary else StitchExpenseJournalColors.OnSurfaceMuted,
                    modifier = Modifier.clickable { category = item },
                )
            }
        }
        StitchExpenseJournalCard {
            OutlinedTextField(
                value = "Client lunch near office",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Note") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = StitchExpenseJournalColors.OnSurface,
                    unfocusedTextColor = StitchExpenseJournalColors.OnSurface,
                    focusedBorderColor = StitchExpenseJournalColors.Primary,
                    unfocusedBorderColor = StitchExpenseJournalColors.Outline,
                    focusedLabelColor = StitchExpenseJournalColors.Primary,
                    unfocusedLabelColor = StitchExpenseJournalColors.OnSurfaceMuted,
                ),
            )
        }
        StitchExpenseJournalCard {
            StitchExpenseJournalSectionTitle("Smart suggestions")
            Spacer(Modifier.height(14.dp))
            StitchExpenseJournalPill("Most used: Food", StitchExpenseJournalColors.Secondary)
            Spacer(Modifier.height(10.dp))
            StitchExpenseJournalPill("Sheets sync queued", StitchExpenseJournalColors.Primary)
        }
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StitchExpenseJournalColors.PrimaryContainer),
        ) {
            Text("Save expense", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
