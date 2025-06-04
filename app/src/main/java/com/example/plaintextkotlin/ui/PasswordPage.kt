package com.example.plaintextkotlin.ui


import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.model.Password
import com.example.plaintextkotlin.navigation.Routes
import com.example.plaintextkotlin.ui.theme.PlainTextKotlinTheme
import com.example.plaintextkotlin.ui.viewmodel.PasswordPageViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordPage(
    navController: NavController,
    username: String? = null,
    viewModel: PasswordPageViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf("") }
    val passwordsState = viewModel.passwords.collectAsState()
    var showWelcomeAppBar by remember { mutableStateOf(true) }
    val welcomeMessageUsername =
        username?.takeIf { it.isNotEmpty() } ?: stringResource(R.string.default_username_display)

    var showAnimatedTitle by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        searchText = ""
        viewModel.searchPasswords(searchText)
    }

    LaunchedEffect(Unit) {
        delay(2000)
        showWelcomeAppBar = false
        delay(300)
        showAnimatedTitle = true
    }

    Scaffold(topBar = {
        AnimatedContent(
            targetState = showWelcomeAppBar, transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            }) { isWelcomeAppBarVisible ->
            if (isWelcomeAppBarVisible) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(
                                R.string.welcome_user, welcomeMessageUsername
                            )
                        )
                    })
            } else {
                AnimatedPlainTextTitleAppBar(
                    navController = navController, startAnimation = showAnimatedTitle
                )
            }
        }
    }, floatingActionButton = {
        AddPasswordButton(onAddPasswordClick = {
            navController.navigate(Routes.ADD_PASSWORD)
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            SearchBar(
                searchText = searchText, onSearchTextChanged = { newSearchText ->
                    searchText = newSearchText
                    viewModel.searchPasswords(newSearchText)
                })
            Spacer(modifier = Modifier.height(16.dp))
            if (passwordsState.value.isEmpty() && searchText.isEmpty()) {
                InfoMessage(
                    titleResId = R.string.no_password_found, bodyResId = R.string.add_new_password
                )
            } else if (passwordsState.value.isEmpty() && searchText.isNotEmpty()) {
                InfoMessage(
                    titleResId = R.string.no_passwords_query,
                    bodyResId = R.string.try_another_query,
                    searchText
                )
            } else {
                PasswordList(
                    passwordList = passwordsState.value, navController = navController
                )
            }
        }
    }
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
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                IconButton(onClick = { onSearchTextChanged("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.clear_search)
                    )
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.surface
        ),
        singleLine = true
    )
}

@Composable
fun PasswordList(
    passwordList: List<Password>, navController: NavController, modifier: Modifier = Modifier
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    AnimatedVisibility(
        visibleState = visibleState, enter = fadeIn(
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
        ), exit = fadeOut(), modifier = modifier
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
                                ), initialOffsetY = { it * (index + 1) })
                        )
                )
            }
        }
    }


}

@Composable
fun PasswordCard(password: Password, navController: NavController, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clickable {
            navController.navigate(
                Routes.PASSWORD_DETAILS.replace(
                    "{passwordId}", password.id.toString()
                )
            )
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
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(password.imageResourceId),
                    contentDescription = stringResource(R.string.password_icon_desc),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(45.dp)
                )
            }
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
fun InfoMessage(
    @StringRes titleResId: Int,
    @StringRes bodyResId: Int,
    vararg formatArgs: Any,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(titleResId),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(bodyResId, *formatArgs),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedPlainTextTitleAppBar(
    navController: NavController, startAnimation: Boolean
) {
    var textVisible by remember { mutableStateOf(false) }
    var animatedTextLength by remember { mutableIntStateOf(0) }
    var showIcon by remember { mutableStateOf(false) }
    val targetText = "plaintext"
    val boldLength = "plain".length

    LaunchedEffect(startAnimation) {
        if (startAnimation) {
            showIcon = true
            delay(500)
            textVisible = true
            for (i in 1..targetText.length) {
                animatedTextLength = i
                delay(80)
            }
        } else {
            showIcon = false
            textVisible = false
            animatedTextLength = 0
        }
    }

    CenterAlignedTopAppBar(title = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showIcon, enter = fadeIn(animationSpec = tween(500))
            ) {
                Image(
                    painter = painterResource(R.drawable.plain_text),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }


            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buildAnnotatedString {
                    val currentVisibleText = targetText.substring(0, animatedTextLength)
                    val boldPart = currentVisibleText.take(boldLength)
                    val normalPart = currentVisibleText.drop(boldLength)

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(boldPart)
                    }
                    append(normalPart)
                })
        }
    }, actions = {
        IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.settings_title)
            )
        }
    })
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
    val samplePassword =
        Password(id = 1, title = "Exemplo", username = "usuário", content = "senha123")
    PlainTextKotlinTheme {
        PasswordCard(samplePassword, rememberNavController())
    }
}