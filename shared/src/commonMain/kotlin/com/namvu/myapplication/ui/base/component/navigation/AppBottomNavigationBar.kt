package com.namvu.myapplication.ui.base.component.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

data class AppBottomTab(
    val route: String,
    val label: String,
    val icon: DrawableResource,
)

@Composable
fun AppBottomNavigationBar(
    tabs: List<AppBottomTab>,
    selectedRoute: String?,
    onTabSelected: (AppBottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = selectedRoute == tab.route,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        painter = painterResource(tab.icon),
                        contentDescription = tab.label,
                    )
                },
                label = { Text(text = tab.label) },
            )
        }
    }
}
