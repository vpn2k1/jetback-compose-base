package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.namvu.myapplication.navigation.AppNavigationScaffold
import com.namvu.myapplication.ui.base.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        AppNavigationScaffold()
    }
}
