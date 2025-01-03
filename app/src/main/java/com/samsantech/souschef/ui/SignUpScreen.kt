package com.samsantech.souschef.ui

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.ui.components.FormOutlinedTextField
import com.samsantech.souschef.ui.components.ColoredButton
import com.samsantech.souschef.ui.components.PasswordOutlinedTextField
import com.samsantech.souschef.ui.components.ProgressSpinner
import com.samsantech.souschef.ui.components.Dialog
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher
import com.samsantech.souschef.utils.isValidUsername
import com.samsantech.souschef.viewmodel.AuthViewModel
import com.samsantech.souschef.viewmodel.UserViewModel

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onNavigateToVerifyEmail: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val signUpInformation by authViewModel.signUpInformation.collectAsState()
    var loading by remember {
        mutableStateOf(false)
    }
    var success by remember {
        mutableStateOf(false)
    }
    var errorDisplayName by remember {
        mutableStateOf("")
    }
    var errorUsername by remember {
        mutableStateOf("")
    }
    var errorEmail by remember {
        mutableStateOf("")
    }
    var errorPassword by remember {
        mutableStateOf("")
    }
    var error by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, bottom = 70.dp, start = 25.dp, end = 25.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create an account",
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
                value = signUpInformation.displayName,
                onValueChange = { valueChange ->
                    error = ""
                    errorDisplayName = ""
                    authViewModel.setSignUpInformation(displayName = valueChange)
                },
                label = "Name",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "person icon"
                    )
                },
            )
            if (errorDisplayName.isNotBlank()) {
                Text(text = errorDisplayName, fontSize = 14.sp, color = Color.Red, modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.height(8.dp))
            FormOutlinedTextField(
                value = signUpInformation.username,
                onValueChange = { valueChange ->
                    error = ""
                    errorUsername = ""
                    authViewModel.setSignUpInformation(username = valueChange)

                    if (valueChange.isNotBlank()) {
                        userViewModel.isUsernameExists(valueChange) {
                            if (it) {
                                errorUsername = "Username is already taken."
                            }
                        }

                        if (!isValidUsername(valueChange)) {
                            errorUsername = "Username must only contain letters, numbers, underscore, and dot."
                        }
                    }
                },
                label = "Username",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "person icon"
                    )
                },
            )
            if (errorUsername.isNotBlank()) {
                Text(text = errorUsername, fontSize = 14.sp, color = Color.Red, modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.height(8.dp))
            FormOutlinedTextField(
                value = signUpInformation.email,
                onValueChange = { valueChange ->
                    error = ""
                    errorEmail = ""
                    authViewModel.setSignUpInformation(email = valueChange)

                    if (valueChange.isNotBlank()) {
                        userViewModel.isEmailExists(valueChange) {
                            if (it) {
                                errorEmail = "Email is already taken."
                            }
                        }
                    }
                },
                label = "Email",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "email icon"
                    )
                },
            )
            if (errorEmail.isNotBlank()) {
                Text(text = errorEmail, fontSize = 14.sp, color = Color.Red, modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.height(8.dp))
            PasswordOutlinedTextField(
                value = signUpInformation.password,
                onValueChange = {
                    error = ""
                    errorPassword = ""
                    authViewModel.setSignUpInformation(password = it)
                },
                label = "Password", withLeadingIcon = true
            )
            if (errorPassword.isNotBlank()) {
                Text(text = errorPassword, fontSize = 14.sp, color = Color.Red, modifier = Modifier.fillMaxWidth())
            }
            if (error.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = error, fontSize = 14.sp, color = Color.Red, modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.height(16.dp))
            ColoredButton(
                onClick = {
                    val displayName = signUpInformation.displayName
                    val username = signUpInformation.username
                    val email = signUpInformation.email
                    val password = signUpInformation.password

                    if (displayName.isEmpty()) {
                        errorDisplayName = "Name is required."
                    }
                    if (username.isEmpty()) {
                        errorUsername = "Username is required."
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        errorEmail = "Email must be valid format."
                    }
                    if (password.length < 8 || !password.contains(Regex("[0-9]"))
                        || !password.contains(Regex("[A-Z]")) || !password.contains(Regex("[^A-Za-z0-9]"))) {
                        errorPassword = "Password must be at least 8 characters long, contain a number, an uppercase letter, and a special character."
                    }

                    if (errorDisplayName.isEmpty() && errorUsername.isEmpty() && errorEmail.isEmpty() && errorPassword.isEmpty() && error.isEmpty()) {
                        loading = true
                        authViewModel.signUp { isSuccess, errorMessage ->
                            loading = false
                            if (errorMessage != null) {
                                error = errorMessage
                            }

                            if (isSuccess) {
                                userViewModel.refreshUser()
                                authViewModel.setSignUpInformation()
                                success = true
                            }
                        }
                    }
                },
                text = "Continue"
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Already have an account?",
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(16.dp))
            ColoredButton(
                onClick = onNavigateToLogin,
                containerColor = Color.White, contentColor = Green,
                text = "Login",
                border = BorderStroke(1.dp, Color.Black)
            )
        }
    }

    if (loading) {
        ProgressSpinner()
    }

    if (success) {
        Dialog(
            icon = "success",
            message = "Sign up successful!",
            subMessage = "Your account has been created.",
            onCloseClick = {
                authViewModel.logout()
                authViewModel.sendEmailVerification() { _, errorMessage ->
                    if (errorMessage != null) {
                        println(errorMessage)
                    }
                }
                onNavigateToVerifyEmail()
            }
        )
    }
}