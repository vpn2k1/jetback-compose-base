package com.namvu.myapplication.ui.home.modules

internal const val HOME_SCREEN_MONTHLY_BUDGET_MINOR = 1_200_000_000L

internal data class HomeScreenBudgetProgress(
    val spentMinor: Long,
    val remainingMinor: Long,
    val progress: Float,
)

internal fun calculateHomeScreenBudgetProgress(
    totalMinor: Long,
    monthlyBudgetMinor: Long = HOME_SCREEN_MONTHLY_BUDGET_MINOR,
): HomeScreenBudgetProgress {
    val spentMinor = totalMinor.coerceAtMost(monthlyBudgetMinor)
    val progress = if (monthlyBudgetMinor == 0L) {
        0f
    } else {
        spentMinor.toFloat() / monthlyBudgetMinor
    }
    return HomeScreenBudgetProgress(
        spentMinor = spentMinor,
        remainingMinor = monthlyBudgetMinor - spentMinor,
        progress = progress,
    )
}

internal fun calculateHomeScreenAverageMinor(
    totalMinor: Long,
    expenseCount: Int,
): Long = if (expenseCount == 0) 0L else totalMinor / expenseCount
