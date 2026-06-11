package com.namvu.myapplication.navigation

sealed class AppRoute(
    val route: String,
) {
    data object Home : AppRoute("home")
    data object Expenses : AppRoute("expenses")
    data object Reports : AppRoute("reports")
    data object Budgets : AppRoute("budgets")
    data object Settings : AppRoute("settings")
}
