package com.example.plaintextkotlin.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plaintextkotlin.data.AppDatabase
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.data.repository.DefaultPasswordRepository

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val passwordDao = AppDatabase.getDatabase(context).passwordDao()
    private val passwordRepository: PasswordRepository = DefaultPasswordRepository(passwordDao)


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordPageViewModel::class.java)) {
            return PasswordPageViewModel(passwordRepository) as T
        } else if (modelClass.isAssignableFrom(AddPasswordPageViewModel::class.java)) {
            return AddPasswordPageViewModel(passwordRepository) as T
        } else if (modelClass.isAssignableFrom(LoginPageViewModel::class.java)) {
            return LoginPageViewModel(passwordRepository) as T
        } else if (modelClass.isAssignableFrom(PasswordDetailPageViewModel::class.java)) {
            return PasswordDetailPageViewModel(passwordRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}