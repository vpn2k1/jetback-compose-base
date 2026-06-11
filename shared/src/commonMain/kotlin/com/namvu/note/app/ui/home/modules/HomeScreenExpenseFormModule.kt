package com.namvu.note.app.ui.home.modules

internal fun homeScreenExpenseFormSaveText(editingExpenseId: String?): String {
    return if (editingExpenseId == null) "Save expense" else "Update expense"
}
