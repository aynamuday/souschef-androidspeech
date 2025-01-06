package com.samsantech.souschef.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.ui.theme.Green

@Composable
fun ColoredButton(modifier: Modifier = Modifier, onClick: () -> Unit, containerColor: Color = Green, contentColor: Color = Color.White,
                  text: String, border: BorderStroke = BorderStroke(0.dp, Color.Transparent), shadowElevation: Dp = 1.dp,
                  contentPadding: PaddingValues = PaddingValues(12.dp, 12.dp), fontWeight: FontWeight = FontWeight.SemiBold,
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .border(border, RoundedCornerShape(20))
            .shadow(shadowElevation, RoundedCornerShape(20)),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(20),
        contentPadding = contentPadding,
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = fontWeight
        )
    }
}