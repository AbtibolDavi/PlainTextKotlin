package com.example.plaintextkotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.utils.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginPageViewModel(
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _initialRememberMeState = MutableStateFlow(false)
    val initialRememberMeState: StateFlow<Boolean> = _initialRememberMeState.asStateFlow()

    private val _initialUsername = MutableStateFlow("")
    val initialUsername: StateFlow<String> = _initialUsername.asStateFlow()

    init {
        _initialRememberMeState.value = preferenceManager.getRememberMeState()
        _initialUsername.value = preferenceManager.getRememberMeUsername() ?: ""
    }

    fun onLoginButtonClicked(
        usernameInput: String,
        passwordInput: String,
        rememberMeChecked: Boolean,
        navigateToPasswordPage: (String) -> Unit
    ) {
        viewModelScope.launch {
            val storedUsername =
                preferenceManager.getAppUsername() ?: PreferenceManager.DEFAULT_APP_USERNAME
            val storedPassword =
                preferenceManager.getAppPassword() ?: PreferenceManager.DEFAULT_APP_PASSWORD

            if (usernameInput.isBlank() || passwordInput.isBlank()) {
                _loginError.value = "Usuário e senha são obrigatórios"
            } else if (usernameInput == storedUsername && passwordInput == storedPassword) {
                _loginError.value = null
                preferenceManager.saveRememberMeState(rememberMeChecked)
                if (rememberMeChecked) {
                    preferenceManager.saveRememberMeCredentials(usernameInput, passwordInput)
                } else {
                    preferenceManager.clearRememberMeCredentials()
                }
                navigateToPasswordPage(usernameInput)
            } else {
                _loginError.value = "Usuário ou senha incorretos"
            }
        }
    }

    fun clearLoginError() {
        _loginError.value = null
    }
}