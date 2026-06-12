package com.namvu.note.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.namvu.note.app.expense.presentation.ExpenseJournalViewModel
import com.namvu.note.app.ui.base.localization.AppLanguage
import com.namvu.note.app.ui.home.HomeScreen
import com.namvu.note.app.ui.base.theme.AppThemeMode
import com.namvu.note.app.ui.budget.BudgetScreen
import com.namvu.note.app.ui.expenses.ExpensesScreen
import com.namvu.note.app.ui.report.ReportScreen
import com.namvu.note.app.ui.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    expenseJournalViewModel: ExpenseJournalViewModel,
    modifier: Modifier = Modifier,
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    appLanguage: AppLanguage,
    onAppLanguageChange: (AppLanguage) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home.route,
        modifier = modifier,
    ) {
        composable(AppRoute.Home.route) {
            HomeScreen(viewModel = expenseJournalViewModel)
        }
        composable(AppRoute.Expenses.route) {
            ExpensesScreen(viewModel = expenseJournalViewModel)
        }
        composable(AppRoute.Reports.route) {
            ReportScreen()
        }
        composable(AppRoute.Budgets.route) {
            BudgetScreen()
        }
        composable(AppRoute.Settings.route) {
            SettingsScreen(
                googleSession = expenseJournalViewModel.state.syncSession,
                isGoogleActionRunning = expenseJournalViewModel.state.isSyncing,
                googleErrorMessage = expenseJournalViewModel.state.errorMessage,
                onGoogleSignOutClick = expenseJournalViewModel::signOutFromGoogle,
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange,
                appLanguage = appLanguage,
                onAppLanguageChange = onAppLanguageChange,
            )
        }
    }
}
