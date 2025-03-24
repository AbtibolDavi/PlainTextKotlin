package com.example.plaintextkotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.plaintextkotlin.data.repository.PasswordRepository

class LoginPageViewModel(
    private val passwordRepository: PasswordRepository // Por enquanto, não vamos usar, mas manter para consistência
) : ViewModel() {

    // Funções para processar eventos da UI, como cliques no botão de login, podem ser adicionadas aqui.
    // Neste ponto, para o login simples que você tem, talvez não precisemos de StateFlows.
    // Se precisarmos gerenciar o estado da tela de login (ex: loading, erro), adicionaremos depois.

    fun onLoginButtonClicked(username: String, navigateToPasswordPage: (String) -> Unit) {
        // Aqui você pode adicionar lógica de validação de login se necessário.
        // Por enquanto, vamos apenas navegar para a PasswordPage.
        navigateToPasswordPage(username)
    }
}