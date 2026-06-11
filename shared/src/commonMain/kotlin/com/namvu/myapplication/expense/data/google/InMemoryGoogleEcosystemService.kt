package com.namvu.myapplication.expense.data.google

import com.namvu.myapplication.expense.domain.sync.GoogleAccount
import com.namvu.myapplication.expense.domain.sync.GoogleEcosystemService
import com.namvu.myapplication.expense.domain.sync.GoogleSheetRow
import com.namvu.myapplication.expense.domain.sync.GoogleSpreadsheet
import com.namvu.myapplication.expense.domain.sync.GoogleSyncSession

class InMemoryGoogleEcosystemService(
    private val shouldFailUpload: (GoogleSheetRow) -> Boolean = { false },
) : GoogleEcosystemService {
    private var account: GoogleAccount? = null
    private var spreadsheet: GoogleSpreadsheet? = null
    private val spreadsheets = mutableMapOf<String, GoogleSpreadsheet>()
    private val sheets = mutableMapOf<String, MutableSet<String>>()
    private val rows = mutableMapOf<String, MutableList<GoogleSheetRow>>()

    override suspend fun getSession(): GoogleSyncSession {
        return GoogleSyncSession(account = account, spreadsheet = spreadsheet)
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
    }

    override suspend fun findSpreadsheet(name: String): GoogleSpreadsheet? {
        return spreadsheets.values.firstOrNull { it.name == name }
    }

    override suspend fun createSpreadsheet(name: String): GoogleSpreadsheet {
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
        val selected = requireNotNull(spreadsheets[spreadsheetId]) { "Spreadsheet not found" }
        spreadsheet = selected
        return selected
    }

    override suspend fun ensureMonthlySheet(spreadsheetId: String, monthKey: String) {
        sheets.getOrPut(spreadsheetId) { mutableSetOf() }.add(monthKey)
    }

    override suspend fun appendExpenseRows(
        spreadsheetId: String,
        monthKey: String,
        rows: List<GoogleSheetRow>,
    ) {
        rows.firstOrNull(shouldFailUpload)?.let { failedRow ->
            error("Could not upload expense ${failedRow.expenseId}")
        }
        this.rows.getOrPut("$spreadsheetId:$monthKey") { mutableListOf() }.addAll(rows)
    }
}
