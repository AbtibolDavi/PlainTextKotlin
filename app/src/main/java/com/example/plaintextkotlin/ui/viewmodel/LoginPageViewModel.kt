package com.example.plaintextkotlin.ui.viewmodel

import android.R
import android.R.attr.password
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.data.repository.PasswordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginPageViewModel(
    private val passwordRepository: PasswordRepository // Por enquanto, não vamos usar, mas manter para consistência
) : ViewModel() {
    private val MASTER_USER = "admin"
    private val MASTER_PASS = "password"

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()


    fun onLoginButtonClicked(usernameInput: String,
                             passwordInput: String,
                             navigateToPasswordPage: (String) -> Unit) {
        viewModelScope.launch {
            if (usernameInput.isBlank() || passwordInput.isBlank()) {
                _loginError.value = "Usuário e senha são obrigatórios."
            } else if (usernameInput == MASTER_USER && passwordInput == MASTER_PASS) {
                _loginError.value = null
                navigateToPasswordPage(usernameInput)
            } else {
                _loginError.value = "Usuário ou senha incorretos."
            }
        }
    }

    fun clearLoginError() {
        _loginError.value = null
    }
}