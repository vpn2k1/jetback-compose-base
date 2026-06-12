package com.namvu.note.app.google

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.namvu.note.app.expense.domain.sync.GoogleAccessGrant
import com.namvu.note.app.expense.domain.sync.GoogleAccount
import com.namvu.note.app.expense.domain.sync.GoogleAuthorizationScope
import com.namvu.note.app.expense.domain.sync.GoogleEcosystemService
import com.namvu.note.app.expense.domain.sync.GoogleSheetRow
import com.namvu.note.app.expense.domain.sync.GoogleSpreadsheet
import com.namvu.note.app.expense.domain.sync.GoogleSyncSession
import java.security.SecureRandom

class AndroidGoogleEcosystemService(
    context: Context,
    private val serverClientId: String,
) : GoogleEcosystemService {
    private val appContext = context.applicationContext
    private val credentialContext = context
    private val credentialManager = CredentialManager.create(context)
    private val preferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private var account: GoogleAccount? = preferences.readAccount()
    private var spreadsheet: GoogleSpreadsheet? = null
    private var accessGrant = GoogleAccessGrant()
    private val spreadsheets = mutableMapOf<String, GoogleSpreadsheet>()
    private val sheets = mutableMapOf<String, MutableSet<String>>()
    private val rows = mutableMapOf<String, MutableList<GoogleSheetRow>>()

    override suspend fun getSession(): GoogleSyncSession {
        return GoogleSyncSession(
            account = account,
            spreadsheet = spreadsheet,
            accessGrant = accessGrant,
        )
    }

    override suspend fun signIn(): GoogleAccount {
        ensureConfiguredClientId()
        return runCatching {
            requestGoogleCredential(filterByAuthorizedAccounts = false)
        }.getOrElse { throwable ->
            throw IllegalStateException(mapSignInFailure(throwable), throwable)
        }.also { signedIn ->
            account = signedIn
            preferences.writeAccount(signedIn)
        }
    }

    override suspend fun signOut() {
        runCatching {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        }.also {
            clearLocalSession()
        }.getOrElse { throwable ->
            throw IllegalStateException(mapSignInFailure(throwable), throwable)
        }
    }

    override suspend fun disconnect() {
        signOut()
        spreadsheets.clear()
        sheets.clear()
        rows.clear()
    }

    override suspend fun requestSheetsAccess(): GoogleAccessGrant {
        requireSignedIn()
        return GoogleAccessGrant(setOf(GoogleAuthorizationScope.DriveFile))
            .also { accessGrant = it }
    }

    override suspend fun findSpreadsheet(name: String): GoogleSpreadsheet? {
        requireSheetsAccess()
        return spreadsheets.values.firstOrNull { it.name == name }
    }

    override suspend fun createSpreadsheet(name: String): GoogleSpreadsheet {
        requireSheetsAccess()
        val created = GoogleSpreadsheet(
            id = "spreadsheet-${spreadsheets.size + 1}",
            name = name,
            webUrl = "https://docs.google.com/spreadsheets/d/spreadsheet-${spreadsheets.size + 1}",
        )
        spreadsheets[created.id] = created
        spreadsheet = created
        return created
    }

    override suspend fun selectSpreadsheet(spreadsheetId: String): GoogleSpreadsheet {
        requireSheetsAccess()
        return requireNotNull(spreadsheets[spreadsheetId]) { "Spreadsheet not found" }
            .also { spreadsheet = it }
    }

    override suspend fun ensureMonthlySheet(spreadsheetId: String, monthKey: String) {
        requireSheetsAccess()
        sheets.getOrPut(spreadsheetId) { mutableSetOf() }.add(monthKey)
    }

    override suspend fun appendExpenseRows(
        spreadsheetId: String,
        monthKey: String,
        rows: List<GoogleSheetRow>,
    ) {
        requireSheetsAccess()
        this.rows.getOrPut("$spreadsheetId:$monthKey") { mutableListOf() }.addAll(rows)
    }

    private suspend fun requestGoogleCredential(filterByAuthorizedAccounts: Boolean): GoogleAccount {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(serverClientId)
            .setNonce(createNonce())
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        val response = credentialManager.getCredential(credentialContext, request)
        val credential = response.credential

        if (
            credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            throw IllegalStateException("Selected credential is not a Google account")
        }

        return try {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            GoogleAccount(
                id = googleCredential.id,
                displayName = googleCredential.displayName
                    ?: googleCredential.givenName
                    ?: googleCredential.id,
                email = googleCredential.id,
            )
        } catch (exception: GoogleIdTokenParsingException) {
            throw IllegalStateException("Could not parse Google account", exception)
        } catch (exception: GetCredentialException) {
            throw IllegalStateException(mapSignInFailure(exception), exception)
        }
    }

    private fun ensureConfiguredClientId() {
        require(serverClientId.isNotBlank() && !serverClientId.startsWith("REPLACE_WITH_")) {
            "Configure google_server_client_id with your Web OAuth client ID"
        }
    }

    private fun createNonce(): String {
        val bytes = ByteArray(NONCE_BYTE_COUNT)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun mapSignInFailure(throwable: Throwable): String {
        val className = throwable::class.simpleName.orEmpty()
        val rawMessage = throwable.message.orEmpty()
        val message = rawMessage.lowercase()
        return when {
            throwable is NoCredentialException -> {
                "No Google account is available on this device"
            }
            className.contains("Cancellation", ignoreCase = true) -> {
                "Google sign in was cancelled"
            }
            message.contains("developer console") ||
                message.contains("configuration") ||
                message.contains("10:") -> {
                "Google sign in is not configured correctly. Check the Web client ID, Android package name, and debug SHA-1 in Google Cloud Console"
            }
            message.contains("network") -> {
                "Could not reach Google. Check your connection and try again"
            }
            rawMessage.isNotBlank() -> {
                "Google sign in failed: $rawMessage"
            }
            else -> {
                "Google sign in failed"
            }
        }
    }

    private fun requireSignedIn() {
        require(account != null) { "Sign in with Google before syncing" }
    }

    private fun requireSheetsAccess() {
        requireSignedIn()
        require(accessGrant.hasSheetsAccess) { "Allow Google Sheets access before syncing" }
    }

    private fun clearLocalSession() {
        account = null
        spreadsheet = null
        accessGrant = GoogleAccessGrant()
        preferences.clearAccount()
    }

    private fun SharedPreferences.readAccount(): GoogleAccount? {
        val id = getString(KEY_ACCOUNT_ID, null) ?: return null
        val email = getString(KEY_ACCOUNT_EMAIL, null) ?: return null
        val displayName = getString(KEY_ACCOUNT_DISPLAY_NAME, null) ?: email
        return GoogleAccount(
            id = id,
            displayName = displayName,
            email = email,
        )
    }

    private fun SharedPreferences.writeAccount(account: GoogleAccount) {
        edit()
            .putString(KEY_ACCOUNT_ID, account.id)
            .putString(KEY_ACCOUNT_DISPLAY_NAME, account.displayName)
            .putString(KEY_ACCOUNT_EMAIL, account.email)
            .apply()
    }

    private fun SharedPreferences.clearAccount() {
        edit()
            .remove(KEY_ACCOUNT_ID)
            .remove(KEY_ACCOUNT_DISPLAY_NAME)
            .remove(KEY_ACCOUNT_EMAIL)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "google_session"
        const val KEY_ACCOUNT_ID = "account_id"
        const val KEY_ACCOUNT_DISPLAY_NAME = "account_display_name"
        const val KEY_ACCOUNT_EMAIL = "account_email"
        const val NONCE_BYTE_COUNT = 32
    }
}
