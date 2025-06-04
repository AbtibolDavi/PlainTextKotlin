package com.example.plaintextkotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.utils.UserDataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataStoreManager: UserDataStoreManager
) : ViewModel() {
    private val _newUsername = MutableStateFlow("")
    val newUsername: StateFlow<String> = _newUsername.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _uiMessage = MutableSharedFlow<Int>()
    val uiMessage: SharedFlow<Int> = _uiMessage.asSharedFlow()

    private val _isSavingUsername = MutableStateFlow(false)
    val isSavingUsername: StateFlow<Boolean> = _isSavingUsername.asStateFlow()

    private val _isSavingPassword = MutableStateFlow(false)
    val isSavingPassword: StateFlow<Boolean> = _isSavingPassword.asStateFlow()

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    private val _navigateToLogin = MutableSharedFlow<Unit>()
    val navigateToLogin: SharedFlow<Unit> = _navigateToLogin.asSharedFlow()

    val dynamicColorsEnabled: StateFlow<Boolean> = userDataStoreManager.dynamicColorsEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun setDynamicColorsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userDataStoreManager.saveDynamicColorsEnabled(enabled)
            _uiMessage.emit(if (enabled) R.string.dynamic_colors_enabled else R.string.dynamic_colors_disabled)
        }
    }

    fun updateNewUsername(input: String) {
        _newUsername.value = input
    }

    fun updateNewPassword(input: String) {
        _newPassword.value = input
    }

    fun updateConfirmPassword(input: String) {
        _confirmPassword.value = input
    }

    fun saveNewUsername() {
        val usernameToSave = _newUsername.value.trim()

        if (usernameToSave.isBlank()) {
            viewModelScope.launch { _uiMessage.emit(R.string.error_settings_username_empty) }
            return
        }

        viewModelScope.launch {
            _isSavingUsername.value = true
            try {
                val currentPassword = userDataStoreManager.getAppPasswordOnce()
                userDataStoreManager.saveAppCredentials(usernameToSave, currentPassword)

                userDataStoreManager.clearRememberMeCredentials()
                userDataStoreManager.saveRememberMeState(false)

                _uiMessage.emit(R.string.success_settings_username_update)

                _newUsername.value = ""

            } catch (_: Exception) {
                _uiMessage.emit(R.string.error_settings_username_update)
            } finally {
                _isSavingUsername.value = false
            }
        }
    }

    fun saveNewPassword() {
        val newPass = _newPassword.value
        val confirmPass = _confirmPassword.value

        if (newPass.isBlank()) {
            viewModelScope.launch { _uiMessage.emit(R.string.error_settings_password_empty) }
            return
        }
        if (newPass != confirmPass) {
            viewModelScope.launch { _uiMessage.emit(R.string.error_settings_password_mismatch) }
            return
        }

        viewModelScope.launch {
            _isSavingPassword.value = true
            try {
                val currentUsername = userDataStoreManager.getAppUsernameOnce()

                userDataStoreManager.saveAppCredentials(currentUsername, newPass)

                userDataStoreManager.clearRememberMeCredentials()
                userDataStoreManager.saveRememberMeState(false)

                _uiMessage.emit(R.string.success_settings_password_update)

                _newPassword.value = ""
                _confirmPassword.value = ""

            } catch (_: Exception) {
                _uiMessage.emit(R.string.error_settings_password_update)
            } finally {
                _isSavingPassword.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoggingOut.value = true
            try {
                userDataStoreManager.clearRememberMeCredentials()
                userDataStoreManager.saveRememberMeState(false)
                _navigateToLogin.emit(Unit)
            } catch (_: Exception) {
                _uiMessage.emit(R.string.error_settings_logout)
            } finally {
                _isLoggingOut.value = false
            }
        }
    }
}