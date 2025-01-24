package com.samsantech.souschef.ui

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.ui.components.ColoredButton
import com.samsantech.souschef.ui.components.ConfirmDialog
import com.samsantech.souschef.ui.components.ErrorText
import com.samsantech.souschef.ui.components.FormOutlinedTextField
import com.samsantech.souschef.ui.components.Header
import com.samsantech.souschef.ui.components.PasswordOutlinedTextField
import com.samsantech.souschef.ui.components.ProgressSpinner
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher
import com.samsantech.souschef.viewmodel.AuthViewModel
import com.samsantech.souschef.viewmodel.UserViewModel

@Composable
fun UpdateEmailScreen(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onNavigateToLogin: () -> Unit,
) {
    val user by userViewModel.user.collectAsState()

    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var errorEmail by remember {
        mutableStateOf("")
    }
    var error by remember {
        mutableStateOf("")
    }
    var loading by remember {
        mutableStateOf(false)
    }
    var showConfirmDialog by remember {
        mutableStateOf(false)
    }

    Box {
        Column {
            Header()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp, bottom = 20.dp, start = 25.dp, end = 25.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Update Email",
                        color = Color(0xFF16A637),
                        fontSize = 25.sp,
                        fontFamily = Konkhmer_Sleokcher,
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )
                    )

                    FormOutlinedTextField(
                        value = email,
                        onValueChange = { valueChange ->
                            error = ""
                            errorEmail = ""
                            email = valueChange

                            if (valueChange.isNotBlank()) {
                                userViewModel.isEmailExists(valueChange) {
                                    if (it) {
                                        errorEmail = "Email is already taken."
                                    }
                                }
                            }
                            if (email == user!!.email) {
                                errorEmail = "The provided email is the same with the current email."
                            }
                        },
                        label = "Email",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null
                            )
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (errorEmail.isNotBlank()) {
                        ErrorText(text = errorEmail)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    PasswordOutlinedTextField(
                        value = password,
                        onValueChange = {
                            error = ""
                            password = it
                        },
                        label = "Confirm Your Password",
                        withLeadingIcon = true
                    )
                    if (error.isNotBlank()) {
                        ErrorText(text = error)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ColoredButton(
                        onClick = {
                            if (!email.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }) {
                                errorEmail = "Email must be valid format."
                            }

                            if (password.isEmpty()) {
                                error = "Password confirmation is required."
                            }

                            if (errorEmail.isEmpty() && error.isEmpty()) {
                                showConfirmDialog = true
                            }
                        },
                        text = "Save"
                    )
                }
            }
        }

        if (showConfirmDialog) {
            ConfirmDialog(
                message = "Are you sure?",
                subMessage = "You will be logged out to verify your email.",
                buttonOkayName = "Update",
                onClickCancel = { showConfirmDialog = false }) {
                    loading = true

                    userViewModel.updateEmail(email, password) { isSuccess, errorMessage ->
                        loading = false

                        if (isSuccess) {
                            authViewModel.sendEmailVerification() { _, _ ->
                                println()
                            }
                            authViewModel.logout()
                            onNavigateToLogin()
                        } else {
                            showConfirmDialog = false
                            if (errorMessage != null) {
                                error = errorMessage
                            }
                        }
                    }
            }
        }

        if (loading) {
            ProgressSpinner()
        }
    }
}