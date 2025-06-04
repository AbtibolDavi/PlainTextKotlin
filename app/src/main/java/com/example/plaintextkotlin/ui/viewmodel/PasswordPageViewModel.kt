package com.example.plaintextkotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.model.Password
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordPageViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository
) : ViewModel() {
    private val _passwords = MutableStateFlow<List<Password>>(emptyList())
    val passwords: StateFlow<List<Password>> = _passwords.asStateFlow()

    private var searchJob: Job? = null
    private var currentQuery: String = ""

    init {
        loadPasswords()
    }

    private fun loadPasswords() {
        viewModelScope.launch {
            passwordRepository.getPasswords().collectLatest { passwordList ->
                if (currentQuery.isBlank()) {
                    _passwords.value = passwordList
                }
            }
        }
    }

    fun searchPasswords(query: String) {
        searchJob?.cancel()
        currentQuery = query.trim()

        if (query.isBlank()) {
            viewModelScope.launch {
                passwordRepository.getPasswords().collectLatest { _passwords.value = it }
            }
            return
        }

        searchJob = viewModelScope.launch {
            passwordRepository.searchPasswords(query).collectLatest { searchResult ->
                _passwords.value = searchResult
            }
        }
    }
}