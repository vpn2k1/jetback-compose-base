package com.namvu.myapplication.expense

import com.namvu.myapplication.expense.data.budget.InMemoryBudgetRepository
import com.namvu.myapplication.expense.data.google.InMemoryGoogleEcosystemService
import com.namvu.myapplication.expense.data.local.InMemoryLocalExpenseDatabase
import com.namvu.myapplication.expense.data.repository.LocalExpenseRepository
import com.namvu.myapplication.expense.domain.budget.BudgetRepository
import com.namvu.myapplication.expense.domain.insight.FinancialInsightUseCases
import com.namvu.myapplication.expense.domain.repository.ExpenseRepository
import com.namvu.myapplication.expense.domain.usecase.ExpenseUseCases
import com.namvu.myapplication.expense.domain.usecase.GoogleSyncUseCases
import com.namvu.myapplication.expense.domain.report.ReportUseCases

object ExpenseJournalGraph {
    private val database = InMemoryLocalExpenseDatabase()
    private val googleService = InMemoryGoogleEcosystemService()

    val repository: ExpenseRepository = LocalExpenseRepository(database)
    val budgetRepository: BudgetRepository = InMemoryBudgetRepository()
    private val googleSyncUseCases = GoogleSyncUseCases(repository, googleService)
    val insightUseCases: FinancialInsightUseCases = FinancialInsightUseCases(repository)
    val useCases: ExpenseUseCases = ExpenseUseCases(repository, googleSyncUseCases, insightUseCases)
    val reportUseCases: ReportUseCases = ReportUseCases(repository, budgetRepository, insightUseCases)
}
