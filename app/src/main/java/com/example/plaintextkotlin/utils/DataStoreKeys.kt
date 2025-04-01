package com.example.plaintextkotlin.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
    val APP_USERNAME = stringPreferencesKey("app_username")
    val APP_PASSWORD = stringPreferencesKey("app_password")
    val REMEMBER_ME = booleanPreferencesKey("remember_me")
    val REMEMBER_ME_USERNAME = stringPreferencesKey("remember_me_username")
    val REMEMBER_ME_PASSWORD = stringPreferencesKey("remember_me_password")

    const val DEFAULT_APP_USERNAME = "admin"
    const val DEFAULT_APP_PASSWORD = "password"
}