package com.namvu.note.app.expense.data.google

import com.namvu.note.app.expense.domain.sync.GoogleAccount
import com.namvu.note.app.expense.domain.sync.GoogleAccessGrant
import com.namvu.note.app.expense.domain.sync.GoogleAuthorizationScope
import com.namvu.note.app.expense.domain.sync.GoogleEcosystemService
import com.namvu.note.app.expense.domain.sync.GoogleSheetRow
import com.namvu.note.app.expense.domain.sync.GoogleSpreadsheet
import com.namvu.note.app.expense.domain.sync.GoogleSyncSession

class InMemoryGoogleEcosystemService(
    private val shouldFailUpload: (GoogleSheetRow) -> Boolean = { false },
) : GoogleEcosystemService {
    private var account: GoogleAccount? = null
    private var spreadsheet: GoogleSpreadsheet? = null
    private var accessGrant = GoogleAccessGrant()
    private val spreadsheets = mutableMapOf<String, GoogleSpreadsheet>()
    private val sheets = mutableMapOf<String, MutableSet<String>>()
    private val rows = mutableMapOf<String, MutableList<GoogleSheetRow>>()

    override suspend fun getSession(): GoogleSyncSession {
        return GoogleSyncSession(
            account = account,
            spreadsheet = spreadsheet,
            accessGrant = accessGrant,
        )
    }

    override suspend fun signIn(): GoogleAccount {
        return GoogleAccount(
            id = "sandbox-google-account",
            displayName = "Google Account",
            email = "user@example.com",
        ).also { account = it }
    }

    override suspend fun signOut() {
        account = null
        spreadsheet = null
        accessGrant = GoogleAccessGrant()
    }

    override suspend fun disconnect() {
        signOut()
        spreadsheets.clear()
        sheets.clear()
        rows.clear()
    }

    override suspend fun requestSheetsAccess(): GoogleAccessGrant {
        requireSignedIn()
        return GoogleAccessGrant(setOf(GoogleAuthorizationScope.DriveFile))
            .also { accessGrant = it }
    }

    override suspend fun findSpreadsheet(name: String): GoogleSpreadsheet? {
        requireSheetsAccess()
        return spreadsheets.values.firstOrNull { it.name == name }
    }

    override suspend fun createSpreadsheet(name: String): GoogleSpreadsheet {
        requireSheetsAccess()
        val created = GoogleSpreadsheet(
            id = "spreadsheet-${spreadsheets.size + 1}",
            name = name,
            webUrl = "https://docs.google.com/spreadsheets/d/spreadsheet-${spreadsheets.size + 1}",
        )
        spreadsheets[created.id] = created
        spreadsheet = created
        return created
    }

    override suspend fun selectSpreadsheet(spreadsheetId: String): GoogleSpreadsheet {
        requireSheetsAccess()
        val selected = requireNotNull(spreadsheets[spreadsheetId]) { "Spreadsheet not found" }
        spreadsheet = selected
        return selected
    }

    override suspend fun ensureMonthlySheet(spreadsheetId: String, monthKey: String) {
        requireSheetsAccess()
        sheets.getOrPut(spreadsheetId) { mutableSetOf() }.add(monthKey)
    }

    override suspend fun appendExpenseRows(
        spreadsheetId: String,
        monthKey: String,
        rows: List<GoogleSheetRow>,
    ) {
        requireSheetsAccess()
        rows.firstOrNull(shouldFailUpload)?.let { failedRow ->
            error("Could not upload expense ${failedRow.expenseId}")
        }
        this.rows.getOrPut("$spreadsheetId:$monthKey") { mutableListOf() }.addAll(rows)
    }

    private fun requireSignedIn() {
        require(account != null) { "Sign in with Google before syncing" }
    }

    private fun requireSheetsAccess() {
        requireSignedIn()
        require(accessGrant.hasSheetsAccess) { "Allow Google Sheets access before syncing" }
    }
}
