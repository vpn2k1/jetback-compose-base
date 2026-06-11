package com.namvu.myapplication.expense.domain.usecase

import com.namvu.myapplication.expense.domain.insight.FinancialInsight
import com.namvu.myapplication.expense.domain.model.Category
import com.namvu.myapplication.expense.domain.model.Expense
import com.namvu.myapplication.expense.domain.sync.GoogleSyncSession

data class ExpenseJournal(
    val categories: List<Category>,
    val expenses: List<Expense>,
    val insights: List<FinancialInsight> = emptyList(),
    val syncSession: GoogleSyncSession = GoogleSyncSession(
        account = null,
        spreadsheet = null,
    ),
)
