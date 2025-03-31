package com.map711s.namibiahockey.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.map711s.namibiahockey.util.Constants.Prefs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages user preferences and authentication state using encrypted shared preferences
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        Prefs.PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Authentication
    private val _authToken = MutableStateFlow<String?>(getAuthToken())
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(getAuthToken() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // User information
    private val _userId = MutableStateFlow<String?>(getUserId())
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _userName = MutableStateFlow<String?>(getUserName())
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(getUserEmail())
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    // App settings
    private val _darkMode = MutableStateFlow(getDarkMode())
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(getNotificationsEnabled())
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    // Authentication methods
    fun saveAuthToken(token: String) {
        prefs.edit {
            putString(Prefs.KEY_AUTH_TOKEN, token)
        }
        _authToken.value = token
        _isLoggedIn.value = true
    }

    fun clearAuthToken() {
        prefs.edit {
            remove(Prefs.KEY_AUTH_TOKEN)
        }
        _authToken.value = null
        _isLoggedIn.value = false
    }

    private fun getAuthToken(): String? {
        return prefs.getString(Prefs.KEY_AUTH_TOKEN, null)
    }

    // User information methods
    fun saveUserInfo(id: String, name: String, email: String) {
        prefs.edit {
            putString(Prefs.KEY_USER_ID, id)
            putString(Prefs.KEY_USER_NAME, name)
            putString(Prefs.KEY_USER_EMAIL, email)
        }
        _userId.value = id
        _userName.value = name
        _userEmail.value = email
    }

    fun clearUserInfo() {
        prefs.edit {
            remove(Prefs.KEY_USER_ID)
            remove(Prefs.KEY_USER_NAME)
            remove(Prefs.KEY_USER_EMAIL)
        }
        _userId.value = null
        _userName.value = null
        _userEmail.value = null
    }

    private fun getUserId(): String? {
        return prefs.getString(Prefs.KEY_USER_ID, null)
    }

    private fun getUserName(): String? {
        return prefs.getString(Prefs.KEY_USER_NAME, null)
    }

    private fun getUserEmail(): String? {
        return prefs.getString(Prefs.KEY_USER_EMAIL, null)
    }

    // App settings methods
    fun setDarkMode(enabled: Boolean) {
        prefs.edit {
            putBoolean(Prefs.KEY_DARK_MODE, enabled)
        }
        _darkMode.value = enabled
    }

    private fun getDarkMode(): Boolean {
        return prefs.getBoolean(Prefs.KEY_DARK_MODE, false)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(Prefs.KEY_NOTIFICATION_ENABLED, enabled)
        }
        _notificationsEnabled.value = enabled
    }

    private fun getNotificationsEnabled(): Boolean {
        return prefs.getBoolean(Prefs.KEY_NOTIFICATION_ENABLED, true)
    }

    // Last sync timestamp for offline first strategy
    fun updateLastSyncTimestamp() {
        prefs.edit {
            putLong(Prefs.KEY_LAST_SYNC, System.currentTimeMillis())
        }
    }

    fun getLastSyncTimestamp(): Long {
        return prefs.getLong(Prefs.KEY_LAST_SYNC, 0L)
    }

    // Full logout - clears all user-specific data
    fun logout() {
        clearAuthToken()
        clearUserInfo()
    }
}