package com.example.plaintextkotlin.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserDataStoreManager(context: Context) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    val defaultUsername = DataStoreKeys.DEFAULT_APP_USERNAME
    val defaultPassword = DataStoreKeys.DEFAULT_APP_PASSWORD

    val appUsernameFlow: Flow<String> = dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[DataStoreKeys.APP_USERNAME] ?: defaultUsername
        }

    val appPasswordFlow: Flow<String> = dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[DataStoreKeys.APP_PASSWORD] ?: defaultPassword
        }

    val rememberMeFlow: Flow<Boolean> = dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[DataStoreKeys.REMEMBER_ME] ?: false
        }

    val rememberMeUsernameFlow: Flow<String?> = dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[DataStoreKeys.REMEMBER_ME_USERNAME]
        }

    val rememberMePasswordFlow: Flow<String?> = dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[DataStoreKeys.REMEMBER_ME_PASSWORD]
        }

    suspend fun saveAppCredentials(username: String, password: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.APP_USERNAME] = username
            preferences[DataStoreKeys.APP_PASSWORD] = password
        }
    }

    suspend fun saveRememberMeState(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.REMEMBER_ME] = enabled
        }
    }

    suspend fun saveRememberMeCredentials(username: String, password: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.REMEMBER_ME_USERNAME] = username
            preferences[DataStoreKeys.REMEMBER_ME_PASSWORD] = password
        }
    }

    suspend fun clearRememberMeCredentials() {
        dataStore.edit { preferences ->
            preferences.remove(DataStoreKeys.REMEMBER_ME_USERNAME)
            preferences.remove(DataStoreKeys.REMEMBER_ME_PASSWORD)
        }
    }

    suspend fun getAppUsernameOnce(): String {
        return appUsernameFlow.first()
    }

    suspend fun getAppPasswordOnce(): String {
        return appPasswordFlow.first()
    }

    suspend fun getRememberMeStateOnce(): Boolean {
        return rememberMeFlow.first()
    }

    suspend fun getRememberMeUsernameOnce(): String? {
        return rememberMeUsernameFlow.first()
    }

    suspend fun getRememberMePasswordOnce(): String? {
        return rememberMePasswordFlow.first()
    }
}