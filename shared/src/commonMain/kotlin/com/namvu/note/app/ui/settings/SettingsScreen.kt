package com.namvu.note.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.namvu.note.app.ui.base.component.list.AppListItem
import com.namvu.note.app.ui.base.component.surface.AppCard
import com.namvu.note.app.ui.base.theme.AppThemeMode
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        AppCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                SingleChoiceSegmentedButtonRow {
                    AppThemeMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = themeMode == mode,
                            onClick = { onThemeModeChange(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = AppThemeMode.entries.size,
                            ),
                        ) {
                            Text(text = mode.name)
                        }
                    }
                }
            }
        }
        AppCard {
            AppListItem(
                title = "Google Sheets",
                subtitle = "Expense changes stay local first, then sync through the pending queue when a spreadsheet is connected.",
            )
            AppListItem(
                title = "Google Account",
                subtitle = "Connected account, spreadsheet, sync status",
            )
            AppListItem(
                title = "Currency",
                subtitle = "Vietnamese dong (VND)",
            )
            AppListItem(
                title = "Language",
                subtitle = "English, Vietnamese",
            )
            AppListItem(
                title = "Notifications",
                subtitle = "Budget alerts and sync reminders",
            )
            AppListItem(
                title = "Privacy",
                subtitle = "Local-first data, user-owned Sheets backup",
            )
            AppListItem(
                title = "Backup",
                subtitle = "Manual sync and restore foundation",
            )
            AppListItem(
                title = "About",
                subtitle = "Expense Journal",
            )
        }
    }
}
