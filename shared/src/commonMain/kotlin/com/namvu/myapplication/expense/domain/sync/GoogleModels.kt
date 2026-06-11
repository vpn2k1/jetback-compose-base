package com.namvu.myapplication.expense.domain.sync

data class GoogleAccount(
    val id: String,
    val displayName: String,
    val email: String,
)

data class GoogleSpreadsheet(
    val id: String,
    val name: String,
    val webUrl: String,
)

data class GoogleSheetRow(
    val expenseId: String,
    val monthKey: String,
    val values: List<String>,
)

data class GoogleSyncSession(
    val account: GoogleAccount?,
    val spreadsheet: GoogleSpreadsheet?,
) {
    val isSignedIn: Boolean
        get() = account != null

    val hasSpreadsheet: Boolean
        get() = spreadsheet != null
}

data class SyncSummary(
    val attempted: Int,
    val uploaded: Int,
    val failed: Int,
    val spreadsheet: GoogleSpreadsheet?,
)
