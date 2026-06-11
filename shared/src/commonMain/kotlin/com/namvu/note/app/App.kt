package com.namvu.note.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.namvu.note.app.navigation.AppNavigationScaffold
import com.namvu.note.app.ui.base.theme.AppTheme
import com.namvu.note.app.ui.base.theme.AppThemeMode

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
