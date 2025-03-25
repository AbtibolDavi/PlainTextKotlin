package com.example.plaintextkotlin.ui.viewmodel

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.plaintextkotlin.data.repository.DefaultPasswordRepository
import com.example.plaintextkotlin.data.repository.PasswordRepository

class PasswordDetailPageViewModelFactory(
//    private val passwordId: String,
    context: Context,
    owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) { // Changed to AbstractSavedStateViewModelFactory and added owner parameter

    private val passwordRepository: PasswordRepository = DefaultPasswordRepository(context)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        savedStateHandle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(PasswordDetailPageViewModel::class.java)) {
            return PasswordDetailPageViewModel(passwordRepository, savedStateHandle) as T // Pass savedStateHandle here
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}