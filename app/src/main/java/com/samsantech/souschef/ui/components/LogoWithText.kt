package com.samsantech.souschef.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.R

@Composable
fun LogoWithText() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.souchef_logo_2),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
        )
        Text(
            text = "S  O  U  S  C  H  E  F",
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.inter)),
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .padding(top = 5.dp),
        )
    }
}