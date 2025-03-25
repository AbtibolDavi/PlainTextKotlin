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

class PasswordDetailPageViewModel(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    private val _password = MutableStateFlow<Password?>(null)
    val password: StateFlow<Password?> = _password.asStateFlow()

    private var detailsLoaded = false

    fun loadDetailsIfNeeded(id: Int) {
        if (id != -1 && !detailsLoaded) {
            detailsLoaded = true
            Log.d("PasswordDetailVM", "loadDetailsIfNeeded chamado com ID: $id")
            loadPasswordDetailsInternal(id)
        } else if (detailsLoaded) {
            Log.d("PasswordDetailVM", "loadDetailsIfNeeded chamado com ID: $id, mas detalhes já foram carregados.")
        } else {
            Log.e("PasswordDetailVM", "loadDetailsIfNeeded chamado com ID inválido: $id")
        }
    }

    private fun loadPasswordDetailsInternal(passwordId: Int) {
        viewModelScope.launch {
            Log.d("PasswordDetailVM", "loadPasswordDetailsInternal buscando ID (Int): $passwordId")
            val allPasswords = passwordRepository.getPasswords()
            val foundPassword = allPasswords.find { it.titleResourceId == passwordId }
            Log.d("PasswordDetailVM", "Senha encontrada para ID $passwordId: ${foundPassword != null}")
            _password.value = foundPassword
        }
    }

    init {
        Log.d("PasswordDetailVM", "Init - ViewModel criado.")
    }
}