package com.example.plaintextkotlin.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.navigation.Routes
import com.example.plaintextkotlin.ui.theme.PlainTextKotlinTheme
import com.example.plaintextkotlin.ui.viewmodel.LoginPageViewModel
import com.example.plaintextkotlin.ui.viewmodel.LoginPageViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun LoginPage(navController: NavController,
              viewModel: LoginPageViewModel = viewModel(factory = LoginPageViewModelFactory(context = LocalContext.current))
) {
//    var username by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showImage by remember { mutableStateOf(false) }
    var animatedSloganTextCount by remember { mutableStateOf(0) }
    var animatedAuthorTextCount by remember { mutableStateOf(0) }
    val sloganText = stringResource(R.string.slogan_text)
    val authorText = stringResource(R.string.slogan_author)
    var showAuthorText by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var loginEnabled by remember { mutableStateOf(true) }

    var usernameFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        for (i in sloganText.indices) {
            animatedSloganTextCount = i + 1
            delay(50)
        }
        showAuthorText = true
        for (i in authorText.indices) {
            animatedAuthorTextCount = i + 1
            delay(50)
        }
        showImage = true
    }

//    LaunchedEffect( /**/) {
//        loginEnabled = true
//    }
    loginEnabled = true

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
                    .padding(16.dp), contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedVisibility(
                        visible = showImage,
                        enter = fadeIn(animationSpec = tween(durationMillis = 2500))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.plain_text),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sloganText.substring(0, animatedSloganTextCount),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        if (showAuthorText) {
                            Text(
                                text = authorText.substring(0, animatedAuthorTextCount),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.login_message),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            var usernameInput by remember { mutableStateOf("") }
            OutlinedTextField(
                value = usernameInput,
                onValueChange = { usernameInput = it },
                label = {
                    Text(
                        stringResource(R.string.username_label),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState -> usernameFocused = focusState.isFocused },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                isError = usernameInput.isEmpty() && usernameFocused,
                supportingText = { if (usernameInput.isEmpty()) Text(stringResource(R.string.required_field))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            var passwordInput by remember { mutableStateOf("") }
            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                label = {
                    Text(
                        stringResource(R.string.password_label),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState -> passwordFocused = focusState.isFocused },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (showPassword) {
                        IconButton(onClick = { showPassword = false }) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = stringResource(R.string.hide_password),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        IconButton(onClick = { showPassword = true }) {
                            Icon(
                                imageVector = Icons.Filled.VisibilityOff,
                                contentDescription = stringResource(R.string.hide_password),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                isError = passwordInput.isEmpty() && passwordFocused,
                supportingText = { if (passwordInput.isEmpty()) Text(stringResource(R.string.required_field))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    stringResource(R.string.remember_me_check),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                enabled = loginEnabled,
                onClick = {
                    viewModel.onLoginButtonClicked(usernameInput) { username ->
                        navController.navigate(Routes.PASSWORD_PAGE.replace("{username}", username))
                        Toast.makeText(context, context.getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .wrapContentWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = stringResource(R.string.login_button_text),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    PlainTextKotlinTheme() {
        LoginPage(rememberNavController())
    }
}