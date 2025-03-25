package com.example.plaintextkotlin.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.ui.theme.PlainTextKotlinTheme
import com.example.plaintextkotlin.ui.viewmodel.PasswordDetailPageViewModel
import com.example.plaintextkotlin.ui.viewmodel.PasswordDetailPageViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordDetailPage(
    navController: NavController,
    passwordId: Int,
    viewModel: PasswordDetailPageViewModel = viewModel(
        factory = PasswordDetailPageViewModelFactory(
            context = LocalContext.current
        )
    )
) {
    Log.d(
        "PasswordDetailPage",
        "Composable iniciado. PasswordId (Int): $passwordId"
    )

    if (passwordId == -1) {
        Log.e("PasswordDetailPage", "PasswordId inválido (-1) recebido como parâmetro. Voltando.")
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    LaunchedEffect(passwordId) {
        Log.d("PasswordDetailPage", "LaunchedEffect rodando para chamar loadDetailsIfNeeded com ID: $passwordId")
        viewModel.loadDetailsIfNeeded(passwordId)
    }

    val passwordState = viewModel.password.collectAsState()
    val password = passwordState.value

    Log.d("PasswordDetailPage", "Recompondo. Valor atual de 'password' do StateFlow: ${password?.let { stringResource(it.titleResourceId) } ?: "null"}")

    password?.let { pwd ->
        Log.d("PasswordDetailPage", "Password NÃO é null. Renderizando Scaffold com detalhes.")
        val clipboardManager = LocalClipboardManager.current
        var showPassword by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val usernameText = stringResource(pwd.usernameResourceId)
        val passwordText = stringResource(pwd.contentResourceId)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.password_detail_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Editar */ }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit))
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(pwd.imageResourceId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(pwd.titleResourceId),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Campo Login
                Surface (
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ){
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${stringResource(R.string.username_label)} $usernameText",
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(usernameText))
                            Toast.makeText(context, context.getString(R.string.login_copied), Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = stringResource(R.string.copy_login))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Campo Senha
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${stringResource(R.string.password_label)} ${
                                if (showPassword) passwordText else "*".repeat(passwordText.length)
                            }",
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPassword) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                            )
                        }
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(passwordText))
                            Toast.makeText(context, context.getString(R.string.password_copied), Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = stringResource(R.string.copy_password))
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                // Botão Excluir
                Button(
                    onClick = { /* TODO: Excluir */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(stringResource(R.string.delete_password), color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }
//        ?: run {
//        Log.d("PasswordDetailPage", "Password é null. Exibindo estado de carregamento/vazio.")
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator()
//        }
//    }
}

@Preview
@Composable
fun PasswordDetailPagePreview() {
    PlainTextKotlinTheme {
        PasswordDetailPage(
            navController = rememberNavController(),
            passwordId = R.string.password_title_1
        )
    }
}