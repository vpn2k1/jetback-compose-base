package com.namvu.myapplication.expense

import com.namvu.myapplication.expense.domain.model.QuickExpenseParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuickExpenseParserTest {
    @Test
    fun parsesCompactThousandAmount() {
        val parsed = QuickExpenseParser.parse(
            input = "Coffee 45k",
            fallbackCategoryId = "other",
        )

        assertEquals("Coffee", parsed.title)
        assertEquals(4_500_000L, parsed.amountMinor)
        assertEquals("drinks", parsed.categoryId)
        assertFalse(parsed.validationError.hasError)
    }

    @Test
    fun parsesMillionAmount() {
        val parsed = QuickExpenseParser.parse(
            input = "Electricity 1.2m",
            fallbackCategoryId = "other",
        )

        assertEquals("Electricity", parsed.title)
        assertEquals(120_000_000L, parsed.amountMinor)
        assertEquals("bills", parsed.categoryId)
    }

    @Test
    fun reportsMissingAmount() {
        val parsed = QuickExpenseParser.parse(
            input = "Lunch",
            fallbackCategoryId = "food",
        )

        assertEquals("Lunch", parsed.title)
        assertEquals(null, parsed.amountMinor)
        assertTrue(parsed.validationError.hasError)
    }

    @Test
    fun parsesAmountFirstNaturalText() {
        val parsed = QuickExpenseParser.parse(
            input = "45k for coffee",
            fallbackCategoryId = "other",
        )

        assertEquals("coffee", parsed.title)
        assertEquals(4_500_000L, parsed.amountMinor)
        assertEquals("drinks", parsed.categoryId)
    }

    @Test
    fun parsesCategoryHint() {
        val parsed = QuickExpenseParser.parse(
            input = "Parking 20k #transport",
            fallbackCategoryId = "other",
        )

        assertEquals("Parking", parsed.title)
        assertEquals("transport", parsed.categoryId)
    }

    @Test
    fun parsesYesterdayDateHint() {
        val now = 1_704_153_600_000L
        val parsed = QuickExpenseParser.parse(
            input = "Lunch 120k yesterday",
            fallbackCategoryId = "other",
            nowMillis = now,
        )

        assertEquals(now - 86_400_000L, parsed.createdAtMillis)
    }

    @Test
    fun parsesCurrencySuffixes() {
        assertEquals(4_500_000L, QuickExpenseParser.parseAmountMinor("45k₫"))
        assertEquals(120_000_000L, QuickExpenseParser.parseAmountMinor("1.2tr"))
        assertEquals(300_000_000L, QuickExpenseParser.parseAmountMinor("3000000vnd"))
    }
}
