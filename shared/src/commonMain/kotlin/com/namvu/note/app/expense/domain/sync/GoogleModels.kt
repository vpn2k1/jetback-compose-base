package com.namvu.note.app.expense.domain.sync

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

enum class GoogleAuthorizationScope(val uri: String) {
    DriveFile("https://www.googleapis.com/auth/drive.file"),
}

data class GoogleAccessGrant(
    val scopes: Set<GoogleAuthorizationScope> = emptySet(),
) {
    val hasSheetsAccess: Boolean
        get() = GoogleAuthorizationScope.DriveFile in scopes
}

data class GoogleSheetRow(
    val expenseId: String,
    val monthKey: String,
    val values: List<String>,
)

data class GoogleSyncSession(
    val account: GoogleAccount?,
    val spreadsheet: GoogleSpreadsheet?,
    val accessGrant: GoogleAccessGrant = GoogleAccessGrant(),
) {
    val isSignedIn: Boolean
        get() = account != null

    val hasSpreadsheet: Boolean
        get() = spreadsheet != null

    val hasSheetsAccess: Boolean
        get() = accessGrant.hasSheetsAccess
}

data class SyncSummary(
    val attempted: Int,
    val uploaded: Int,
    val failed: Int,
    val spreadsheet: GoogleSpreadsheet?,
)
