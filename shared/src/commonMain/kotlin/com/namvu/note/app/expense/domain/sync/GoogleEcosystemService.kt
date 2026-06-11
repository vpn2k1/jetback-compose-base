package com.namvu.note.app.expense.domain.sync

interface GoogleEcosystemService {
    suspend fun getSession(): GoogleSyncSession
    suspend fun signIn(): GoogleAccount
    suspend fun signOut()
    suspend fun findSpreadsheet(name: String): GoogleSpreadsheet?
    suspend fun createSpreadsheet(name: String): GoogleSpreadsheet
    suspend fun selectSpreadsheet(spreadsheetId: String): GoogleSpreadsheet
    suspend fun ensureMonthlySheet(spreadsheetId: String, monthKey: String)
    suspend fun appendExpenseRows(spreadsheetId: String, monthKey: String, rows: List<GoogleSheetRow>)
}
