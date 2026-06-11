package com.namvu.note.app.expense.domain.model

object QuickExpenseParser {
    fun parse(
        input: String,
        fallbackCategoryId: String,
        nowMillis: Long? = null,
    ): ParsedExpenseInput {
        val trimmed = input.trim()
        if (trimmed.isBlank()) {
            return ParsedExpenseInput(
                title = "",
                amountMinor = null,
                validationError = ExpenseValidationError(
                    titleError = "Add a description",
                    amountError = "Add an amount",
                ),
            )
        }

        val rawParts = trimmed.split(Regex("\\s+")).filter { it.isNotBlank() }
        val categoryHint = rawParts.firstNotNullOfOrNull { categoryFromHint(it) }
        val dateHint = rawParts.firstNotNullOfOrNull { dateOffsetFromToken(it) }
        val amountIndex = rawParts.indexOfLast { parseAmountMinor(it) != null }
        if (amountIndex == -1) {
            return ParsedExpenseInput(
                title = cleanupTitle(rawParts).ifBlank { trimmed },
                amountMinor = null,
                validationError = ExpenseValidationError(amountError = "Use an amount like 45k or 1.2m"),
            )
        }

        val titleParts = rawParts.filterIndexed { index, part ->
            index != amountIndex &&
                categoryFromHint(part) == null &&
                dateOffsetFromToken(part) == null &&
                part.normalizedWord() !in fillerWords
        }
        val title = cleanupTitle(titleParts)
        val suggestedCategory = suggestCategory(title, categoryHint ?: fallbackCategoryId)

        return ParsedExpenseInput(
            title = title,
            amountMinor = parseAmountMinor(rawParts[amountIndex]),
            categoryId = suggestedCategory.categoryId,
            categorySuggestion = suggestedCategory,
            createdAtMillis = dateHint?.let { offset ->
                nowMillis?.let { startOfUtcDay(it) + offset * MILLIS_PER_DAY }
            },
            validationError = ExpenseValidationError(
                titleError = if (title.isBlank()) "Add a description" else null,
            ),
        )
    }

    fun parseAmountMinor(raw: String): Long? {
        val normalized = raw
            .trim()
            .lowercase()
            .trim(',', '.', ';', ':')
            .replace(",", "")
            .replace("₫", "")
            .removeSuffix("vnd")
            .removeSuffix("dong")
        if (normalized.isBlank()) return null

        val multiplier = when {
            normalized.endsWith("k") -> 1_000L
            normalized.endsWith("m") -> 1_000_000L
            normalized.endsWith("tr") -> 1_000_000L
            else -> 1L
        }
        val numberText = if (multiplier == 1L) {
            normalized
        } else if (normalized.endsWith("tr")) {
            normalized.dropLast(2)
        } else {
            normalized.dropLast(1)
        }
        val number = numberText.toDoubleOrNull() ?: return null
        if (number <= 0.0) return null

        return (number * multiplier * 100).toLong()
    }

    fun suggestCategory(title: String, fallbackCategoryId: String): CategorySuggestion {
        val normalized = title.lowercase()
        categoryKeywords.forEach { rule ->
            val matchedKeyword = rule.keywords.firstOrNull { it in normalized }
            if (matchedKeyword != null) {
                return CategorySuggestion(
                    categoryId = rule.categoryId,
                    confidence = 0.92f,
                    reason = "Matched \"$matchedKeyword\"",
                )
            }
        }
        return CategorySuggestion(
            categoryId = fallbackCategoryId.ifBlank { "other" },
            confidence = 0.35f,
            reason = "Using selected category",
        )
    }

    private fun cleanupTitle(parts: List<String>): String {
        return parts
            .map { it.trim(',', '.', ';', ':') }
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun categoryFromHint(raw: String): String? {
        val token = raw.normalizedWord()
        val hint = when {
            token.startsWith("#") -> token.drop(1)
            token.startsWith("@") -> token.drop(1)
            else -> return null
        }
        return when (hint) {
            "food", "meal", "eat" -> "food"
            "drink", "drinks", "coffee" -> "drinks"
            "transport", "travel", "fuel" -> "transport"
            "bill", "bills", "utility" -> "bills"
            "shopping", "shop" -> "shopping"
            else -> "other"
        }
    }

    private fun dateOffsetFromToken(raw: String): Long? {
        return when (raw.normalizedWord()) {
            "today", "now" -> 0L
            "yesterday", "yday" -> -1L
            else -> null
        }
    }

    private fun String.normalizedWord(): String {
        return lowercase().trim(',', '.', ';', ':')
    }

    private fun startOfUtcDay(epochMillis: Long): Long {
        return floorDiv(epochMillis, MILLIS_PER_DAY) * MILLIS_PER_DAY
    }

    private fun floorDiv(value: Long, divisor: Long): Long {
        var result = value / divisor
        if ((value xor divisor) < 0 && result * divisor != value) {
            result--
        }
        return result
    }

    private data class CategoryRule(
        val categoryId: String,
        val keywords: List<String>,
    )

    private val fillerWords = setOf("for", "on", "at", "spent", "paid", "buy", "bought")

    private val categoryKeywords = listOf(
        CategoryRule("drinks", listOf("coffee", "tea", "milk", "juice", "smoothie", "beer")),
        CategoryRule("food", listOf("lunch", "dinner", "breakfast", "food", "meal", "rice", "noodle", "pho", "banh")),
        CategoryRule("transport", listOf("fuel", "gas", "taxi", "bus", "grab", "train", "parking", "bike")),
        CategoryRule("bills", listOf("electricity", "water", "rent", "internet", "wifi", "phone", "bill")),
        CategoryRule("shopping", listOf("shop", "shirt", "shoe", "market", "grocery", "groceries", "clothes")),
    )

    private const val MILLIS_PER_DAY = 86_400_000L
}

data class ParsedExpenseInput(
    val title: String,
    val amountMinor: Long?,
    val categoryId: String = "other",
    val categorySuggestion: CategorySuggestion = CategorySuggestion("other", 0f, ""),
    val createdAtMillis: Long? = null,
    val validationError: ExpenseValidationError = ExpenseValidationError(),
)

data class CategorySuggestion(
    val categoryId: String,
    val confidence: Float,
    val reason: String,
)
