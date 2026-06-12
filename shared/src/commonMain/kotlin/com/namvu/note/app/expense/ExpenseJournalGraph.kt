package com.namvu.note.app.expense

import com.namvu.note.app.expense.data.budget.InMemoryBudgetRepository
import com.namvu.note.app.expense.data.google.InMemoryGoogleEcosystemService
import com.namvu.note.app.expense.data.local.InMemoryLocalExpenseDatabase
import com.namvu.note.app.expense.data.repository.LocalExpenseRepository
import com.namvu.note.app.expense.domain.budget.BudgetRepository
import com.namvu.note.app.expense.domain.insight.FinancialInsightUseCases
import com.namvu.note.app.expense.domain.repository.ExpenseRepository
import com.namvu.note.app.expense.domain.sync.GoogleEcosystemService
import com.namvu.note.app.expense.domain.usecase.ExpenseUseCases
import com.namvu.note.app.expense.domain.usecase.GoogleSyncUseCases
import com.namvu.note.app.expense.domain.report.ReportUseCases

object ExpenseJournalGraph {
    private val database = InMemoryLocalExpenseDatabase()
    private var googleService: GoogleEcosystemService = InMemoryGoogleEcosystemService()

    val repository: ExpenseRepository = LocalExpenseRepository(database)
    val budgetRepository: BudgetRepository = InMemoryBudgetRepository()
    private var googleSyncUseCases = GoogleSyncUseCases(repository, googleService)
    val insightUseCases: FinancialInsightUseCases = FinancialInsightUseCases(repository)
    var useCases: ExpenseUseCases = ExpenseUseCases(repository, googleSyncUseCases, insightUseCases)
        private set
    val reportUseCases: ReportUseCases = ReportUseCases(repository, budgetRepository, insightUseCases)

    fun configureGoogleService(service: GoogleEcosystemService) {
        googleService = service
        googleSyncUseCases = GoogleSyncUseCases(repository, googleService)
        useCases = ExpenseUseCases(repository, googleSyncUseCases, insightUseCases)
    }
}
