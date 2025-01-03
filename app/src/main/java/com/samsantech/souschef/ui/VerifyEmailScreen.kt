package com.samsantech.souschef.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.samsantech.souschef.ui.components.ErrorText
import com.samsantech.souschef.ui.components.ProgressSpinner
import com.samsantech.souschef.ui.components.Dialog
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher
import com.samsantech.souschef.viewmodel.AuthViewModel
import com.samsantech.souschef.viewmodel.UserViewModel

@Composable
fun VerifyEmailScreen(
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    val user by userViewModel.user.collectAsState()

    var error by remember {
        mutableStateOf("")
    }
    var loading by remember {
        mutableStateOf(false)
    }
    var showSuccessDialog by remember {
        mutableStateOf(false)
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
                text = "Verify Your Email",
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
            Text(text = "A verification email has been sent to your email address. Please check your email and click on the link to verify your email address.")

            Spacer(modifier = Modifier.height(10.dp))
            ErrorText(text = error)

//            ColoredButton(
//                onClick = {
//                    loading = true
//
//                    authViewModel.sendEmailVerification() { isSuccess, errorMessage ->
//                        loading = false
//
//                        if (isSuccess) {
//                            showSuccessDialog = true
//                        } else {
//                            if (errorMessage != null) {
//                                error = errorMessage
//                            }
//                        }
//                    }
//                },
//                text = "Resend Email Verification"
//            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            ColoredButton(
                onClick = onNavigateToLogin,
                containerColor = Green, contentColor = Color.White,
                text = "Login",
                border = BorderStroke(1.dp, Green)
            )
        }
    }

    if (loading) {
        ProgressSpinner()
    }

    if (showSuccessDialog) {
        Dialog(
            icon = "success",
            message = "Email Sent",
            subMessage = "A new verification email is sent to your email address.",
        ) {
            showSuccessDialog = false
        }
    }
}