package com.example.plaintextkotlin.ui


import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.model.Password
import com.example.plaintextkotlin.navigation.Routes
import com.example.plaintextkotlin.ui.theme.PlainTextKotlinTheme
import com.example.plaintextkotlin.ui.viewmodel.PasswordPageViewModel
import com.example.plaintextkotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordPage(navController: NavController,
                 username: String? = null,
                 viewModel: PasswordPageViewModel = viewModel(
                     factory = ViewModelFactory(
                         context = LocalContext.current
                     )
                 )
) {
    Log.d("PasswordPage", "Composable PasswordPage iniciada")
    var searchText by remember { mutableStateOf("") }
    val passwordsState = viewModel.passwords.collectAsState()
    Log.d("PasswordPage", "Estado passwordsState coletado, tamanho da lista: ${passwordsState.value.size}")
    var showWelcomeAppBar by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        delay(2000)
        showWelcomeAppBar = false
    }

    Scaffold(
        topBar = {
            AnimatedContent(
                targetState = showWelcomeAppBar,
                transitionSpec = {
                    if (targetState) {
                        fadeIn() togetherWith fadeOut()
                    } else {
                        fadeIn() togetherWith fadeOut()
                    }
                }, label = "topAppBarAnimation"
            ) { isWelcomeAppBarVisible ->
                if (isWelcomeAppBarVisible) {
                    CenterAlignedTopAppBar(
                        title = { Text("Bem-vindo, ${if (username.isNullOrEmpty()) "usuário" else username}") }
                    )
                } else {
                    CenterAlignedTopAppBar(
                        title = { Text(stringResource(R.string.password_page_title)) }
                    )
                }
            }
        },
        floatingActionButton = {
            AddPasswordButton ( onAddPasswordClick = {
                navController.navigate(Routes.ADD_PASSWORD)
            } )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            SearchBar(
                searchText = searchText,
                onSearchTextChanged = { newSearchText ->
                    searchText = newSearchText
                    viewModel.searchPasswords(newSearchText) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Log.d("PasswordPage", "Antes de verificar se a lista está vazia e chamar PasswordList/EmptyStateMessage")
            if (passwordsState.value.isEmpty()) {
                EmptyStateMessage()
                Log.d("PasswordPage", "Lista de senhas vazia, EmptyStateMessage exibida")
            } else {
                Log.d("PasswordPage", "Lista de senhas NÃO vazia, PasswordList será exibida, tamanho da lista: ${passwordsState.value.size}")
                PasswordList(
                    passwordList = passwordsState.value,
                    navController = navController
                )
                Log.d("PasswordPage", "PasswordList exibida")
            }
        }
    }
    Log.d("PasswordPage", "Fim do Composable PasswordPage")
}

@Composable
fun SearchBar(searchText: String, onSearchTextChanged: (String) -> Unit) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.search_passwords)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search)
            )
        },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun PasswordList(
    passwordList: List<Password>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Log.d("PasswordList", "Exibindo lista de senhas com ${passwordList.size} itens")
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
        ),
        exit = fadeOut(),
        modifier = modifier
    ) {
        LazyColumn(modifier = modifier) {
            itemsIndexed(passwordList) { index, password ->
                PasswordCard(
                    password = password,
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .animateEnterExit(
                            enter = slideInVertically(
                                animationSpec = spring(
                                    stiffness = Spring.StiffnessVeryLow,
                                    dampingRatio = Spring.DampingRatioLowBouncy
                                ),
                                initialOffsetY = { it * (index + 1) }
                            )
                        )
                )
            }
        }
    }


}

@Composable
fun PasswordCard(password: Password, navController: NavController, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .clickable {
                Log.d("PasswordCard", "Clicou no card: ${(password.id)}, ${password.title}")
                navController.navigate(Routes.PASSWORD_DETAILS.replace("{passwordId}", password.id.toString()))
            },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(password.imageResourceId),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = password.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = password.username,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyStateMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_password_found),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.add_new_password),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AddPasswordButton(onAddPasswordClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, end = 16.dp)
    ) {
        FloatingActionButton(
            onClick = onAddPasswordClick,
            modifier = Modifier.align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Filled.Add,
                stringResource(R.string.add_password),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview
@Composable
fun PasswordPagePreview() {
    PlainTextKotlinTheme {
        PasswordPage(rememberNavController())
    }
}

@Preview
@Composable
fun PasswordCardPreview() {
    val samplePassword = Password(id = 1, title = "Exemplo", username = "usuário", content = "senha123")
    PlainTextKotlinTheme {
        PasswordCard(samplePassword, rememberNavController())
    }
}