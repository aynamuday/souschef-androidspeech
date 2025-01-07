package com.samsantech.souschef.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserNamePhoto(
    photoUri: String?,
    userName: String?,
    photoSize: Dp = 40.dp,
    fontWeight: FontWeight = FontWeight(600),
    fontColor: Color = Color.Black,
    fontSize: TextUnit = 16.sp,
    spacer: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacer),
        modifier = modifier
    ) {
        ProfilePhoto(
            uri = photoUri,
            size = photoSize
        )
        userName?.let {
            Text(
                text = it,
                fontWeight = fontWeight,
                color = fontColor,
                fontSize = fontSize,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}