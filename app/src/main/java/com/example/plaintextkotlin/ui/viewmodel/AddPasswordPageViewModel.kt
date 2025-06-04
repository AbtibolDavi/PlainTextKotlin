package com.example.plaintextkotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.model.Password
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddPasswordPageViewModel @Inject constructor(
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
                    withContext(Dispatchers.IO) {
                        passwordRepository.insertPassword(newPassword)
                    }
                    _uiMessage.emit(R.string.password_saved_success)
                } catch (_: Exception) {
                    _uiMessage.emit(R.string.password_saved_error)
                }
            }
        }
    }
}