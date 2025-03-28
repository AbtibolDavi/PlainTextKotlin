package com.example.plaintextkotlin.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.ui.viewmodel.PasswordDetailPageViewModel
import com.example.plaintextkotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordDetailPage(
    navController: NavController,
    passwordId: Int,
    viewModel: PasswordDetailPageViewModel = viewModel(
        factory = ViewModelFactory(
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

    val isEditing by viewModel.isEditing.collectAsState()
    val editableTitle by viewModel.editableTitle.collectAsState()
    val editableUsername by viewModel.editableUsername.collectAsState()
    val editableContent by viewModel.editableContent.collectAsState()
    val saveStatus by viewModel.saveStatus.collectAsState()
    val isEditFormValid = editableTitle.isNotBlank() && editableUsername.isNotBlank() && editableContent.isNotBlank()

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showPasswordText by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.uiMessage, navController, context) {
        viewModel.uiMessage.collectLatest { messageResId ->
            val message = context.getString(messageResId)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if (messageResId == R.string.successful_deleted_pwd) {
                navController.popBackStack()
            }
        }
    }

    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text(context.getString(R.string.delete_alert_dialog))},
            text = { Text(context.getString(R.string.delete_alert_dialog_text))},
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmationDialog = false
                        viewModel.deletePassword()
                    }
                ) {
                    Text(context.getString(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmationDialog = false }
                ) {
                    Text(context.getString(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) stringResource(R.string.edit_password_title)
                        else stringResource(R.string.password_detail_title)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing) {
                            viewModel.toggleEditMode()
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (password != null) {
                        IconButton(
                            onClick = {
                                if (isEditing) {
                                    viewModel.savePasswordChanges()
                                } else {
                                    viewModel.toggleEditMode()
                                }
                            },
                            enabled = saveStatus != PasswordDetailPageViewModel.SaveStatus.Saving && (!isEditing || isEditFormValid)
                        ) {
                            if (saveStatus == PasswordDetailPageViewModel.SaveStatus.Saving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Icon(
                                    imageVector = if (isEditing) Icons.Filled.Save else Icons.Filled.Edit,
                                    contentDescription = if (isEditing) stringResource(R.string.save_changes)
                                    else stringResource(R.string.edit)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        password?.let { pwd ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
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
                    if (isEditing) {
                        OutlinedTextField(
                            value = editableTitle,
                            onValueChange = { viewModel.updateEditableTitle(it) },
                            label = { Text(stringResource(R.string.website_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = editableTitle.isBlank(),
                            supportingText = { if (editableTitle.isBlank()) Text(stringResource(R.string.required_field))}
                        )
                    } else {
                        Text(
                            text = pwd.title,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = editableUsername,
                            onValueChange = { viewModel.updateEditableUsername(it) },
                            label = { Text(stringResource(R.string.username_label_required)) },
                            modifier = Modifier.fillMaxWidth(),
                            isError = editableUsername.isBlank(),
                            supportingText = { if (editableUsername.isBlank()) Text(stringResource(R.string.required_field))}
                        )
                    } else {
                        Surface (
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ){
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${stringResource(R.string.username_label)} ${pwd.username}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                IconButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(pwd.username))
                                    Toast.makeText(context, context.getString(R.string.login_copied), Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Filled.ContentCopy, contentDescription = stringResource(R.string.copy_login))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo Senha

                    if (isEditing) {
                        OutlinedTextField(
                            value = editableContent,
                            onValueChange = { viewModel.updateEditableContent(it) },
                            label = { Text(stringResource(R.string.password_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (showPasswordText) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPasswordText = !showPasswordText }) {
                                    Icon(
                                        imageVector = if (showPasswordText) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = if (showPasswordText) stringResource(R.string.hide_password) else stringResource(
                                            R.string.show_password
                                        )
                                    )
                                }
                            },
                            isError = editableContent.isBlank(),
                            supportingText = { if (editableContent.isBlank()) Text(stringResource(R.string.required_field))}
                        )
                    } else {
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
                                        if (showPasswordText) pwd.content else "*".repeat(pwd.content.length)
                                    }",
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1
                                )
                                IconButton(onClick = { showPasswordText = !showPasswordText }) {
                                    Icon(
                                        imageVector = if (showPasswordText) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = if (showPasswordText) stringResource(R.string.hide_password) else stringResource(
                                            R.string.show_password
                                        )
                                    )
                                }
                                IconButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(pwd.content))
                                    Toast.makeText(context, context.getString(R.string.password_copied), Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        Icons.Filled.ContentCopy,
                                        contentDescription = stringResource(R.string.copy_password)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (!isEditing) {
                        Button(
                            onClick = { showDeleteConfirmationDialog = true },
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
        }
}

//@Preview
//@Composable
//fun PasswordDetailPagePreview() {
//    PlainTextKotlinTheme {
//        PasswordDetailPage(
//            navController = rememberNavController(),
//            passwordId = R.string.password_title_1
//        )
//    }
//}