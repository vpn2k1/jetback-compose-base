package com.namvu.note.app.stitch.expensejournal

import androidx.compose.ui.graphics.Color

enum class StitchExpenseJournalTab(
    val label: String,
    val icon: String,
) {
    Dashboard("Home", "H"),
    Entry("Add", "+"),
    Budgets("Budget", "B"),
    Insights("Insights", "I"),
    Sync("Sync", "S"),
}

data class StitchExpenseJournalTransaction(
    val merchant: String,
    val category: String,
    val amount: String,
    val time: String,
    val status: String,
    val color: Color,
)

data class StitchExpenseJournalBudget(
    val name: String,
    val spent: String,
    val limit: String,
    val progress: Float,
    val tone: Color,
)

data class StitchExpenseJournalInsight(
    val title: String,
    val value: String,
    val change: String,
    val tone: Color,
)

data class StitchExpenseJournalOnboardingPage(
    val eyebrow: String,
    val title: String,
    val body: String,
    val metric: String,
)

object StitchExpenseJournalSampleData {
    val transactions = listOf(
        StitchExpenseJournalTransaction("Blue Bottle", "Food", "-$8.20", "08:15", "Synced", Color(0xFF4EDEA3)),
        StitchExpenseJournalTransaction("Uber", "Transport", "-$15.50", "09:02", "Synced", Color(0xFFC3C0FF)),
        StitchExpenseJournalTransaction("Whole Foods", "Groceries", "-$36.00", "12:10", "Queued", Color(0xFFFFB95F)),
        StitchExpenseJournalTransaction("Figma", "Work", "-$12.00", "14:05", "Synced", Color(0xFF8EA7FF)),
    )

    val budgets = listOf(
        StitchExpenseJournalBudget("Groceries", "$612", "$820", 0.74f, Color(0xFF4EDEA3)),
        StitchExpenseJournalBudget("Transport", "$188", "$260", 0.72f, Color(0xFFC3C0FF)),
        StitchExpenseJournalBudget("Social", "$246", "$300", 0.82f, Color(0xFFFFB95F)),
        StitchExpenseJournalBudget("Subscriptions", "$84", "$120", 0.70f, Color(0xFF8EA7FF)),
    )

    val insights = listOf(
        StitchExpenseJournalInsight("Daily average", "$81", "-8% vs last week", Color(0xFF4EDEA3)),
        StitchExpenseJournalInsight("Month forecast", "$2,530", "+5% vs budget", Color(0xFFFFB95F)),
        StitchExpenseJournalInsight("Saved this month", "$420", "+$84 vs May", Color(0xFFC3C0FF)),
    )

    val onboardingPages = listOf(
        StitchExpenseJournalOnboardingPage(
            eyebrow = "Ultra fast capture",
            title = "Record expenses before the receipt fades.",
            body = "Type an amount, pick a smart category, and keep moving. Expense Journal is designed around two-tap entry.",
            metric = "2 taps",
        ),
        StitchExpenseJournalOnboardingPage(
            eyebrow = "Google Sheets sync",
            title = "Your ledger stays portable and current.",
            body = "Every entry is queued, synced, and visible with clear status so finance data never feels trapped.",
            metric = "12 sec",
        ),
        StitchExpenseJournalOnboardingPage(
            eyebrow = "Financial pulse",
            title = "See budgets, burn rate, and savings in one flow.",
            body = "Dark-first analytics, calm warnings, and realistic insights help you spend with intention.",
            metric = "76%",
        ),
    )
}
