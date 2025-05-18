package com.map711s.namibiahockey.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext

class SecureStorageManager(
    @ApplicationContext private val context: Context
) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val securePrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            "hockey_secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Store sensitive data
    fun storeAuthToken(token: String) {
        securePrefs.edit().putString("auth_token", token).apply()
    }

    fun getAuthToken(): String? {
        return securePrefs.getString("auth_token", null)
    }

    fun clearAuthToken() {
        securePrefs.edit().remove("auth_token").apply()
    }

    // Store user ID securely
    fun storeUserId(userId: String) {
        securePrefs.edit().putString("user_id", userId).apply()
    }

    fun getUserId(): String? {
        return securePrefs.getString("user_id", null)
    }

    fun clearUserId() {
        securePrefs.edit().remove("user_id").apply()
    }

    // General secure key-value storage
    fun storeString(key: String, value: String) {
        securePrefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return securePrefs.getString(key, defaultValue)
    }

    fun storeBoolean(key: String, value: Boolean) {
        securePrefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return securePrefs.getBoolean(key, defaultValue)
    }

    fun clearAll() {
        securePrefs.edit().clear().apply()
    }
}