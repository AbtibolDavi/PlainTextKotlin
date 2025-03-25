package com.example.plaintextkotlin.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.data.repository.DefaultPasswordRepository

class PasswordPageViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val passwordRepository: PasswordRepository = DefaultPasswordRepository(context)


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordPageViewModel::class.java)) {
            return PasswordPageViewModel(passwordRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}