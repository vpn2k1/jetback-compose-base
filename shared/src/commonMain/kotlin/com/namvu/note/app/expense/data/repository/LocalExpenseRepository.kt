package com.namvu.note.app.expense.data.repository

import com.namvu.note.app.expense.data.local.ExpenseRecord
import com.namvu.note.app.expense.data.local.LocalExpenseDatabase
import com.namvu.note.app.expense.domain.model.Category
import com.namvu.note.app.expense.domain.model.DefaultCategories
import com.namvu.note.app.expense.domain.model.Expense
import com.namvu.note.app.expense.domain.model.ExpenseDraft
import com.namvu.note.app.expense.domain.model.ExpenseSyncStatus
import com.namvu.note.app.expense.domain.repository.ExpenseRepository
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class LocalExpenseRepository(
    private val database: LocalExpenseDatabase,
    private val clock: () -> Long = currentTimeMillisProvider(),
    private val idGenerator: () -> String = { "expense-${Random.nextLong().toString().replace("-", "")}" },
) : ExpenseRepository {
    override suspend fun getCategories(): List<Category> {
        return DefaultCategories.items
    }

    override suspend fun getExpenses(): List<Expense> {
        return database.getExpenses().map { it.toDomain() }
    }

    override suspend fun getPendingSyncExpenses(): List<Expense> {
        return getExpenses().filter { it.syncStatus != ExpenseSyncStatus.Synced }
    }

    override suspend fun addExpense(draft: ExpenseDraft): Expense {
        val now = clock()
        val expense = Expense(
            id = idGenerator(),
            title = draft.title.trim(),
            amountMinor = draft.amountMinor,
            categoryId = draft.categoryId,
            note = draft.note.trim(),
            createdAtMillis = draft.createdAtMillis ?: now,
            updatedAtMillis = now,
            syncStatus = ExpenseSyncStatus.Pending,
        )
        database.upsertExpense(expense.toRecord())
        return expense
    }

    override suspend fun updateExpense(id: String, draft: ExpenseDraft): Expense {
        val existing = requireNotNull(database.getExpense(id)) { "Expense not found" }
        val expense = existing.toDomain().copy(
            title = draft.title.trim(),
            amountMinor = draft.amountMinor,
            categoryId = draft.categoryId,
            note = draft.note.trim(),
            updatedAtMillis = clock(),
            syncStatus = ExpenseSyncStatus.Pending,
            syncedAtMillis = null,
            syncError = null,
        )
        database.upsertExpense(expense.toRecord())
        return expense
    }

    override suspend fun deleteExpense(id: String) {
        database.deleteExpense(id)
    }

    override suspend fun markExpenseSynced(id: String, syncedAtMillis: Long) {
        val expense = database.getExpense(id) ?: return
        database.upsertExpense(
            expense.copy(
                syncStatus = ExpenseSyncStatus.Synced.name,
                syncedAtMillis = syncedAtMillis,
                syncError = null,
            ),
        )
    }

    override suspend fun markExpenseSyncFailed(id: String, message: String) {
        val expense = database.getExpense(id) ?: return
        database.upsertExpense(
            expense.copy(
                syncStatus = ExpenseSyncStatus.Failed.name,
                syncError = message,
                retryCount = expense.retryCount + 1,
            ),
        )
    }

    override suspend fun resetFailedExpenseSync() {
        database.getExpenses()
            .filter { it.syncStatus == ExpenseSyncStatus.Failed.name }
            .forEach { expense ->
                database.upsertExpense(
                    expense.copy(
                        syncStatus = ExpenseSyncStatus.Pending.name,
                        syncError = null,
                    ),
                )
            }
    }
}

private fun ExpenseRecord.toDomain(): Expense {
    return Expense(
        id = id,
        title = title,
        amountMinor = amountMinor,
        categoryId = categoryId,
        note = note,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis,
        syncStatus = runCatching { ExpenseSyncStatus.valueOf(syncStatus) }
            .getOrDefault(ExpenseSyncStatus.Pending),
        syncedAtMillis = syncedAtMillis,
        syncError = syncError,
        retryCount = retryCount,
    )
}

private fun Expense.toRecord(): ExpenseRecord {
    return ExpenseRecord(
        id = id,
        title = title,
        amountMinor = amountMinor,
        categoryId = categoryId,
        note = note,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis,
        syncStatus = syncStatus.name,
        syncedAtMillis = syncedAtMillis,
        syncError = syncError,
        retryCount = retryCount,
    )
}

@OptIn(ExperimentalTime::class)
private fun currentTimeMillisProvider(): () -> Long = {
    Clock.System.now().toEpochMilliseconds()
}
