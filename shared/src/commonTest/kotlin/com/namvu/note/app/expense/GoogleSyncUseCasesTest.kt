package com.namvu.note.app.expense

import com.namvu.note.app.expense.data.google.InMemoryGoogleEcosystemService
import com.namvu.note.app.expense.data.local.InMemoryLocalExpenseDatabase
import com.namvu.note.app.expense.data.repository.LocalExpenseRepository
import com.namvu.note.app.expense.domain.model.ExpenseDraft
import com.namvu.note.app.expense.domain.model.ExpenseSyncStatus
import com.namvu.note.app.expense.domain.sync.ExpenseSheetMapper
import com.namvu.note.app.expense.domain.usecase.GoogleSyncUseCases
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GoogleSyncUseCasesTest {
    @Test
    fun syncCreatesSpreadsheetAndMarksPendingExpensesSynced() = runSuspending {
        val repository = LocalExpenseRepository(
            database = InMemoryLocalExpenseDatabase(),
            clock = { 1_704_067_200_000L },
            idGenerator = { "expense-1" },
        )
        val sync = GoogleSyncUseCases(
            repository = repository,
            googleService = InMemoryGoogleEcosystemService(),
            clock = { 1_704_067_300_000L },
        )

        sync.signIn()
        repository.addExpense(
            ExpenseDraft(
                title = "Coffee",
                amountMinor = 4_500_000L,
                categoryId = "drinks",
            ),
        )

        val summary = sync.syncPendingExpenses()
        val expense = repository.getExpenses().first()

        assertEquals(1, summary.attempted)
        assertEquals(1, summary.uploaded)
        assertEquals(0, summary.failed)
        assertEquals("Expense Journal", summary.spreadsheet?.name)
        assertEquals(ExpenseSyncStatus.Synced, expense.syncStatus)
        assertEquals(1_704_067_300_000L, expense.syncedAtMillis)
    }

    @Test
    fun retryFailedSyncResetsFailedItemsAndUploadsAgain() = runSuspending {
        val repository = LocalExpenseRepository(
            database = InMemoryLocalExpenseDatabase(),
            clock = { 1_704_067_200_000L },
            idGenerator = { "expense-1" },
        )
        val service = InMemoryGoogleEcosystemService(
            shouldFailUpload = { row -> row.expenseId == "expense-1" },
        )
        val failingSync = GoogleSyncUseCases(repository, service)

        failingSync.signIn()
        repository.addExpense(
            ExpenseDraft(
                title = "Lunch",
                amountMinor = 12_000_000L,
                categoryId = "food",
            ),
        )

        val failedSummary = failingSync.syncPendingExpenses()
        assertEquals(1, failedSummary.failed)
        assertEquals(ExpenseSyncStatus.Failed, repository.getExpenses().first().syncStatus)

        val retrySync = GoogleSyncUseCases(
            repository = repository,
            googleService = InMemoryGoogleEcosystemService(),
            clock = { 1_704_067_400_000L },
        )
        retrySync.signIn()

        val retrySummary = retrySync.retryFailedSync()

        assertEquals(1, retrySummary.uploaded)
        assertEquals(ExpenseSyncStatus.Synced, repository.getExpenses().first().syncStatus)
    }

    @Test
    fun mapperCreatesMonthlySheetKeyFromExpenseDate() {
        val monthKey = ExpenseSheetMapper.monthKeyFor(1_704_067_200_000L)

        assertEquals("2024-01", monthKey)
        assertNotNull(monthKey)
    }
}
