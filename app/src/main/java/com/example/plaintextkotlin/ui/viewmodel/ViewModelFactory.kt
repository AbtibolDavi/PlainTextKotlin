package com.example.plaintextkotlin.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plaintextkotlin.data.AppDatabase
import com.example.plaintextkotlin.data.repository.DefaultPasswordRepository
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.utils.UserDataStoreManager

class ViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val passwordDao = AppDatabase.getDatabase(context).passwordDao()
    private val passwordRepository: PasswordRepository = DefaultPasswordRepository(passwordDao)
    private val userDataStoreManager: UserDataStoreManager =
        UserDataStoreManager(context.applicationContext)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PasswordPageViewModel::class.java) -> {
                PasswordPageViewModel(passwordRepository) as T
            }

            modelClass.isAssignableFrom(AddPasswordPageViewModel::class.java) -> {
                AddPasswordPageViewModel(passwordRepository) as T
            }

            modelClass.isAssignableFrom(LoginPageViewModel::class.java) -> {
                LoginPageViewModel(userDataStoreManager) as T
            }

            modelClass.isAssignableFrom(PasswordDetailPageViewModel::class.java) -> {
                PasswordDetailPageViewModel(passwordRepository) as T
            }

            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(userDataStoreManager) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(userDataStoreManager) as T
            }

            else -> throw IllegalArgumentException("Classe ViewModel desconhecida. ${modelClass.name}")
        }
    }
}