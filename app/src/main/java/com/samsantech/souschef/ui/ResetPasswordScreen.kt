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
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.ui.components.FormOutlinedTextField
import com.samsantech.souschef.ui.components.ColoredButton
import com.samsantech.souschef.ui.components.ErrorText
import com.samsantech.souschef.ui.components.ProgressSpinner
import com.samsantech.souschef.ui.components.Dialog
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher
import com.samsantech.souschef.viewmodel.AuthViewModel

@Composable
fun ResetPasswordScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    var email by remember {
        mutableStateOf("")
    }
    var error by remember {
        mutableStateOf("")
    }
    var loading by remember {
        mutableStateOf(false)
    }
    var success by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, bottom = 70.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Reset Password",
                color = Color(0xFF16A637),
                fontSize = 32.sp,
                fontFamily = Konkhmer_Sleokcher,
                style = LocalTextStyle.current.merge(
                    TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            )
            Text(text = "Please enter the email associated with your account.", textAlign = TextAlign.Center)

            FormOutlinedTextField(
                value = email,
                onValueChange = {
                    error = ""
                    email = it
                },
                label = "Email",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null
                    )
                },
            )
            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                ErrorText(text = error)
            }
            Spacer(modifier = Modifier.height(20.dp))
            ColoredButton(
                onClick = {
                    if (email.isNotEmpty() ) {
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            error = "Please provide a valid email address."
                        } else {
                            loading = true
                            authViewModel.resetPassword(email) { isSuccess, errorMessage ->
                                loading = false
                                success = isSuccess
                                if (errorMessage != null) {
                                    error = errorMessage
                                }
                            }
                        }
                    }
                },
                text = "Send Reset Email"
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Remember Password?",
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
            message = "Reset Link Sent",
            subMessage = "Password reset link has been successfully sent to your email.",
            onCloseClick = onNavigateToLogin
        )
    }
}