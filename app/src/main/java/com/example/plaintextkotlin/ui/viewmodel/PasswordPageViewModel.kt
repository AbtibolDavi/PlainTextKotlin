package com.example.plaintextkotlin.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.model.Password
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PasswordPageViewModel(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    private val _passwords = MutableStateFlow<List<Password>>(emptyList())
    val passwords: StateFlow<List<Password>> = _passwords.asStateFlow()

    private var searchJob: Job? = null
    private var currentQuery: String = ""

    init {
        Log.d("PasswordPageViewModel", "ViewModel PasswordPageViewModel inicializado")
        loadPasswords()
    }

    private fun loadPasswords() {
        viewModelScope.launch {
            Log.d("PasswordPageViewModel", "Iniciando loadPasswords()")
            passwordRepository.getPasswords()
                .catch { exception ->
                    Log.e("PasswordPageViewModel", "Erro ao carregar senhas", exception)
                }
                .collectLatest { passwordList ->
                    if (currentQuery.isBlank()) {
                        _passwords.value = passwordList
                        Log.d("PasswordPageViewModel", "Senhas carregadas, tamanho da lista: ${passwordList.size}")
                    }
                }
        }
    }

    fun searchPasswords(query: String) {
        searchJob?.cancel()
        currentQuery = query.trim()

        if (query.isBlank()) {
            Log.d("PasswordPageViewModel", "Query vazia, carregando todas as senhas")
            viewModelScope.launch {
                passwordRepository.getPasswords().collectLatest { _passwords.value = it }
            }
            return
        }

        searchJob = viewModelScope.launch {
            Log.d("PasswordPageViewModel", "Iniciando searchPasswords(), query: $query")
            passwordRepository.searchPasswords(query)
                .catch { exception ->
                    Log.e("PasswordPageViewModel", "Erro ao buscar senhas", exception)
                }
                .collectLatest { searchResult ->
                    _passwords.value = searchResult
                    Log.d("PasswordPageViewModel", "Senhas filtradas carregadas, tamanho da lista: ${searchResult.size}")
                }
        }
    }
}