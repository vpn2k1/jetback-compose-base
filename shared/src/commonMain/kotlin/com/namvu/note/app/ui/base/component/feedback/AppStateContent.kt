package com.namvu.note.app.ui.base.component.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.namvu.note.app.ui.base.component.button.AppButton
import com.namvu.note.app.ui.base.model.UiState
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.common_coming_soon
import myapplication.shared.generated.resources.common_empty
import myapplication.shared.generated.resources.common_error
import myapplication.shared.generated.resources.common_loading
import myapplication.shared.generated.resources.common_no_data
import myapplication.shared.generated.resources.common_permission_denied
import myapplication.shared.generated.resources.common_retry
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T> AppStateContent(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    onRetryClick: (() -> Unit)? = null,
    content: @Composable (T) -> Unit,
) {
    when (state) {
        UiState.Idle -> Box(modifier = modifier)
        UiState.Loading -> LoadingContent(modifier = modifier)
        is UiState.Empty -> EmptyContent(
            message = state.message ?: stringResource(Res.string.common_empty),
            modifier = modifier,
        )
        is UiState.Error -> ErrorContent(
            message = state.message,
            modifier = modifier,
            onRetryClick = if (state.canRetry) onRetryClick else null,
        )
        is UiState.Success -> content(state.data)
    }
}

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier,
    message: String = stringResource(Res.string.common_loading),
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator()
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun EmptyContent(
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.common_empty),
    message: String = stringResource(Res.string.common_no_data),
) {
    MessageContent(
        title = title,
        message = message,
        modifier = modifier,
    )
}

@Composable
fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.common_error),
    onRetryClick: (() -> Unit)? = null,
) {
    MessageContent(
        title = title,
        message = message,
        modifier = modifier,
        actionText = onRetryClick?.let { stringResource(Res.string.common_retry) },
        onActionClick = onRetryClick,
    )
}

@Composable
fun PermissionDeniedContent(
    modifier: Modifier = Modifier,
    message: String = stringResource(Res.string.common_permission_denied),
) {
    MessageContent(
        title = stringResource(Res.string.common_permission_denied),
        message = message,
        modifier = modifier,
    )
}

@Composable
fun ComingSoonContent(
    modifier: Modifier = Modifier,
    message: String = stringResource(Res.string.common_coming_soon),
) {
    MessageContent(
        title = stringResource(Res.string.common_coming_soon),
        message = message,
        modifier = modifier,
    )
}

@Composable
private fun MessageContent(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (actionText != null && onActionClick != null) {
                AppButton(
                    text = actionText,
                    onClick = onActionClick,
                )
            }
        }
    }
}
