package com.samsantech.souschef.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.samsantech.souschef.ui.components.LogoWithText
import com.samsantech.souschef.ui.components.ColoredButton
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Yellow

@Composable
fun SignUpOrLoginScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(22, 166, 55, 255))
            .padding(top = 130.dp, bottom = 30.dp, start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LogoWithText()

        Column {
            ColoredButton(
                onClick = onNavigateToLogin,
                containerColor = Color.White, contentColor = Green,
                text = "Login"
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            ColoredButton(
                onClick = onNavigateToSignUp,
                containerColor = Yellow, contentColor = Green,
                text = "Sign up",
            )
        }
    }
}