package com.namvu.note.app.stitch.expensejournal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.namvu.note.app.stitch.expensejournal.StitchExpenseJournalTab
import com.namvu.note.app.stitch.expensejournal.stitchPrimaryGradient

@Composable
fun StitchExpenseJournalTopBar(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box {
            Text(
                text = title,
                color = StitchExpenseJournalColors.OnSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = StitchExpenseJournalColors.OnSurfaceMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 32.dp),
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(stitchPrimaryGradient()),
            contentAlignment = Alignment.Center,
        ) {
            Text("N", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StitchExpenseJournalBottomBar(
    selectedTab: StitchExpenseJournalTab,
    onTabSelected: (StitchExpenseJournalTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(StitchExpenseJournalColors.Surface.copy(alpha = 0.94f))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        StitchExpenseJournalTab.entries.forEach { tab ->
            val selected = selectedTab == tab
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        if (selected) StitchExpenseJournalColors.PrimaryContainer else Color.Transparent,
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = if (selected) tab.label else tab.icon,
                    color = if (selected) Color.White else StitchExpenseJournalColors.OnSurfaceMuted,
                    fontSize = if (selected) 12.sp else 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        }
    }
}
