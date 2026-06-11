package com.namvu.myapplication.expense

import com.namvu.myapplication.expense.data.local.InMemoryLocalExpenseDatabase
import com.namvu.myapplication.expense.data.repository.LocalExpenseRepository
import com.namvu.myapplication.expense.domain.model.ExpenseDraft
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalExpenseRepositoryTest {
    @Test
    fun addsUpdatesAndDeletesExpense() = runSuspending {
        val repository = LocalExpenseRepository(
            database = InMemoryLocalExpenseDatabase(),
            clock = { 1_000L },
            idGenerator = { "expense-1" },
        )

        repository.addExpense(
            ExpenseDraft(
                title = "Coffee",
                amountMinor = 4_500_000L,
                categoryId = "drinks",
            ),
        )

        assertEquals(1, repository.getExpenses().size)
        assertEquals("Coffee", repository.getExpenses().first().title)

        repository.updateExpense(
            id = "expense-1",
            draft = ExpenseDraft(
                title = "Lunch",
                amountMinor = 12_000_000L,
                categoryId = "food",
            ),
        )

        assertEquals("Lunch", repository.getExpenses().first().title)

        repository.deleteExpense("expense-1")

        assertEquals(emptyList(), repository.getExpenses())
    }
}

internal fun runSuspending(block: suspend () -> Unit) {
    var result: Result<Unit>? = null
    block.startCoroutine(
        Continuation(EmptyCoroutineContext) { continuationResult ->
            result = continuationResult
        },
    )
    result?.getOrThrow()
}
