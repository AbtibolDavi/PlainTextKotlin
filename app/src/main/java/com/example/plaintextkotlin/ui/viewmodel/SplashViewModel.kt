package com.example.plaintextkotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plaintextkotlin.navigation.Routes
import com.example.plaintextkotlin.utils.UserDataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userDataStoreManager: UserDataStoreManager
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
                val rememberMeEnabled = userDataStoreManager.getRememberMeStateOnce()
                if (rememberMeEnabled) {
                    val rememberedUser = userDataStoreManager.getRememberMeUsernameOnce()
                    val rememberedPass = userDataStoreManager.getRememberMePasswordOnce()

                    if (rememberedUser != null && rememberedPass != null) {
                        val appUser = userDataStoreManager.getAppUsernameOnce()
                        val appPass = userDataStoreManager.getAppPasswordOnce()

                        if (rememberedUser == appUser && rememberedPass == appPass) {
                            destination = Routes.PASSWORD_PAGE.replace("{username}", rememberedUser)
                        } else {
                            userDataStoreManager.clearRememberMeCredentials()
                            userDataStoreManager.saveRememberMeState(false)
                            destination = Routes.LOGIN
                        }
                    } else {
                        userDataStoreManager.clearRememberMeCredentials()
                        userDataStoreManager.saveRememberMeState(false)
                        destination = Routes.LOGIN
                    }
                } else {
                    destination = Routes.LOGIN
                }
            } catch (_: Throwable) {
                destination = Routes.LOGIN
                userDataStoreManager.saveRememberMeState(false)
                userDataStoreManager.clearRememberMeCredentials()
            } finally {
                _startDestination.value = destination ?: Routes.LOGIN
            }
        }
    }
}