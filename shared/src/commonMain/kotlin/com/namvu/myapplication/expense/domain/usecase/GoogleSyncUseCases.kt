package com.namvu.myapplication.expense.domain.usecase

import com.namvu.myapplication.expense.domain.repository.ExpenseRepository
import com.namvu.myapplication.expense.domain.sync.ExpenseSheetMapper
import com.namvu.myapplication.expense.domain.sync.GoogleEcosystemService
import com.namvu.myapplication.expense.domain.sync.GoogleSpreadsheet
import com.namvu.myapplication.expense.domain.sync.GoogleSyncSession
import com.namvu.myapplication.expense.domain.sync.SyncSummary
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class GoogleSyncUseCases(
    private val repository: ExpenseRepository,
    private val googleService: GoogleEcosystemService,
    private val clock: () -> Long = currentTimeMillisProvider(),
) {
    suspend fun loadSession(): GoogleSyncSession {
        return googleService.getSession()
    }

    suspend fun signIn(): GoogleSyncSession {
        googleService.signIn()
        return googleService.getSession()
    }

    suspend fun signOut(): GoogleSyncSession {
        googleService.signOut()
        return googleService.getSession()
    }

    suspend fun connectExpenseJournalSpreadsheet(): GoogleSpreadsheet {
        val existing = googleService.findSpreadsheet(EXPENSE_JOURNAL_SPREADSHEET_NAME)
        return if (existing != null) {
            googleService.selectSpreadsheet(existing.id)
        } else {
            googleService.createSpreadsheet(EXPENSE_JOURNAL_SPREADSHEET_NAME)
        }
    }

    suspend fun syncPendingExpenses(): SyncSummary {
        val session = googleService.getSession()
        require(session.isSignedIn) { "Sign in with Google before syncing" }

        val spreadsheet = session.spreadsheet ?: connectExpenseJournalSpreadsheet()
        val categories = repository.getCategories().associateBy { it.id }
        val pendingExpenses = repository.getPendingSyncExpenses()
        var uploaded = 0
        var failed = 0

        pendingExpenses
            .groupBy { ExpenseSheetMapper.monthKeyFor(it.createdAtMillis) }
            .forEach { (monthKey, expenses) ->
                googleService.ensureMonthlySheet(spreadsheet.id, monthKey)
                expenses.forEach { expense ->
                    val row = ExpenseSheetMapper.toRow(expense, categories[expense.categoryId])
                    runCatching {
                        googleService.appendExpenseRows(
                            spreadsheetId = spreadsheet.id,
                            monthKey = monthKey,
                            rows = listOf(row),
                        )
                    }
                        .onSuccess {
                            repository.markExpenseSynced(expense.id, clock())
                            uploaded++
                        }
                        .onFailure { throwable ->
                            repository.markExpenseSyncFailed(
                                id = expense.id,
                                message = throwable.message ?: "Google Sheets upload failed",
                            )
                            failed++
                        }
                }
            }

        return SyncSummary(
            attempted = pendingExpenses.size,
            uploaded = uploaded,
            failed = failed,
            spreadsheet = spreadsheet,
        )
    }

    suspend fun retryFailedSync(): SyncSummary {
        repository.resetFailedExpenseSync()
        return syncPendingExpenses()
    }

    private companion object {
        const val EXPENSE_JOURNAL_SPREADSHEET_NAME = "Expense Journal"
    }
}

@OptIn(ExperimentalTime::class)
private fun currentTimeMillisProvider(): () -> Long = {
    Clock.System.now().toEpochMilliseconds()
}
