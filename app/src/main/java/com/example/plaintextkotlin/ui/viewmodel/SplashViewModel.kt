package com.example.plaintextkotlin.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.navigation.Routes
import com.example.plaintextkotlin.utils.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel (
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        Log.d("SplashViewModel", "Init: Verificando auto-login")
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            var destination: String? = null
            try {
                val rememberMeEnabled = preferenceManager.getRememberMeState()
                Log.d("SplashViewModel", "Remember Me Enabled: $rememberMeEnabled")
                if (rememberMeEnabled) {
                    val rememberedUser = preferenceManager.getRememberMeUsername()
                    val rememberedPass = preferenceManager.getRememberMePassword()
                    Log.d(
                        "SplashViewModel",
                        "Remembered User: $rememberedUser Remembered Pass: $rememberedPass"
                    )

                    if (rememberedUser != null && rememberedPass != null) {
                        val appUser = preferenceManager.getAppUsername()
                            ?: PreferenceManager.DEFAULT_APP_USERNAME
                        val appPass = preferenceManager.getAppPassword()
                            ?: PreferenceManager.DEFAULT_APP_PASSWORD

                        if (rememberedUser == appUser && rememberedPass == appPass) {
                            Log.d("SplashViewModel", "Auto-login bem-sucedido")
                            destination =
                                Routes.PASSWORD_PAGE.replace("{username}", rememberedUser)
                        } else {
                            Log.d("SplashViewModel", "Auto-login falhou")
                            preferenceManager.clearRememberMeCredentials()
                            preferenceManager.saveRememberMeState(false)
                            destination = Routes.LOGIN
                        }
                    } else {
                        Log.d(
                            "SplashViewModel",
                            "Remember me desativado. Redirecionando para a tela de login."
                        )
                        destination = Routes.LOGIN
                    }
                } else {
                    Log.d("SplashViewModel", "Auto-login desativado. Redirecionando para a tela de login.")
                    destination = Routes.LOGIN
                }
            } catch (e: Throwable) {
                Log.e("SplashViewModel", "Erro ao verificar auto-login", e)
                destination = Routes.LOGIN
                try {
                    preferenceManager.saveRememberMeState(false)
                    preferenceManager.clearRememberMeCredentials()
                } catch (cleanupError: Throwable) {
                    Log.e("SplashViewModel", "checkAutoLogin - erro ao limpar prefs no catch", cleanupError)
                }
            } finally {
                Log.d("SplashViewModel", "checkAutoLogin - Dentro do FINALLY. Destino determinado: $destination")
                if (destination != null) {
                    _startDestination.value = destination
                    Log.d("SplashViewModel", "checkAutoLogin - _startDestination ATUALIZADO para: $destination")
                } else {
                    // Segurança extra: Se destination ainda for null (não deveria acontecer), ir para Login
                    Log.e("SplashViewModel", "checkAutoLogin - FINALLY: Destino ainda nulo! Forçando LOGIN.")
                    _startDestination.value = Routes.LOGIN
                }
            }
        }
    }
}