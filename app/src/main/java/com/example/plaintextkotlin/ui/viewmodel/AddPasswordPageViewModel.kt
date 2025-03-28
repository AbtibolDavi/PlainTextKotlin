package com.example.plaintextkotlin.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.data.repository.PasswordRepository
import kotlinx.coroutines.launch
import com.example.plaintextkotlin.model.Password
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext

class AddPasswordPageViewModel(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    private val _uiMessage = MutableSharedFlow<Int>()
    val uiMessage: SharedFlow<Int> = _uiMessage.asSharedFlow()

    fun onSavePasswordClicked(
        title: String,
        username: String,
        passwordContent: String,
    ) {
        val trimmedTitle = title.trim()
        val trimmedUsername = username.trim()

        if (trimmedTitle.isNotBlank() && trimmedUsername.isNotBlank() && passwordContent.isNotBlank()) {
            val newPassword = Password(
                title = trimmedTitle.trim(),
                username = trimmedUsername.trim(),
                content = passwordContent
            )
            viewModelScope.launch {
                try {
                    Log.d("AddPasswordPageViewModel", "Tentando inserir senha ${newPassword.title}")
                    withContext(Dispatchers.IO) {
                        passwordRepository.insertPassword(newPassword)
                    }
//                    passwordRepository.insertPassword(newPassword)
                    _uiMessage.emit(R.string.password_saved_success)
                    Log.d(
                        "AddPasswordPageViewModel",
                        "Senha inserida com sucesso ${newPassword.title}"
                    )
                } catch (e: Exception) {
                    Log.e("AddPasswordPageViewModel", "Erro ao inserir senha", e)
                    try { _uiMessage.emit(R.string.password_saved_error) } catch (_: Exception) {}
                }
            }
        } else {
            Log.w("AddPasswordPageViewModel", "Campos vazios ou em branco")
        }
    }
}