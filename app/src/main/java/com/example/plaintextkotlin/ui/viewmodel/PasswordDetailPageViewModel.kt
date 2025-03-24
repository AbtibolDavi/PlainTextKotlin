package com.example.plaintextkotlin.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.model.Password
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PasswordDetailPageViewModel(
    private val passwordRepository: PasswordRepository,
    savedStateHandle: SavedStateHandle // Para pegar argumentos da navegação
) : ViewModel() {

    private val passwordId: String = savedStateHandle["passwordId"] ?: "" // Pega o passwordId do SavedStateHandle

    private val _password = MutableStateFlow<Password?>(null)
    val password: StateFlow<Password?> = _password.asStateFlow()

    init {
        loadPasswordDetails(passwordId)
    }

    private fun loadPasswordDetails(passwordId: String) {
        viewModelScope.launch {
            // TODO: Implementar lógica para buscar os detalhes da senha pelo ID no repositório.
            // Por enquanto, vamos simular buscando a lista completa e encontrando pelo titleResourceId.
            val allPasswords = passwordRepository.getPasswords()
            _password.value = allPasswords.find { it.titleResourceId.toString() == passwordId }
        }
    }
}