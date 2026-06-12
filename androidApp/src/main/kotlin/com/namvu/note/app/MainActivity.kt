package com.namvu.note.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.namvu.note.app.expense.ExpenseJournalGraph
import com.namvu.note.app.google.AndroidGoogleEcosystemService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ExpenseJournalGraph.configureGoogleService(
            AndroidGoogleEcosystemService(
                context = this,
                serverClientId = getString(R.string.google_server_client_id),
            ),
        )

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
