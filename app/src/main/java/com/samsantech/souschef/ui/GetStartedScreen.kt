package com.samsantech.souschef.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.R
import com.samsantech.souschef.ui.components.ColoredButton
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher

@Composable
fun GetStartedScreen(activity: ComponentActivity, onNavigateToSignUpOrLogin: () -> Unit) {
    BackHandler {
        activity.finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.start_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Gray.copy(alpha = .2f))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp, bottom = 30.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "SOUCHEF",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontFamily = Konkhmer_Sleokcher,
                    style = LocalTextStyle.current.merge(
                        TextStyle(
                            lineHeight = 0.em,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                )
                Text(
                    text = "Your Assistant to Culinary Success",
                    color = Color(0xFFFFD600),
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    style = LocalTextStyle.current.merge(
                        TextStyle(
                            lineHeight = 0.em,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                )
            }
            ColoredButton(
                onClick = onNavigateToSignUpOrLogin,
                text = "Start"
            )
        }
    }
}