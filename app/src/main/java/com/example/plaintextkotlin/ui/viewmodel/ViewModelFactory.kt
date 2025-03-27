package com.example.plaintextkotlin.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plaintextkotlin.data.AppDatabase
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.data.repository.DefaultPasswordRepository
import com.example.plaintextkotlin.utils.PreferenceManager

class ViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val passwordDao = AppDatabase.getDatabase(context).passwordDao()
    private val passwordRepository: PasswordRepository = DefaultPasswordRepository(passwordDao)

    private val preferenceManager: PreferenceManager = PreferenceManager(context.applicationContext)


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
                LoginPageViewModel(preferenceManager) as T
            }
            modelClass.isAssignableFrom(PasswordDetailPageViewModel::class.java) -> {
                PasswordDetailPageViewModel(passwordRepository) as T
            }
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(preferenceManager) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(preferenceManager) as T
            }
            else -> throw IllegalArgumentException("Classe ViewModel desconhecida. ${modelClass.name}")
        }
    }
}