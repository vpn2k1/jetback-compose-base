package com.namvu.myapplication.ui.home.modules

internal fun homeScreenExpenseFormSaveText(editingExpenseId: String?): String {
    return if (editingExpenseId == null) "Save expense" else "Update expense"
}
