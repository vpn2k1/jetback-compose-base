package com.namvu.note.app.ui.base.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.namvu.note.app.ui.base.theme.AppThemeDefaults

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fullWidth: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .defaultMinSize(minHeight = AppThemeDefaults.dimens.buttonHeight),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(AppThemeDefaults.dimens.buttonRadius),
    ) {
        AppButtonContent(text = text, isLoading = isLoading)
    }
}

@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fullWidth: Boolean = false,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .defaultMinSize(minHeight = AppThemeDefaults.dimens.buttonHeight),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(AppThemeDefaults.dimens.buttonRadius),
    ) {
        AppButtonContent(text = text, isLoading = isLoading)
    }
}

@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = AppThemeDefaults.dimens.compactButtonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(AppThemeDefaults.dimens.compactButtonRadius),
    ) {
        Text(text = text)
    }
}

@Composable
fun AppDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fullWidth: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .defaultMinSize(minHeight = AppThemeDefaults.dimens.buttonHeight),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(AppThemeDefaults.dimens.buttonRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
        ),
    ) {
        AppButtonContent(text = text, isLoading = isLoading)
    }
}

@Composable
private fun AppButtonContent(
    text: String,
    isLoading: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppThemeDefaults.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Text(text = text)
    }
}
