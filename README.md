# PlainTextKotlin - Um Gerenciador de Senhas Didático

![Linguagem](https://img.shields.io/badge/Linguagem-Kotlin-7F52FF?style=for-the-badge)
![Plataforma](https://img.shields.io/badge/Plataforma-Android-3DDC84?style=for-the-badge)
![Licença](https://img.shields.io/badge/Licença-MIT-lightgrey?style=for-the-badge)

Projeto de um gerenciador de senhas desenvolvido em Kotlin como estudo de caso para um Trabalho de Conclusão de Curso sobre **Desenvolvimento Android Moderno**. O objetivo principal deste aplicativo não é ser um produto final, mas sim servir como um guia prático e um exemplo claro da aplicação das tecnologias e arquiteturas mais recentes do ecossistema Android.

---

## ⚠️ Aviso de Segurança Crucial ⚠️

> Este aplicativo foi desenvolvido para **fins estritamente educacionais**. Ele armazena todas as senhas (incluindo a credencial mestra) em **TEXTO PLANO**, sem qualquer forma de criptografia.
>
> ## **NÃO UTILIZE ESTE APLICATIVO PARA ARMAZENAR SENHAS REAIS OU DADOS SENSÍVEIS.**

---

## ✨ Funcionalidades

-   **Autenticação Mestra:** Tela de login para acessar o cofre de senhas.
-   **Lembrar-me:** Funcionalidade para salvar as credenciais mestras e pular o login em acessos futuros.
-   **CRUD de Senhas:** Crie, leia, atualize e exclua registros de senhas (site/app, login, senha).
-   **Busca Rápida:** Filtre a lista de senhas por título, nome de usuário ou conteúdo.
-   **Copiar para a Área de Transferência:** Copie facilmente logins e senhas.
-   **Gerenciamento de Conta:** Altere o nome de usuário e a senha mestra.
-   **Tema Dinâmico:** Suporte completo a cores dinâmicas (Material You) no Android 12+.
-   **Ícones Temáticos:** O ícone do aplicativo se adapta ao tema do sistema no Android 13+.

---

## 🛠️ Tecnologias e Arquitetura

Este projeto é um exemplo prático da arquitetura e das ferramentas recomendadas pela Google para o desenvolvimento Android moderno.

-   **Linguagem:** **[Kotlin](https://kotlinlang.org/)** - 100% Kotlin, incluindo scripts Gradle (KTS).
-   **Toolkit de UI:** **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - UI totalmente declarativa.
-   **Arquitetura:** **MVVM (Model-View-ViewModel)** - Separação clara entre a lógica de UI, lógica de apresentação e dados.
-   **Injeção de Dependência:** **[Hilt](https://developer.android.com/training/dependency-injection/hilt-android)** - Para gerenciamento de dependências e ciclo de vida.
-   **Programação Assíncrona:** **[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** e **[Flow](https://kotlinlang.org/docs/flow.html)** - Para operações em background e fluxos de dados reativos.
-   **Navegação:** **[Navigation Compose](https://developer.android.com/develop/ui/compose/navigation)** - Para navegar entre as telas (`@Composable`).
-   **Persistência de Dados:**
    -   **[Room](https://developer.android.com/training/data-storage/room)**: Para armazenar a lista de senhas em um banco de dados SQLite local.
    -   **[DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)**: Para armazenar dados de chave-valor, como configurações do usuário e credenciais salvas.
-   **Tematização:** **Material Design 3** - Com suporte a temas claro/escuro e cores dinâmicas.

---

## 🚀 Como Compilar e Executar

1.  Clone este repositório:
    ```bash
    git clone https://github.com/AbtibolDavi/PlainTextKotlin.git
    ```
2.  Abra o projeto no **Android Studio** (versão **Meerkat | 2024.3.2** ou mais recente).
3.  Aguarde a sincronização do Gradle e o download das dependências.
4.  Execute o aplicativo em um emulador ou dispositivo físico (API 24+).

---

## 🎓 Conexão com o TCC

Este repositório é o código-fonte complementar do Trabalho de Conclusão de Curso intitulado **"Desenvolvimento Android Moderno com Kotlin e Jetpack Compose: Um Guia Prático Baseado no App PlainTextKotlin"**. O documento do TCC analisa e explica detalhadamente as decisões de arquitetura e implementação contidas neste código.

---

## 📝 Licença

Este projeto é distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

---

## 👨‍💻 Autor

**Davi Israel Abtibol Carvalho**