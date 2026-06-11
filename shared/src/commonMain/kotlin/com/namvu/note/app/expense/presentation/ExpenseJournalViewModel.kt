package com.namvu.note.app.expense.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namvu.note.app.expense.domain.model.ExpenseDraft
import com.namvu.note.app.expense.domain.model.ExpenseFormatter
import com.namvu.note.app.expense.domain.model.ExpenseValidationError
import com.namvu.note.app.expense.domain.model.QuickExpenseParser
import com.namvu.note.app.expense.domain.usecase.ExpenseUseCases
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ExpenseJournalViewModel(
    private val useCases: ExpenseUseCases,
    private val clock: () -> Long = currentTimeMillisProvider(),
) : ViewModel() {
    var state by mutableStateOf(ExpenseJournalState())
        private set

    init {
        loadJournal()
    }

    fun loadJournal() {
        state = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { useCases.loadJournal() }
                .onSuccess { journal ->
                    state = state.copy(
                        isLoading = false,
                        categories = journal.categories,
                        expenses = journal.expenses,
                        insights = journal.insights,
                        syncSession = journal.syncSession,
                        recentTemplates = buildTemplates(journal.expenses, journal.categories),
                        selectedCategoryId = state.selectedCategoryId.takeIf { selected ->
                            journal.categories.any { it.id == selected }
                        } ?: journal.categories.firstOrNull()?.id.orEmpty(),
                    )
                }
                .onFailure { throwable ->
                    state = state.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Could not load expenses",
                    )
                }
        }
    }

    fun onQuickEntryChange(value: String) {
        state = state.copy(
            quickEntry = value,
            validationError = ExpenseValidationError(),
            quickPreview = buildPreview(value),
            duplicateCandidate = null,
        )
    }

    fun onTitleChange(value: String) {
        state = state.copy(titleInput = value, validationError = state.validationError.copy(titleError = null))
    }

    fun onAmountChange(value: String) {
        state = state.copy(amountInput = value, validationError = state.validationError.copy(amountError = null))
    }

    fun onNoteChange(value: String) {
        state = state.copy(noteInput = value)
    }

    fun onCategorySelected(categoryId: String) {
        state = state.copy(selectedCategoryId = categoryId)
    }

    fun signInToGoogle() {
        runSyncAction(
            successMessage = "Google account connected",
            action = { useCases.signInToGoogle() },
        )
    }

    fun signOutFromGoogle() {
        runSyncAction(
            successMessage = "Signed out of Google",
            action = { useCases.signOutFromGoogle() },
        )
    }

    fun connectSpreadsheet() {
        runSyncAction(
            successMessage = "Expense Journal spreadsheet is ready",
            action = { useCases.connectSpreadsheet() },
        )
    }

    fun syncPendingExpenses() {
        runSyncAction(
            successMessage = "Sync finished",
            action = { useCases.syncPendingExpenses() },
        )
    }

    fun retryFailedSync() {
        runSyncAction(
            successMessage = "Retry finished",
            action = { useCases.retryFailedSync() },
        )
    }

    fun addQuickExpense() {
        val parsed = QuickExpenseParser.parse(
            input = state.quickEntry,
            fallbackCategoryId = state.selectedCategoryId,
            nowMillis = clock(),
        )
        if (parsed.validationError.hasError || parsed.amountMinor == null) {
            state = state.copy(
                validationError = parsed.validationError,
                quickPreview = null,
            )
            return
        }

        val preview = buildPreview(parsed)
        if (preview != null && isDuplicate(preview)) {
            state = state.copy(duplicateCandidate = preview)
            return
        }

        saveQuickPreview(requireNotNull(preview))
    }

    fun confirmDuplicateQuickAdd() {
        val preview = state.duplicateCandidate ?: return
        saveQuickPreview(preview)
    }

    fun dismissDuplicateQuickAdd() {
        state = state.copy(duplicateCandidate = null)
    }

    fun useTemplate(template: ExpenseTemplate) {
        state = state.copy(
            quickEntry = template.quickText,
            selectedCategoryId = template.categoryId,
            validationError = ExpenseValidationError(),
            quickPreview = buildPreview(template.quickText),
            duplicateCandidate = null,
        )
    }

    fun startAddExpense() {
        state = state.copy(
            editingExpenseId = null,
            titleInput = "",
            amountInput = "",
            noteInput = "",
            validationError = ExpenseValidationError(),
        )
    }

    fun startEditExpense(expenseId: String) {
        val expense = state.expenses.firstOrNull { it.id == expenseId } ?: return
        state = state.copy(
            editingExpenseId = expense.id,
            titleInput = expense.title,
            amountInput = ExpenseFormatter.formatAmount(expense.amountMinor).replace(",", ""),
            noteInput = expense.note,
            selectedCategoryId = expense.categoryId,
            validationError = ExpenseValidationError(),
        )
    }

    fun saveCurrentForm() {
        val amountMinor = QuickExpenseParser.parseAmountMinor(state.amountInput)
        val draft = ExpenseDraft(
            title = state.titleInput,
            amountMinor = amountMinor ?: 0L,
            categoryId = state.selectedCategoryId,
            note = state.noteInput,
        )
        val validationError = useCases.validateDraft(draft).copy(
            amountError = if (amountMinor == null) "Use an amount like 45k or 1.2m" else null,
        )
        if (validationError.hasError) {
            state = state.copy(validationError = validationError)
            return
        }

        saveDraft(
            draft = draft,
            editingExpenseId = state.editingExpenseId,
            onSaved = { startAddExpense() },
        )
    }

    fun requestDeleteExpense(expenseId: String) {
        state = state.copy(deleteCandidateId = expenseId)
    }

    fun dismissDeleteDialog() {
        state = state.copy(deleteCandidateId = null)
    }

    fun confirmDeleteExpense() {
        val expenseId = state.deleteCandidateId ?: return
        viewModelScope.launch {
            runCatching { useCases.deleteExpense(expenseId) }
                .onSuccess {
                    state = state.copy(deleteCandidateId = null)
                    refreshAfterMutation()
                }
                .onFailure { throwable ->
                    state = state.copy(
                        deleteCandidateId = null,
                        errorMessage = throwable.message ?: "Could not delete expense",
                    )
                }
        }
    }

    private fun saveDraft(
        draft: ExpenseDraft,
        editingExpenseId: String? = null,
        onSaved: () -> Unit,
    ) {
        state = state.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                if (editingExpenseId == null) {
                    useCases.addExpense(draft)
                } else {
                    useCases.updateExpense(editingExpenseId, draft)
                }
            }
                .onSuccess {
                    state = state.copy(isSaving = false)
                    refreshAfterMutation()
                    onSaved()
                }
                .onFailure { throwable ->
                    state = state.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Could not save expense",
                    )
                }
        }
    }

    private suspend fun refreshAfterMutation() {
        val journal = useCases.loadJournal()
        state = state.copy(
            categories = journal.categories,
            expenses = journal.expenses,
            insights = journal.insights,
            syncSession = journal.syncSession,
            recentTemplates = buildTemplates(journal.expenses, journal.categories),
            quickPreview = buildPreview(state.quickEntry),
            errorMessage = null,
        )
    }

    private fun runSyncAction(
        successMessage: String,
        action: suspend () -> com.namvu.note.app.expense.domain.usecase.ExpenseJournal,
    ) {
        state = state.copy(isSyncing = true, errorMessage = null, syncMessage = null)
        viewModelScope.launch {
            runCatching { action() }
                .onSuccess { journal ->
                    state = state.copy(
                        isSyncing = false,
                        categories = journal.categories,
                        expenses = journal.expenses,
                        insights = journal.insights,
                        syncSession = journal.syncSession,
                        recentTemplates = buildTemplates(journal.expenses, journal.categories),
                        syncMessage = successMessage,
                    )
                }
                .onFailure { throwable ->
                    state = state.copy(
                        isSyncing = false,
                        errorMessage = throwable.message ?: "Google sync failed",
                    )
                }
        }
    }

    private fun saveQuickPreview(preview: QuickExpensePreview) {
        saveDraft(
            draft = ExpenseDraft(
                title = preview.title,
                amountMinor = preview.amountMinor,
                categoryId = preview.categoryId,
                createdAtMillis = preview.createdAtMillis,
            ),
            onSaved = {
                state = state.copy(
                    quickEntry = "",
                    titleInput = "",
                    amountInput = "",
                    noteInput = "",
                    selectedCategoryId = preview.categoryId,
                    validationError = ExpenseValidationError(),
                    quickPreview = null,
                    duplicateCandidate = null,
                )
            },
        )
    }

    private fun buildPreview(input: String): QuickExpensePreview? {
        val parsed = QuickExpenseParser.parse(
            input = input,
            fallbackCategoryId = state.selectedCategoryId,
            nowMillis = clock(),
        )
        return buildPreview(parsed)
    }

    private fun buildPreview(
        parsed: com.namvu.note.app.expense.domain.model.ParsedExpenseInput,
    ): QuickExpensePreview? {
        val amountMinor = parsed.amountMinor ?: return null
        if (parsed.validationError.hasError) return null
        val category = state.categories.firstOrNull { it.id == parsed.categoryId }
        return QuickExpensePreview(
            title = parsed.title,
            amountMinor = amountMinor,
            categoryId = parsed.categoryId,
            categoryName = category?.name ?: parsed.categoryId,
            suggestionReason = parsed.categorySuggestion.reason,
            createdAtMillis = parsed.createdAtMillis,
        )
    }

    private fun isDuplicate(preview: QuickExpensePreview): Boolean {
        val recentWindowStart = clock() - DUPLICATE_WINDOW_MILLIS
        return state.expenses.any { expense ->
            expense.createdAtMillis >= recentWindowStart &&
                expense.title.equals(preview.title, ignoreCase = true) &&
                expense.amountMinor == preview.amountMinor
        }
    }

    private fun buildTemplates(
        expenses: List<com.namvu.note.app.expense.domain.model.Expense>,
        categories: List<com.namvu.note.app.expense.domain.model.Category>,
    ): List<ExpenseTemplate> {
        val categoriesById = categories.associateBy { it.id }
        return expenses
            .distinctBy { "${it.title.lowercase()}-${it.amountMinor}-${it.categoryId}" }
            .take(6)
            .map { expense ->
                val category = categoriesById[expense.categoryId]
                ExpenseTemplate(
                    title = expense.title,
                    amountMinor = expense.amountMinor,
                    categoryId = expense.categoryId,
                    categoryName = category?.name ?: expense.categoryId,
                    quickText = "${expense.title} ${ExpenseFormatter.formatAmount(expense.amountMinor).replace(",", "")}",
                )
            }
    }

    private companion object {
        const val DUPLICATE_WINDOW_MILLIS = 10 * 60 * 1000L
    }
}

@OptIn(ExperimentalTime::class)
private fun currentTimeMillisProvider(): () -> Long = {
    Clock.System.now().toEpochMilliseconds()
}
