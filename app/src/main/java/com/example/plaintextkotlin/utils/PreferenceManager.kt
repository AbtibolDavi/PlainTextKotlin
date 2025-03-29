package com.example.plaintextkotlin.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_FILENAME = "com.example.plaintextkotlin.prefs"
        private const val PREF_KEY_APP_USERNAME = "app_username"
        private const val PREF_KEY_APP_PASSWORD = "app_password"
        private const val PREF_KEY_REMEMBER_ME = "remember_me"

        private const val PREF_KEY_REMEMBER_ME_USERNAME = "remember_me_username"
        private const val PREF_KEY_REMEMBER_ME_PASSWORD = "remember_me_password"

        const val DEFAULT_APP_USERNAME = "admin"
        const val DEFAULT_APP_PASSWORD = "password"
    }

    fun saveAppCredentials(username: String, password: String) {
        with(sharedPreferences.edit()) {
            putString(PREF_KEY_APP_USERNAME, username)
            putString(PREF_KEY_APP_PASSWORD, password)
            apply()
        }
    }

    fun getAppUsername(): String? {
        return sharedPreferences.getString(PREF_KEY_APP_USERNAME, null)
    }

    fun getAppPassword(): String? {
        return sharedPreferences.getString(PREF_KEY_APP_PASSWORD, null)
    }

    fun saveRememberMeState(enabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(PREF_KEY_REMEMBER_ME, enabled)
            apply()
        }
    }

    fun getRememberMeState(): Boolean {
        return sharedPreferences.getBoolean(PREF_KEY_REMEMBER_ME, false)
    }

    fun saveRememberMeCredentials(username: String, password: String) {
        with(sharedPreferences.edit()) {
            putString(PREF_KEY_REMEMBER_ME_USERNAME, username)
            putString(PREF_KEY_REMEMBER_ME_PASSWORD, password)
            apply()
        }
    }

    fun getRememberMeUsername(): String? {
        return sharedPreferences.getString(PREF_KEY_REMEMBER_ME_USERNAME, null)
    }

    fun getRememberMePassword(): String? {
        return sharedPreferences.getString(
            PREF_KEY_REMEMBER_ME_PASSWORD,
            null
        )
    }

    fun clearRememberMeCredentials() {
        with(sharedPreferences.edit()) {
            remove(PREF_KEY_REMEMBER_ME_USERNAME)
            remove(PREF_KEY_REMEMBER_ME_PASSWORD)
            apply()
        }
    }
}