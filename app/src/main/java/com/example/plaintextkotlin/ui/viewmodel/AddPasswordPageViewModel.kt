package com.example.plaintextkotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.data.repository.PasswordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddPasswordPageViewModel(
    private val passwordRepository: PasswordRepository // Também não usado diretamente agora, mas para consistência
) : ViewModel() {

    private val _isPasswordSaved = MutableStateFlow(false)
    val isPasswordSaved: StateFlow<Boolean> = _isPasswordSaved.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    fun onSavePasswordClicked(
        title: String,
        username: String,
        password: String,
        onPasswordSaved: () -> Unit // Lambda para notificar a UI que a senha foi salva
    ) {
        viewModelScope.launch {
            // TODO: Implementar a lógica para salvar a senha no repositório de dados.
            // Por enquanto, vamos apenas simular o salvamento e notificar a UI.
            if (title.isNotBlank() && username.isNotBlank() && password.isNotBlank()) {
                // Simulação de salvamento bem-sucedido
                _isPasswordSaved.value = true
                onPasswordSaved() // Notifica a UI que a senha foi salva
            } else {
                // Simulação de falha no salvamento (campos inválidos)
                _isFormValid.value = false // Ou você pode ter um StateFlow de erro mais específico
            }
        }
    }

    fun resetIsPasswordSaved() {
        _isPasswordSaved.value = false
    }

    fun validateForm(title: String, username: String, password: String) {
        _isFormValid.value = title.isNotBlank() && username.isNotBlank() && password.isNotBlank()
    }
}