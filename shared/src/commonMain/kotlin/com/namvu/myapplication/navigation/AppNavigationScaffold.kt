package com.namvu.myapplication.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.ic_home
import myapplication.shared.generated.resources.ic_settings
import myapplication.shared.generated.resources.tab_home
import myapplication.shared.generated.resources.tab_settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppNavigationScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val tabs = rememberBaseBottomTabs()
    val selectedRoute = tabs.firstOrNull { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }?.route

    Scaffold(
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
            route = AppRoute.Settings.route,
            label = stringResource(Res.string.tab_settings),
            icon = Res.drawable.ic_settings,
        ),
    )
}
