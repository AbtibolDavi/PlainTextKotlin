package com.example.plaintextkotlin.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.data.repository.PasswordRepository
import com.example.plaintextkotlin.model.Password
import com.example.plaintextkotlin.ui.theme.PlainTextKotlinTheme
import com.example.plaintextkotlin.ui.viewmodel.PasswordDetailPageViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordDetailPage(
    navController: NavController,
    passwordId: Int,
    viewModel: PasswordDetailPageViewModel = hiltViewModel()
) {

    if (passwordId == -1) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    LaunchedEffect(passwordId) {
        viewModel.loadDetailsIfNeeded(passwordId)
    }

    val passwordState = viewModel.password.collectAsState()
    val password = passwordState.value

    val isEditing by viewModel.isEditing.collectAsState()
    val editableTitle by viewModel.editableTitle.collectAsState()
    val editableUsername by viewModel.editableUsername.collectAsState()
    val editableContent by viewModel.editableContent.collectAsState()
    val saveStatus by viewModel.saveStatus.collectAsState()
    val isEditFormValid =
        editableTitle.isNotBlank() && editableUsername.isNotBlank() && editableContent.isNotBlank()

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showPasswordText by remember { mutableStateOf(false) }
    var backButtonClicked by remember { mutableStateOf(false) }

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
            title = { Text(context.getString(R.string.delete_alert_dialog)) },
            text = { Text(context.getString(R.string.delete_alert_dialog_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmationDialog = false
                        viewModel.deletePassword()
                    }) {
                    Text(
                        context.getString(R.string.delete), color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmationDialog = false }) {
                    Text(context.getString(R.string.cancel))
                }
            })
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    if (isEditing) stringResource(R.string.edit_password_title)
                    else stringResource(R.string.password_detail_title)
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    if (isEditing) {
                        viewModel.toggleEditMode()
                    } else {
                        if (!backButtonClicked) {
                            backButtonClicked = true
                            navController.popBackStack()
                        }
                    }
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }, actions = {
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
            })
        }) { paddingValues ->
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

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(pwd.imageResourceId),
                        contentDescription = stringResource(R.string.password_icon_desc),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(72.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = editableTitle,
                        onValueChange = { viewModel.updateEditableTitle(it) },
                        label = { Text(stringResource(R.string.website_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = editableTitle.isBlank(),
                        supportingText = { if (editableTitle.isBlank()) Text(stringResource(R.string.required_field)) })

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editableUsername,
                        onValueChange = { viewModel.updateEditableUsername(it) },
                        label = { Text(stringResource(R.string.username_label_required)) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = editableUsername.isBlank(),
                        supportingText = { if (editableUsername.isBlank()) Text(stringResource(R.string.required_field)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editableContent,
                        onValueChange = { viewModel.updateEditableContent(it) },
                        label = { Text(stringResource(R.string.password_label_required)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (showPasswordText) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPasswordText = !showPasswordText }) {
                                Icon(
                                    imageVector = if (showPasswordText) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (showPasswordText) stringResource(R.string.hide_password) else stringResource(
                                        R.string.show_password
                                    ),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        singleLine = true,
                        isError = editableContent.isBlank(),
                        supportingText = { if (editableContent.isBlank()) Text(stringResource(R.string.required_field)) },
                    )
                } else {
                    Text(
                        text = pwd.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
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
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.login_copied),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                                Icon(
                                    Icons.Filled.ContentCopy,
                                    contentDescription = stringResource(R.string.copy_login)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                                    if (showPasswordText) pwd.content else "•".repeat(pwd.content.length)
                                }", fontSize = 16.sp, modifier = Modifier.weight(1f), maxLines = 1
                            )
                            IconButton(onClick = { showPasswordText = !showPasswordText }) {
                                Icon(
                                    imageVector = if (showPasswordText) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (showPasswordText) stringResource(R.string.hide_password) else stringResource(
                                        R.string.show_password
                                    ),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(pwd.content))
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.password_copied),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                                Icon(
                                    Icons.Filled.ContentCopy,
                                    contentDescription = stringResource(R.string.copy_password)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { showDeleteConfirmationDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            stringResource(R.string.delete_password),
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }
        }
    }
}

class FakePasswordRepositoryPreview : PasswordRepository {
    val samplePassword = Password(
        id = 1, title = "Preview Website", username = "preview.user", content = "password123"
    )

    override fun getPasswords(): Flow<List<Password>> = flowOf(listOf(samplePassword))

    override fun searchPasswords(query: String): Flow<List<Password>> = flowOf(
        if (query.isBlank() || samplePassword.title.contains(query, ignoreCase = true)) {
            listOf(samplePassword)
        } else {
            emptyList()
        }
    )

    override suspend fun getPasswordById(id: Int): Password? {
        return if (id == samplePassword.id) samplePassword else null
    }

    override suspend fun insertPassword(password: Password) {}
    override suspend fun updatePassword(password: Password) {}
    override suspend fun deletePassword(password: Password) {}
}

@Preview(showBackground = true, name = "Password Detail View")
@Composable
fun PasswordDetailPagePreview() {
    val fakeRepository = FakePasswordRepositoryPreview()
    val previewViewModel = PasswordDetailPageViewModel(fakeRepository)
    val previewPasswordId = 1

    LaunchedEffect(Unit) {
        previewViewModel.loadDetailsIfNeeded(previewPasswordId)
    }

    PlainTextKotlinTheme {
        PasswordDetailPage(
            navController = rememberNavController(),
            passwordId = previewPasswordId,
            viewModel = previewViewModel
        )
    }
}