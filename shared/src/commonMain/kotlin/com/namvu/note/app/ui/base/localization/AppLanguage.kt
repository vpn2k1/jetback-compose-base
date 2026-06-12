package com.namvu.note.app.ui.base.localization

enum class AppLanguage(
    val code: String,
    val label: String,
) {
    English("en", "English"),
    Vietnamese("vi", "Tiếng Việt"),
}

enum class AppText {
    TabHome,
    TabExpenses,
    TabReports,
    TabBudgets,
    TabSettings,
    SettingsTitle,
    Theme,
    GoogleSheets,
    GoogleSheetsConnected,
    GoogleSheetsLocalFirst,
    GoogleAccount,
    GoogleNoAccount,
    GoogleAccountOverline,
    SignOut,
    Currency,
    CurrencyVnd,
    Language,
    Notifications,
    NotificationsSubtitle,
    Privacy,
    PrivacySubtitle,
    Backup,
    BackupSubtitle,
    About,
    AboutSubtitle,
    System,
    Light,
    Dark,
}

fun AppLanguage.text(key: AppText): String {
    return when (this) {
        AppLanguage.English -> key.englishText()
        AppLanguage.Vietnamese -> key.vietnameseText()
    }
}

private fun AppText.englishText(): String {
    return when (this) {
        AppText.TabHome -> "Dashboard"
        AppText.TabExpenses -> "Expenses"
        AppText.TabReports -> "Reports"
        AppText.TabBudgets -> "Budgets"
        AppText.TabSettings -> "Settings"
        AppText.SettingsTitle -> "Settings"
        AppText.Theme -> "Theme"
        AppText.GoogleSheets -> "Google Sheets"
        AppText.GoogleSheetsConnected -> "Connected to %s."
        AppText.GoogleSheetsLocalFirst -> "Expense changes stay local first, then sync when a spreadsheet is connected."
        AppText.GoogleAccount -> "Google Account"
        AppText.GoogleNoAccount -> "No account signed in."
        AppText.GoogleAccountOverline -> "Google Account"
        AppText.SignOut -> "Sign out"
        AppText.Currency -> "Currency"
        AppText.CurrencyVnd -> "Vietnamese dong (VND)"
        AppText.Language -> "Language"
        AppText.Notifications -> "Notifications"
        AppText.NotificationsSubtitle -> "Budget alerts and sync reminders"
        AppText.Privacy -> "Privacy"
        AppText.PrivacySubtitle -> "Local-first data, user-owned Sheets backup"
        AppText.Backup -> "Backup"
        AppText.BackupSubtitle -> "Manual sync and restore foundation"
        AppText.About -> "About"
        AppText.AboutSubtitle -> "Expense Journal"
        AppText.System -> "System"
        AppText.Light -> "Light"
        AppText.Dark -> "Dark"
    }
}

private fun AppText.vietnameseText(): String {
    return when (this) {
        AppText.TabHome -> "Tổng quan"
        AppText.TabExpenses -> "Chi tiêu"
        AppText.TabReports -> "Báo cáo"
        AppText.TabBudgets -> "Ngân sách"
        AppText.TabSettings -> "Cài đặt"
        AppText.SettingsTitle -> "Cài đặt"
        AppText.Theme -> "Giao diện"
        AppText.GoogleSheets -> "Google Sheets"
        AppText.GoogleSheetsConnected -> "Đã kết nối với %s."
        AppText.GoogleSheetsLocalFirst -> "Thay đổi chi tiêu được lưu cục bộ trước, rồi đồng bộ khi đã kết nối bảng tính."
        AppText.GoogleAccount -> "Tài khoản Google"
        AppText.GoogleNoAccount -> "Chưa đăng nhập tài khoản."
        AppText.GoogleAccountOverline -> "Tài khoản Google"
        AppText.SignOut -> "Đăng xuất"
        AppText.Currency -> "Tiền tệ"
        AppText.CurrencyVnd -> "Việt Nam đồng (VND)"
        AppText.Language -> "Ngôn ngữ"
        AppText.Notifications -> "Thông báo"
        AppText.NotificationsSubtitle -> "Nhắc nhở ngân sách và đồng bộ"
        AppText.Privacy -> "Quyền riêng tư"
        AppText.PrivacySubtitle -> "Dữ liệu ưu tiên lưu cục bộ, sao lưu bằng Sheets của người dùng"
        AppText.Backup -> "Sao lưu"
        AppText.BackupSubtitle -> "Nền tảng đồng bộ và khôi phục thủ công"
        AppText.About -> "Giới thiệu"
        AppText.AboutSubtitle -> "Nhật ký chi tiêu"
        AppText.System -> "Hệ thống"
        AppText.Light -> "Sáng"
        AppText.Dark -> "Tối"
    }
}
