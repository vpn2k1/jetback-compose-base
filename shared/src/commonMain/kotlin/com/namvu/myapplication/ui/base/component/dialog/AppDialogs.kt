package com.namvu.myapplication.ui.base.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.namvu.myapplication.ui.base.component.button.AppButton
import com.namvu.myapplication.ui.base.component.button.AppDestructiveButton
import com.namvu.myapplication.ui.base.component.button.AppTextButton
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_cancel
import myapplication.shared.generated.resources.common_confirm
import myapplication.shared.generated.resources.common_delete
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppConfirmDialog(
    title: String,
    message: String,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    confirmText: String = stringResource(Res.string.common_confirm),
    dismissText: String = stringResource(Res.string.common_cancel),
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            AppButton(
                text = confirmText,
                onClick = onConfirmClick,
            )
        },
        dismissButton = {
            AppTextButton(
                text = dismissText,
                onClick = onDismissRequest,
            )
        },
    )
}

@Composable
fun AppDestructiveConfirmDialog(
    title: String,
    message: String,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    confirmText: String = stringResource(Res.string.common_delete),
    dismissText: String = stringResource(Res.string.common_cancel),
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            AppDestructiveButton(
                text = confirmText,
                onClick = onConfirmClick,
            )
        },
        dismissButton = {
            AppTextButton(
                text = dismissText,
                onClick = onDismissRequest,
            )
        },
    )
}
