package com.example.plaintextkotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.utils.PreferenceManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    private val _newUsername = MutableStateFlow("")
    val newUsername: StateFlow<String> = _newUsername.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage: SharedFlow<String> = _uiMessage.asSharedFlow()

    private val _isSavingUsername = MutableStateFlow(false)
    val isSavingUsername: StateFlow<Boolean> = _isSavingUsername.asStateFlow()

    private val _isSavingPassword = MutableStateFlow(false)
    val isSavingPassword: StateFlow<Boolean> = _isSavingPassword.asStateFlow()

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    private val _navigateToLogin = MutableSharedFlow<Unit>()
    val navigateToLogin: SharedFlow<Unit> = _navigateToLogin.asSharedFlow()

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
            viewModelScope.launch { _uiMessage.emit("Nome de usuário não pode ser vazio.") }
            return
        }

        viewModelScope.launch {
            _isSavingUsername.value = true
            try {
                val currentPassword =
                    preferenceManager.getAppPassword() ?: PreferenceManager.DEFAULT_APP_PASSWORD
                preferenceManager.saveAppCredentials(usernameToSave, currentPassword)

                preferenceManager.clearRememberMeCredentials()
                preferenceManager.saveRememberMeState(false)

                _uiMessage.emit("Nome de usuário atualizado com sucesso!")

                _newUsername.value = ""

            } catch (_: Exception) {
                _uiMessage.emit("Erro ao atualizar nome de usuário.")
            } finally {
                _isSavingUsername.value = false
            }
        }
    }

    fun saveNewPassword() {
        val newPass = _newPassword.value
        val confirmPass = _confirmPassword.value

        if (newPass.isBlank()) {
            viewModelScope.launch { _uiMessage.emit("Nova senha não pode ser vazia.") }
            return
        }
        if (newPass != confirmPass) {
            viewModelScope.launch { _uiMessage.emit("As senhas não coincidem.") }
            return
        }

        viewModelScope.launch {
            _isSavingPassword.value = true
            try {
                val currentUsername =
                    preferenceManager.getAppUsername() ?: PreferenceManager.DEFAULT_APP_USERNAME

                preferenceManager.saveAppCredentials(
                    currentUsername, newPass
                )

                preferenceManager.clearRememberMeCredentials()
                preferenceManager.saveRememberMeState(false)

                _uiMessage.emit("Senha atualizada com sucesso!")

                _newPassword.value = ""
                _confirmPassword.value = ""

            } catch (_: Exception) {
                _uiMessage.emit("Erro ao atualizar senha.")
            } finally {
                _isSavingPassword.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoggingOut.value = true
            try {
                preferenceManager.clearRememberMeCredentials()
                preferenceManager.saveRememberMeState(false)
                _navigateToLogin.emit(Unit)
            } catch (_: Exception) {
                _uiMessage.emit("Erro ao fazer logout.")
            } finally {
                _isLoggingOut.value = false
            }
        }
    }
}