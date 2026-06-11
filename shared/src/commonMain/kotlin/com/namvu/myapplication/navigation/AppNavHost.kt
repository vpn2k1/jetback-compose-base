package com.namvu.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.namvu.myapplication.ui.home.HomeScreen
import com.namvu.myapplication.ui.base.theme.AppThemeMode
import com.namvu.myapplication.ui.budget.BudgetScreen
import com.namvu.myapplication.ui.expenses.ExpensesScreen
import com.namvu.myapplication.ui.report.ReportScreen
import com.namvu.myapplication.ui.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home.route,
        modifier = modifier,
    ) {
        composable(AppRoute.Home.route) {
            HomeScreen()
        }
        composable(AppRoute.Expenses.route) {
            ExpensesScreen()
        }
        composable(AppRoute.Reports.route) {
            ReportScreen()
        }
        composable(AppRoute.Budgets.route) {
            BudgetScreen()
        }
        composable(AppRoute.Settings.route) {
            SettingsScreen(
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange,
            )
        }
    }
}
