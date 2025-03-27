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
import android.util.Log

class SettingsViewModel(
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    // --- Estados para os Campos de Texto ---
    private val _newUsername = MutableStateFlow("")
    val newUsername: StateFlow<String> = _newUsername.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    // --- Estados de UI ---
    private val _uiMessage = MutableSharedFlow<String>() // Para Snackbar/Toast
    val uiMessage: SharedFlow<String> = _uiMessage.asSharedFlow()

    // Estados de Loading Separados para cada ação assíncrona
    private val _isSavingUsername = MutableStateFlow(false)
    val isSavingUsername: StateFlow<Boolean> = _isSavingUsername.asStateFlow()

    private val _isSavingPassword = MutableStateFlow(false)
    val isSavingPassword: StateFlow<Boolean> = _isSavingPassword.asStateFlow()

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    // --- Evento de Navegação ---
    private val _navigateToLogin = MutableSharedFlow<Unit>()
    val navigateToLogin: SharedFlow<Unit> = _navigateToLogin.asSharedFlow()

    // --- Funções de Atualização de Estado ---
    fun updateNewUsername(input: String) { _newUsername.value = input }
    fun updateNewPassword(input: String) { _newPassword.value = input }
    fun updateConfirmPassword(input: String) { _confirmPassword.value = input }

    // --- Lógica de Salvamento ---
    fun saveNewUsername() {
        val usernameToSave = _newUsername.value.trim()

        if (usernameToSave.isBlank()) {
            viewModelScope.launch { _uiMessage.emit("Nome de usuário não pode ser vazio.") }
            return
        }
        // Opcional: Adicionar validação se username já existe ou é igual ao atual

        Log.d("SettingsViewModel", "Iniciando saveNewUsername...")
        viewModelScope.launch {
            _isSavingUsername.value = true // Inicia loading específico
            Log.d("SettingsViewModel", "isSavingUsername definido como true")
            try {
                // Lê a senha atual ANTES de salvar
                val currentPassword = preferenceManager.getAppPassword() ?: PreferenceManager.DEFAULT_APP_PASSWORD
                Log.d("SettingsViewModel", "saveNewUsername - Senha atual lida: ${currentPassword.isNotEmpty()}")

                // Salva novo username com senha atual
                preferenceManager.saveAppCredentials(usernameToSave, currentPassword)
                Log.d("SettingsViewModel", "saveNewUsername - saveAppCredentials chamado.")

                // Limpa auto-login
                preferenceManager.clearRememberMeCredentials()
                preferenceManager.saveRememberMeState(false)
                Log.d("SettingsViewModel", "saveNewUsername - Auto-login limpo.")

                // Emite mensagem de sucesso
                Log.d("SettingsViewModel", "saveNewUsername - ANTES de emit")
                _uiMessage.emit("Nome de usuário atualizado com sucesso!")
                Log.d("SettingsViewModel", "saveNewUsername - DEPOIS de emit")

                // Limpa campo na UI
                _newUsername.value = ""

            } catch (e: Exception) {
                Log.e("SettingsViewModel", "ERRO no TRY de saveNewUsername", e)
                try { _uiMessage.emit("Erro ao atualizar nome de usuário.") } catch (_: Exception) {} // Tenta emitir erro
            } finally {
                Log.d("SettingsViewModel", "saveNewUsername - INÍCIO DO FINALLY")
                _isSavingUsername.value = false // Finaliza loading específico
                Log.d("SettingsViewModel", "isSavingUsername definido como false")
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

        Log.d("SettingsViewModel", "Iniciando saveNewPassword...")
        viewModelScope.launch {
            _isSavingPassword.value = true // Inicia loading específico
            Log.d("SettingsViewModel", "isSavingPassword definido como true")
            try {
                // Lê username atual ANTES de salvar
                val currentUsername = preferenceManager.getAppUsername() ?: PreferenceManager.DEFAULT_APP_USERNAME
                Log.d("SettingsViewModel", "saveNewPassword - Username atual lido: $currentUsername")

                // Salva username atual com nova senha
                preferenceManager.saveAppCredentials(currentUsername, newPass) // INSEGURO - TEXTO PLANO
                Log.d("SettingsViewModel", "saveNewPassword - saveAppCredentials chamado.")

                // Limpa auto-login
                preferenceManager.clearRememberMeCredentials()
                preferenceManager.saveRememberMeState(false)
                Log.d("SettingsViewModel", "saveNewPassword - Auto-login limpo.")

                // Emite mensagem de sucesso
                Log.d("SettingsViewModel", "saveNewPassword - ANTES de emit")
                _uiMessage.emit("Senha atualizada com sucesso!")
                Log.d("SettingsViewModel", "saveNewPassword - DEPOIS de emit")

                // Limpa campos na UI
                _newPassword.value = ""
                _confirmPassword.value = ""

            } catch (e: Exception) {
                Log.e("SettingsViewModel", "ERRO no TRY de saveNewPassword", e)
                try { _uiMessage.emit("Erro ao atualizar senha.") } catch (_: Exception) {} // Tenta emitir erro
            } finally {
                Log.d("SettingsViewModel", "saveNewPassword - INÍCIO DO FINALLY")
                _isSavingPassword.value = false // Finaliza loading específico
                Log.d("SettingsViewModel", "isSavingPassword definido como false")
            }
        }
    }

    // --- Logout ---
    fun logout() {
        Log.d("SettingsViewModel", "Iniciando logout...")
        viewModelScope.launch {
            _isLoggingOut.value = true // Inicia loading específico
            Log.d("SettingsViewModel", "isLoggingOut definido como true")
            try {
                preferenceManager.clearRememberMeCredentials()
                preferenceManager.saveRememberMeState(false)
                Log.d("SettingsViewModel", "Logout: Credenciais de Auto-Login limpas.")
                _navigateToLogin.emit(Unit) // Emite evento para navegação
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "ERRO durante logout", e)
                try { _uiMessage.emit("Erro ao fazer logout.") } catch (_: Exception) {}
            } finally {
                Log.d("SettingsViewModel", "Logout - INÍCIO DO FINALLY")
                _isLoggingOut.value = false // Finaliza loading específico
                Log.d("SettingsViewModel", "isLoggingOut definido como false")
            }
        }
    }
}