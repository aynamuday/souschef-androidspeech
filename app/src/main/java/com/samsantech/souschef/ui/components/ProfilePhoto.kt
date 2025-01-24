package com.samsantech.souschef.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ProfilePhoto(
    uri: String?,
    size: Dp
) {
    if (uri == "" || uri == null || uri == "null") {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(1.dp, Color.Gray, RoundedCornerShape(50)),
            tint = Color.Gray
        )
    } else {
        AsyncImage(
            model = uri,
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .size(size),
            contentScale = ContentScale.Crop
        )
    }
}

