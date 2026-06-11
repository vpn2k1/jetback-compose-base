package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.namvu.myapplication.navigation.AppNavigationScaffold
import com.namvu.myapplication.ui.base.theme.AppTheme
import com.namvu.myapplication.ui.base.theme.AppThemeMode

@Composable
@Preview
fun App() {
    var themeMode by remember { mutableStateOf(AppThemeMode.System) }

    AppTheme(themeMode = themeMode) {
        AppNavigationScaffold(
            themeMode = themeMode,
            onThemeModeChange = { themeMode = it },
        )
    }
}
