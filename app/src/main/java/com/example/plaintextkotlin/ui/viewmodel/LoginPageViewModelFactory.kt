package com.example.plaintextkotlin.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.data.repository.DefaultPasswordRepository

class LoginPageViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val passwordRepository: PasswordRepository = DefaultPasswordRepository(context)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginPageViewModel::class.java)) {
            return LoginPageViewModel(passwordRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}