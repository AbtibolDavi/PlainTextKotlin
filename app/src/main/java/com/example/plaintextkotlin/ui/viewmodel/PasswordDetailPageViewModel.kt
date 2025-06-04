package com.example.plaintextkotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.model.Password
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PasswordDetailPageViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository
) : ViewModel() {
    private val _password = MutableStateFlow<Password?>(null)
    val password: StateFlow<Password?> = _password.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _editableTitle = MutableStateFlow("")
    val editableTitle: StateFlow<String> = _editableTitle.asStateFlow()

    private val _editableUsername = MutableStateFlow("")
    val editableUsername: StateFlow<String> = _editableUsername.asStateFlow()

    private val _editableContent = MutableStateFlow("")
    val editableContent: StateFlow<String> = _editableContent.asStateFlow()

    enum class SaveStatus { Idle, Saving, Success, Error }

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()

    private val _uiMessage = MutableSharedFlow<Int>()
    val uiMessage: SharedFlow<Int> = _uiMessage.asSharedFlow()

    private var currentPasswordId: Int? = null
    private var detailsLoaded = false

    fun loadDetailsIfNeeded(id: Int) {
        if (id != -1 && id != currentPasswordId) {
            currentPasswordId = id
            detailsLoaded = false
            loadPasswordDetailsInternal(id)
        } else if (!detailsLoaded && id != -1) {
            loadPasswordDetailsInternal(id)
        }
    }

    private fun loadPasswordDetailsInternal(passwordId: Int) {
        viewModelScope.launch {
            try {
                val foundPassword = passwordRepository.getPasswordById(passwordId)
                _password.value = foundPassword
                detailsLoaded = true

                foundPassword?.let {
                    _editableTitle.value = it.title
                    _editableUsername.value = it.username
                    _editableContent.value = it.content
                }
            } catch (_: Exception) {
                _saveStatus.value = SaveStatus.Error
                detailsLoaded = false
            }
        }
    }

    fun toggleEditMode() {
        val currentlyEditing = _isEditing.value
        _isEditing.value = !currentlyEditing

        if (!currentlyEditing) {
            _saveStatus.value = SaveStatus.Idle
            _password.value?.let {
                _editableTitle.value = it.title
                _editableUsername.value = it.username
                _editableContent.value = it.content
            }
        }
    }

    fun updateEditableTitle(newTitle: String) {
        _editableTitle.value = newTitle
    }

    fun updateEditableUsername(newUsername: String) {
        _editableUsername.value = newUsername
    }

    fun updateEditableContent(newContent: String) {
        _editableContent.value = newContent
    }

    fun savePasswordChanges() {
        val currentId = _password.value?.id ?: return
        val title = _editableTitle.value.trim()
        val username = _editableUsername.value.trim()
        val content = _editableContent.value

        if (title.isBlank() || username.isBlank() || content.isBlank()) {
            viewModelScope.launch {
                _uiMessage.emit(R.string.fill_required_fields)
            }
            return
        }
        val updatedPassword = Password(
            id = currentId,
            title = title,
            username = username,
            content = content,
            imageResourceId = _password.value?.imageResourceId ?: R.drawable.item_key_foreground,
        )

        viewModelScope.launch {
            _saveStatus.value = SaveStatus.Saving
            try {
                withContext(Dispatchers.IO) {
                    passwordRepository.updatePassword(updatedPassword)
                }
                _password.value = updatedPassword
                _isEditing.value = false
                _saveStatus.value = SaveStatus.Success
                _uiMessage.emit(R.string.password_saved_success)
            } catch (_: Exception) {
                _saveStatus.value = SaveStatus.Error
                _uiMessage.emit(R.string.password_saved_error)
            } finally {
                if (_saveStatus.value != SaveStatus.Saving) {
                    kotlinx.coroutines.delay(1000)
                    _saveStatus.value = SaveStatus.Idle
                }
            }
        }
    }

    fun deletePassword() {
        val passwordToDelete = _password.value

        if (passwordToDelete != null) {
            viewModelScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        passwordRepository.deletePassword(passwordToDelete)
                    }
                    _uiMessage.emit(R.string.successful_deleted_pwd)
                } catch (_: Exception) {
                    _uiMessage.emit(R.string.password_delete_error)
                }
            }
        } else {
            viewModelScope.launch {
                _uiMessage.emit(R.string.password_delete_error)
            }
        }
    }
}