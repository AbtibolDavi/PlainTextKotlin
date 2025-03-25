package com.example.plaintextkotlin.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.model.Password
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PasswordPageViewModel(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    private val _passwords = MutableStateFlow<List<Password>>(emptyList())
    val passwords: StateFlow<List<Password>> = _passwords.asStateFlow()

    init {
        Log.d("PasswordPageViewModel", "ViewModel PasswordPageViewModel inicializado")
        loadPasswords()
    }

    private fun loadPasswords() {
        viewModelScope.launch {
            Log.d("PasswordPageViewModel", "Iniciando loadPasswords()")
            _passwords.value = passwordRepository.getPasswords()
            Log.d("PasswordPageViewModel", "Senhas carregadas, tamanho da lista: ${_passwords.value.size}")
            Log.d("PasswordPageViewModel", "Finalizando loadPasswords()")
        }
    }

    fun searchPasswords(query: String) {
        viewModelScope.launch {
            Log.d("PasswordPageViewModel", "Iniciando searchPasswords(), query: $query")
            _passwords.value = passwordRepository.searchPasswords(query)
            Log.d("PasswordPageViewModel", "Query não vazia, exibindo senhas filtradas, tamanho da lista: ${_passwords.value.size}")
            Log.d("PasswordPageViewModel", "Finalizando searchPasswords(), query: $query")
        }
    }
}