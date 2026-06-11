package com.namvu.note.app.ui.home.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.namvu.note.app.expense.domain.model.ExpenseSyncStatus

@Composable
internal fun HomeScreenSyncStatusItem(
    status: ExpenseSyncStatus,
    modifier: Modifier = Modifier,
    text: String = status.name,
) {
    val color = when (status) {
        ExpenseSyncStatus.Pending -> MaterialTheme.colorScheme.tertiary
        ExpenseSyncStatus.Synced -> MaterialTheme.colorScheme.secondary
        ExpenseSyncStatus.Failed -> MaterialTheme.colorScheme.error
    }
    Text(
        text = text,
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.SemiBold,
    )
}
