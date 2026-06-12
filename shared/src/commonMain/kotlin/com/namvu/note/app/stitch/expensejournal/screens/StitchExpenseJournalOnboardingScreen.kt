package com.namvu.note.app.stitch.expensejournal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalOnboardingPage
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalCard
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalPill
import com.namvu.note.app.stitch.expensejournal.stitchPrimaryGradient

@Composable
fun StitchExpenseJournalOnboardingScreen(
    page: StitchExpenseJournalOnboardingPage,
    pageIndex: Int,
    pageCount: Int,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StitchExpenseJournalColors.Background)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Expense Journal", color = StitchExpenseJournalColors.OnSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Skip", color = StitchExpenseJournalColors.OnSurfaceMuted, modifier = Modifier.padding(12.dp))
        }
        StitchExpenseJournalCard {
            StitchExpenseJournalPill(page.eyebrow, StitchExpenseJournalColors.Secondary)
            Spacer(Modifier.height(42.dp))
            Box(
                modifier = Modifier
                    .size(148.dp)
                    .clip(CircleShape)
                    .background(stitchPrimaryGradient()),
                contentAlignment = Alignment.Center,
            ) {
                Text(page.metric, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(42.dp))
            Text(page.title, color = StitchExpenseJournalColors.OnSurface, fontSize = 34.sp, lineHeight = 39.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Text(page.body, color = StitchExpenseJournalColors.OnSurfaceMuted, fontSize = 15.sp, lineHeight = 23.sp)
        }
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(pageCount) { index ->
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .weight(if (index == pageIndex) 1.8f else 1f)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                if (index == pageIndex) StitchExpenseJournalColors.Primary else StitchExpenseJournalColors.Outline,
                            ),
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onNextClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StitchExpenseJournalColors.PrimaryContainer),
            ) {
                Text(if (pageIndex == pageCount - 1) "Get started" else "Continue", color = Color.White)
            }
        }
    }
}
