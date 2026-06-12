package com.namvu.note.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.namvu.note.app.expense.domain.sync.GoogleSyncSession
import com.namvu.note.app.ui.base.component.button.AppDestructiveButton
import com.namvu.note.app.ui.base.component.list.AppListItem
import com.namvu.note.app.ui.base.component.surface.AppCard
import com.namvu.note.app.ui.base.localization.AppLanguage
import com.namvu.note.app.ui.base.localization.AppText
import com.namvu.note.app.ui.base.localization.text
import com.namvu.note.app.ui.base.theme.AppThemeMode

@Composable
fun SettingsScreen(
    googleSession: GoogleSyncSession,
    isGoogleActionRunning: Boolean,
    googleErrorMessage: String?,
    onGoogleSignOutClick: () -> Unit,
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    appLanguage: AppLanguage,
    onAppLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = appLanguage.text(AppText.SettingsTitle),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        AppCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = appLanguage.text(AppText.Theme),
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
                            Text(text = appLanguage.themeText(mode))
                        }
                    }
                }
            }
        }
        AppCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = appLanguage.text(AppText.Language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                SingleChoiceSegmentedButtonRow {
                    AppLanguage.entries.forEachIndexed { index, language ->
                        SegmentedButton(
                            selected = appLanguage == language,
                            onClick = { onAppLanguageChange(language) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = AppLanguage.entries.size,
                            ),
                        ) {
                            Text(text = language.label)
                        }
                    }
                }
            }
        }
        AppCard {
            val account = googleSession.account
            AppListItem(
                title = appLanguage.text(AppText.GoogleSheets),
                subtitle = googleSession.spreadsheet?.let { spreadsheet ->
                    appLanguage.text(AppText.GoogleSheetsConnected).format(spreadsheet.name)
                } ?: appLanguage.text(AppText.GoogleSheetsLocalFirst),
            )
            if (account == null) {
                AppListItem(
                    title = appLanguage.text(AppText.GoogleAccount),
                    subtitle = appLanguage.text(AppText.GoogleNoAccount),
                )
            } else {
                AppListItem(
                    title = account.displayName,
                    subtitle = account.email,
                    overline = appLanguage.text(AppText.GoogleAccountOverline),
                )
                AppDestructiveButton(
                    text = appLanguage.text(AppText.SignOut),
                    onClick = onGoogleSignOutClick,
                    isLoading = isGoogleActionRunning,
                    fullWidth = true,
                )
            }
            if (googleErrorMessage != null) {
                Text(
                    text = googleErrorMessage,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            AppListItem(
                title = appLanguage.text(AppText.Currency),
                subtitle = appLanguage.text(AppText.CurrencyVnd),
            )
            AppListItem(
                title = appLanguage.text(AppText.Notifications),
                subtitle = appLanguage.text(AppText.NotificationsSubtitle),
            )
            AppListItem(
                title = appLanguage.text(AppText.Privacy),
                subtitle = appLanguage.text(AppText.PrivacySubtitle),
            )
            AppListItem(
                title = appLanguage.text(AppText.Backup),
                subtitle = appLanguage.text(AppText.BackupSubtitle),
            )
            AppListItem(
                title = appLanguage.text(AppText.About),
                subtitle = appLanguage.text(AppText.AboutSubtitle),
            )
        }
    }
}

private fun AppLanguage.themeText(mode: AppThemeMode): String {
    return when (mode) {
        AppThemeMode.System -> text(AppText.System)
        AppThemeMode.Light -> text(AppText.Light)
        AppThemeMode.Dark -> text(AppText.Dark)
    }
}
