package com.example.plaintextkotlin.ui.viewmodel

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.model.Password
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PasswordDetailPageViewModel(
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

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()

    private val _passwordDeleted = MutableStateFlow(false)
    val passwordDeleted: StateFlow<Boolean> = _passwordDeleted.asStateFlow()

    enum class SaveStatus { Idle, Saving, Success, Error }

    private var currentPasswordId: Int? = null

    fun loadDetailsIfNeeded(id: Int) {
        if (id != -1 && id != currentPasswordId) {
            currentPasswordId = id
            detailsLoaded = false
            Log.d("PasswordDetailVM", "loadDetailsIfNeeded chamado com ID: $id")
            loadPasswordDetailsInternal(id)
        } else if (!detailsLoaded && id != -1) {
            Log.d("PasswordDetailVM", "loadDetailsIfNeeded chamado com ID: $id, carregando detalhes pela primeira vez")
            loadPasswordDetailsInternal(id)
        } else {
            Log.e("PasswordDetailVM", "loadDetailsIfNeeded chamado com ID: $id. Detalhes ja carregados ou ID inválido")
        }
    }

    private var detailsLoaded = false

    private fun loadPasswordDetailsInternal(passwordId: Int) {
        viewModelScope.launch {
            Log.d("PasswordDetailVM", "loadPasswordDetailsInternal buscando ID (Int): $passwordId")
            try {
                val foundPassword = passwordRepository.getPasswordById(passwordId)
                Log.d("PasswordDetailVM", "Senha encontrada para ID $passwordId: ${foundPassword != null}")
                _password.value = foundPassword
                detailsLoaded = true

                foundPassword?.let {
                    _editableTitle.value = it.title
                    _editableUsername.value = it.username
                    _editableContent.value = it.content
                }
            } catch (e: Exception) {
                Log.e("PasswordDetailVM", "Erro ao buscar detalhes da senha", e)
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

    fun updateEditableTitle(newTitle: String) { _editableTitle.value = newTitle }
    fun updateEditableUsername(newUsername: String) { _editableUsername.value = newUsername }
    fun updateEditableContent(newContent: String) { _editableContent.value = newContent }

    fun savePasswordChanges() {
        val currentId = _password.value?.id ?: return
        val title = _editableTitle.value.trim()
        val username = _editableUsername.value.trim()
        val content = _editableContent.value

        if (title.isBlank() || username.isBlank() || content.isBlank()) {
            Log.w("PasswordDetailVM", "Tentativa de salvar senha com campos em branco")
            return
        }
        val updatedPassword = Password(
            id = currentId,
            title = title,
            username = username,
            content = content,
            imageResourceId = _password.value?.imageResourceId ?: R.drawable.item_key_novo,
        )

        viewModelScope.launch {
            _saveStatus.value = SaveStatus.Saving
            try {
                Log.d("PasswordDetailVM", "Tentativa de salvar senha com ID: $currentId")
                passwordRepository.updatePassword(updatedPassword)
                _password.value = updatedPassword
                _isEditing.value = false
                _saveStatus.value = SaveStatus.Success
                Log.d("PasswordDetailVM", "Senha salva com sucesso")
                kotlinx.coroutines.delay(1500)
                _saveStatus.value = SaveStatus.Idle
            } catch (e: Exception) {
                Log.e("PasswordDetailVM", "Erro ao salvar senha", e)
                _saveStatus.value = SaveStatus.Error
            }
        }
    }

    fun deletePassword() {
        val passwordToDelete = _password.value

        if (passwordToDelete != null) {
            viewModelScope.launch {
                try {
                    Log.d("PasswordDetailVM", "Tentativa de excluir senha com ID: ${passwordToDelete.id}")
                    passwordRepository.deletePassword(passwordToDelete)
                    _passwordDeleted.value = true
                    Log.d("PasswordDetailVM", "Senha excluída com sucesso")
                } catch (e: Exception) {
                    Log.e("PasswordDetailVM", "Erro ao excluir senha", e)
                    _saveStatus.value = SaveStatus.Error
                }
            }
        } else {
            Log.w("PasswordDetailVM", "Tentativa de excluir senha sem senha selecionada")
            _saveStatus.value = SaveStatus.Error
        }
    }

    fun resetDeletionStatus() {
        _passwordDeleted.value = false
    }

    init {
        Log.d("PasswordDetailVM", "Init - ViewModel criado.")
    }
}