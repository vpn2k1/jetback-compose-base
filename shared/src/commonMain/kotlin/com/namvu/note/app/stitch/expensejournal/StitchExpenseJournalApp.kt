package com.namvu.note.app.stitch.expensejournal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalBottomBar
import com.namvu.note.app.stitch.expensejournal.components.StitchExpenseJournalTopBar
import com.namvu.note.app.stitch.expensejournal.screens.StitchExpenseJournalBudgetsScreen
import com.namvu.note.app.stitch.expensejournal.screens.StitchExpenseJournalDashboardScreen
import com.namvu.note.app.stitch.expensejournal.screens.StitchExpenseJournalEntryScreen
import com.namvu.note.app.stitch.expensejournal.screens.StitchExpenseJournalInsightsScreen
import com.namvu.note.app.stitch.expensejournal.screens.StitchExpenseJournalOnboardingScreen
import com.namvu.note.app.stitch.expensejournal.screens.StitchExpenseJournalSyncScreen

@Composable
fun StitchExpenseJournalApp() {
    var onboardingIndex by remember { mutableIntStateOf(0) }
    var hasFinishedOnboarding by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(StitchExpenseJournalTab.Dashboard) }
    val onboardingPages = StitchExpenseJournalSampleData.onboardingPages

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = StitchExpenseJournalColors.Background,
    ) {
        if (!hasFinishedOnboarding) {
            StitchExpenseJournalOnboardingScreen(
                page = onboardingPages[onboardingIndex],
                pageIndex = onboardingIndex,
                pageCount = onboardingPages.size,
                onNextClick = {
                    if (onboardingIndex == onboardingPages.lastIndex) {
                        hasFinishedOnboarding = true
                    } else {
                        onboardingIndex += 1
                    }
                },
                onSkipClick = { hasFinishedOnboarding = true },
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(StitchExpenseJournalColors.Background),
            ) {
                StitchExpenseJournalTopBar(
                    title = screenTitle(selectedTab),
                    subtitle = "Stitch project 8220887671920567100",
                )
                Box(Modifier.weight(1f)) {
                    when (selectedTab) {
                        StitchExpenseJournalTab.Dashboard -> StitchExpenseJournalDashboardScreen(
                            onQuickAddClick = { selectedTab = StitchExpenseJournalTab.Entry },
                        )
                        StitchExpenseJournalTab.Entry -> StitchExpenseJournalEntryScreen()
                        StitchExpenseJournalTab.Budgets -> StitchExpenseJournalBudgetsScreen()
                        StitchExpenseJournalTab.Insights -> StitchExpenseJournalInsightsScreen()
                        StitchExpenseJournalTab.Sync -> StitchExpenseJournalSyncScreen()
                    }
                }
                StitchExpenseJournalBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
        }
    }
}

private fun screenTitle(tab: StitchExpenseJournalTab): String {
    return when (tab) {
        StitchExpenseJournalTab.Dashboard -> "Dashboard"
        StitchExpenseJournalTab.Entry -> "Quick add"
        StitchExpenseJournalTab.Budgets -> "Budgets"
        StitchExpenseJournalTab.Insights -> "Insights"
        StitchExpenseJournalTab.Sync -> "Sheets sync"
    }
}
