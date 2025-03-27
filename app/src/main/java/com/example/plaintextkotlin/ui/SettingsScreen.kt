package com.example.plaintextkotlin.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.navigation.Routes
import com.example.plaintextkotlin.ui.theme.PlainTextKotlinTheme
import com.example.plaintextkotlin.ui.viewmodel.SettingsViewModel
import com.example.plaintextkotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope() // Usar escopo da composição para lançar Snackbar

    // Coleta de estados do ViewModel
    val newUsername by viewModel.newUsername.collectAsState()
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    // Coleta estados de loading separados
    val isSavingUsername by viewModel.isSavingUsername.collectAsState()
    val isSavingPassword by viewModel.isSavingPassword.collectAsState()
    val isLoggingOut by viewModel.isLoggingOut.collectAsState()

    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    // Habilita/desabilita geral baseado se ALGUMA operação está em andamento
    val isAnyOperationLoading = isSavingUsername || isSavingPassword || isLoggingOut

    // Observador para mensagens da UI (Snackbar)
    LaunchedEffect(viewModel.uiMessage, snackbarHostState) {
        viewModel.uiMessage.collectLatest { message ->
            // Lança showSnackbar no escopo da composição para segurança
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    // Observador para evento de navegação de Logout
    LaunchedEffect(viewModel.navigateToLogin, navController) {
        viewModel.navigateToLogin.collectLatest {
            navController.navigate(Routes.LOGIN) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
            // Mostrar Toast após navegação pode ser problemático se a tela destino demorar
            // Considerar mostrar o Toast ANTES de navegar ou usar um Snackbar na tela de Login
            // Toast.makeText(context, "Logout efetuado.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = { if (!isAnyOperationLoading) navController.popBackStack() }) { // Previne voltar durante loading
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- AVISO DE SEGURANÇA ---
            Text(
                text = "AVISO: As credenciais mestras são armazenadas localmente em texto plano. Isso é TOTALMENTE SUPER seguro!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- Seção Alterar Login Mestre ---
            Text("Alterar Login Mestre", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newUsername,
                onValueChange = viewModel::updateNewUsername,
                label = { Text("Novo Nome de Usuário") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isAnyOperationLoading // Desabilita se qualquer operação estiver ativa
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = viewModel::saveNewUsername,
                // Habilitado apenas se campo não vazio E nenhuma operação em andamento
                enabled = newUsername.isNotBlank() && !isAnyOperationLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Mostra loading APENAS se ESTA operação específica estiver ativa
                if (isSavingUsername) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Salvar Novo Login")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Seção Alterar Senha Mestra ---
            Text("Alterar Senha Mestra", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField( // Nova Senha
                value = newPassword,
                onValueChange = viewModel::updateNewPassword,
                label = { Text("Nova Senha") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showNewPassword = !showNewPassword }) {
                        Icon(
                            imageVector = if (showNewPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (showNewPassword) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                        )
                    }
                },
                enabled = !isAnyOperationLoading // Desabilita se qualquer operação estiver ativa
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField( // Confirmar Nova Senha
                value = confirmPassword,
                onValueChange = viewModel::updateConfirmPassword,
                label = { Text("Confirmar Nova Senha") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Icon(
                            imageVector = if (showNewPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (showNewPassword) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                        )
                    }
                },
                enabled = !isAnyOperationLoading, // Desabilita se qualquer operação estiver ativa
                isError = newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = viewModel::saveNewPassword,
                // Habilitado apenas se campos válidos E nenhuma operação em andamento
                enabled = newPassword.isNotBlank() && confirmPassword.isNotBlank() && newPassword == confirmPassword && !isAnyOperationLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Mostra loading APENAS se ESTA operação específica estiver ativa
                if (isSavingPassword) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Salvar Nova Senha")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Seção Conta ---
            Text("Conta", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = viewModel::logout,
                // Desabilita se qualquer operação estiver ativa
                enabled = !isAnyOperationLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                // Mostra loading APENAS se ESTA operação específica estiver ativa
                if (isLoggingOut) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.error)
                } else {
                    Text("Sair / Logout")
                }
            }
        } // Fim da Column
    } // Fim do Scaffold
} // Fim do Composable

// Preview não terá acesso real ao ViewModel/Prefs, apenas layout
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    PlainTextKotlinTheme {
        // Simula um estado de loading para preview, se necessário
        // SettingsScreen(navController = rememberNavController(), viewModel = fakeViewModel)
        SettingsScreen(navController = rememberNavController())
    }
}