package com.example.plaintextkotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.navigation.Routes
import com.example.plaintextkotlin.utils.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            var destination: String? = null
            try {
                val rememberMeEnabled = preferenceManager.getRememberMeState()
                if (rememberMeEnabled) {
                    val rememberedUser = preferenceManager.getRememberMeUsername()
                    val rememberedPass = preferenceManager.getRememberMePassword()

                    if (rememberedUser != null && rememberedPass != null) {
                        val appUser = preferenceManager.getAppUsername()
                            ?: PreferenceManager.DEFAULT_APP_USERNAME
                        val appPass = preferenceManager.getAppPassword()
                            ?: PreferenceManager.DEFAULT_APP_PASSWORD

                        if (rememberedUser == appUser && rememberedPass == appPass) {
                            destination = Routes.PASSWORD_PAGE.replace("{username}", rememberedUser)
                        } else {
                            preferenceManager.clearRememberMeCredentials()
                            preferenceManager.saveRememberMeState(false)
                            destination = Routes.LOGIN
                        }
                    } else {
                        destination = Routes.LOGIN
                    }
                } else {
                    destination = Routes.LOGIN
                }
            } catch (_: Throwable) {
                destination = Routes.LOGIN
                preferenceManager.saveRememberMeState(false)
                preferenceManager.clearRememberMeCredentials()
            } finally {
                if (destination != null) {
                    _startDestination.value = destination
                } else {
                    _startDestination.value = Routes.LOGIN
                }
            }
        }
    }
}