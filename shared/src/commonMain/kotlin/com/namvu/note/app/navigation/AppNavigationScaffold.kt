package com.namvu.note.app.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.namvu.note.app.expense.ExpenseJournalGraph
import com.namvu.note.app.expense.presentation.ExpenseJournalViewModel
import com.namvu.note.app.ui.base.localization.AppLanguage
import com.namvu.note.app.ui.base.localization.AppText
import com.namvu.note.app.ui.base.localization.text
import com.namvu.note.app.ui.base.component.navigation.AppBottomNavigationBar
import com.namvu.note.app.ui.base.component.navigation.AppBottomTab
import com.namvu.note.app.ui.base.theme.AppThemeMode
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.ic_budget
import myapplication.shared.generated.resources.ic_expenses
import myapplication.shared.generated.resources.ic_home
import myapplication.shared.generated.resources.ic_reports
import myapplication.shared.generated.resources.ic_settings

@Composable
fun AppNavigationScaffold(
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    appLanguage: AppLanguage,
    onAppLanguageChange: (AppLanguage) -> Unit,
) {
    val navController = rememberNavController()
    val expenseJournalViewModel = remember {
        ExpenseJournalViewModel(ExpenseJournalGraph.useCases)
    }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val tabs = rememberBaseBottomTabs(appLanguage)
    val selectedRoute = tabs.firstOrNull { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }?.route

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            AppBottomNavigationBar(
                tabs = tabs,
                selectedRoute = selectedRoute,
                onTabSelected = { tab ->
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
    ) { paddingValues: PaddingValues ->
        AppNavHost(
            navController = navController,
            expenseJournalViewModel = expenseJournalViewModel,
            modifier = Modifier.padding(paddingValues),
            themeMode = themeMode,
            onThemeModeChange = onThemeModeChange,
            appLanguage = appLanguage,
            onAppLanguageChange = onAppLanguageChange,
        )
    }
}

@Composable
private fun rememberBaseBottomTabs(appLanguage: AppLanguage): List<AppBottomTab> {
    return listOf(
        AppBottomTab(
            route = AppRoute.Home.route,
            label = appLanguage.text(AppText.TabHome),
            icon = Res.drawable.ic_home,
        ),
        AppBottomTab(
            route = AppRoute.Expenses.route,
            label = appLanguage.text(AppText.TabExpenses),
            icon = Res.drawable.ic_expenses,
        ),
        AppBottomTab(
            route = AppRoute.Reports.route,
            label = appLanguage.text(AppText.TabReports),
            icon = Res.drawable.ic_reports,
        ),
        AppBottomTab(
            route = AppRoute.Budgets.route,
            label = appLanguage.text(AppText.TabBudgets),
            icon = Res.drawable.ic_budget,
        ),
        AppBottomTab(
            route = AppRoute.Settings.route,
            label = appLanguage.text(AppText.TabSettings),
            icon = Res.drawable.ic_settings,
        ),
    )
}
