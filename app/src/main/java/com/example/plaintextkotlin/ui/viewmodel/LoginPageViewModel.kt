package com.example.plaintextkotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.utils.UserDataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginPageViewModel(
    private val userDataStoreManager: UserDataStoreManager
) : ViewModel() {
    private val _loginError = MutableStateFlow<Int?>(null)
    val loginError: StateFlow<Int?> = _loginError.asStateFlow()

    private val _initialRememberMeState = MutableStateFlow(false)
    val initialRememberMeState: StateFlow<Boolean> = _initialRememberMeState.asStateFlow()

    init {
        viewModelScope.launch {
            _initialRememberMeState.value = userDataStoreManager.getRememberMeStateOnce()
        }
    }

    fun onLoginButtonClicked(
        usernameInput: String,
        passwordInput: String,
        rememberMeChecked: Boolean,
        navigateToPasswordPage: (String) -> Unit
    ) {
        viewModelScope.launch {
            val storedUsername = userDataStoreManager.getAppUsernameOnce()
            val storedPassword = userDataStoreManager.getAppPasswordOnce()

            if (usernameInput.isBlank() || passwordInput.isBlank()) {
                _loginError.value = R.string.error_login_required_fields
            } else if (usernameInput == storedUsername && passwordInput == storedPassword) {
                _loginError.value = null
                userDataStoreManager.saveRememberMeState(rememberMeChecked)
                if (rememberMeChecked) {
                    userDataStoreManager.saveRememberMeCredentials(usernameInput, passwordInput)
                } else {
                    userDataStoreManager.clearRememberMeCredentials()
                }
                navigateToPasswordPage(usernameInput)
            } else {
                _loginError.value = R.string.error_login_incorrect_credentials
            }
        }
    }

    fun clearLoginError() {
        _loginError.value = null
    }
}