package com.samsantech.souschef.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun KebabMenu(modifier: Modifier, onClick: () -> Unit) {
    Icon(
        imageVector = Icons.Filled.MoreVert,
        contentDescription = null,
        tint = Color.White,
        modifier = modifier
            .size(30.dp)
            .clip(RoundedCornerShape(100))
            .clickable {
                onClick()
            }
    )
}