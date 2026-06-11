package com.namvu.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.namvu.myapplication.ui.base.component.button.AppButton
import com.namvu.myapplication.ui.base.component.button.AppOutlinedButton
import com.namvu.myapplication.ui.base.component.feedback.EmptyContent
import com.namvu.myapplication.ui.base.component.layout.AppScaffold
import com.namvu.myapplication.ui.base.component.surface.AppCard
import com.namvu.myapplication.ui.base.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        AppScaffold(title = "Base KMP App") { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AppCard {
                    Text(
                        text = "Shared UI foundation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Theme, strings, buttons, inputs, dialogs, cards, lists, and state UI are ready for new screens.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                AppButton(
                    text = "Primary action",
                    onClick = {},
                    fullWidth = true,
                )

                AppOutlinedButton(
                    text = "Secondary action",
                    onClick = {},
                    fullWidth = true,
                )

                EmptyContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    title = "Ready for your first feature",
                    message = "Use the base UI package to build production screens with shared theme, components, and state handling.",
                )
            }
        }
    }
}
