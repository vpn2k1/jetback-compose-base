package com.namvu.note.app.expense.domain.model

import androidx.compose.ui.graphics.Color

data class Category(
    val id: String,
    val name: String,
    val color: Color,
    val icon: String,
)

object DefaultCategories {
    val items = listOf(
        Category(
            id = "food",
            name = "Food",
            color = Color(0xFF0F766E),
            icon = "F",
        ),
        Category(
            id = "drinks",
            name = "Drinks",
            color = Color(0xFF2563EB),
            icon = "D",
        ),
        Category(
            id = "transport",
            name = "Transport",
            color = Color(0xFFEA580C),
            icon = "T",
        ),
        Category(
            id = "bills",
            name = "Bills",
            color = Color(0xFF7C3AED),
            icon = "B",
        ),
        Category(
            id = "shopping",
            name = "Shopping",
            color = Color(0xFFC026D3),
            icon = "S",
        ),
        Category(
            id = "other",
            name = "Other",
            color = Color(0xFF64748B),
            icon = "O",
        ),
    )
}
