package com.namvu.myapplication.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.namvu.myapplication.ui.base.component.navigation.AppBottomNavigationBar
import com.namvu.myapplication.ui.base.component.navigation.AppBottomTab
import com.namvu.myapplication.ui.base.theme.AppThemeMode
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.ic_budget
import myapplication.shared.generated.resources.ic_expenses
import myapplication.shared.generated.resources.ic_home
import myapplication.shared.generated.resources.ic_reports
import myapplication.shared.generated.resources.ic_settings
import myapplication.shared.generated.resources.tab_budgets
import myapplication.shared.generated.resources.tab_expenses
import myapplication.shared.generated.resources.tab_home
import myapplication.shared.generated.resources.tab_reports
import myapplication.shared.generated.resources.tab_settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppNavigationScaffold(
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val tabs = rememberBaseBottomTabs()
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
            modifier = Modifier.padding(paddingValues),
            themeMode = themeMode,
            onThemeModeChange = onThemeModeChange,
        )
    }
}

@Composable
private fun rememberBaseBottomTabs(): List<AppBottomTab> {
    return listOf(
        AppBottomTab(
            route = AppRoute.Home.route,
            label = stringResource(Res.string.tab_home),
            icon = Res.drawable.ic_home,
        ),
        AppBottomTab(
            route = AppRoute.Expenses.route,
            label = stringResource(Res.string.tab_expenses),
            icon = Res.drawable.ic_expenses,
        ),
        AppBottomTab(
            route = AppRoute.Reports.route,
            label = stringResource(Res.string.tab_reports),
            icon = Res.drawable.ic_reports,
        ),
        AppBottomTab(
            route = AppRoute.Budgets.route,
            label = stringResource(Res.string.tab_budgets),
            icon = Res.drawable.ic_budget,
        ),
        AppBottomTab(
            route = AppRoute.Settings.route,
            label = stringResource(Res.string.tab_settings),
            icon = Res.drawable.ic_settings,
        ),
    )
}
