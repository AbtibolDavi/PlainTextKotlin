package com.example.plaintextkotlin.ui.viewmodel

import android.R.attr.password
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.data.repository.PasswordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.plaintextkotlin.model.Password

class AddPasswordPageViewModel(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    private val _isPasswordSaved = MutableStateFlow(false)
    val isPasswordSaved: StateFlow<Boolean> = _isPasswordSaved.asStateFlow()

    fun onSavePasswordClicked(
        title: String,
        username: String,
        passwordContent: String,
    ) {
        if (title.isNotBlank() && username.isNotBlank() && passwordContent.isNotBlank()) {
            val newPassword = Password(
                title = title.trim(),
                username = username.trim(),
                content = passwordContent
            )
            viewModelScope.launch {
                try {
                    Log.d("AddPasswordPageViewModel", "Tentando inserir senha ${newPassword.title}")
                    passwordRepository.insertPassword(newPassword)
                    _isPasswordSaved.value = true
                    Log.d(
                        "AddPasswordPageViewModel",
                        "Senha inserida com sucesso ${newPassword.title}"
                    )
                } catch (e: Exception) {
                    Log.e("AddPasswordPageViewModel", "Erro ao inserir senha", e)
                }
            }
        } else {
            Log.w("AddPasswordPageViewModel", "Campos vazios ou em branco")
        }
    }

    fun resetIsPasswordSaved() {
        _isPasswordSaved.value = false
    }
}