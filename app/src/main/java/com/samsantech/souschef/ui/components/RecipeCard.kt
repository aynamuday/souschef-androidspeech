package com.samsantech.souschef.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.samsantech.souschef.R

@Composable
fun RecipeCard(
    photoUrl: Uri?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    showKebabMenu: Boolean = false,
    onClickKebabMenu: () -> Unit = {},
    showPrivacyIcon: Boolean = false,
    privacy: String? = null
) {
    Box(
        modifier = modifier
            .zIndex(-1f)
            .height(180.dp)
            .background(Color.Gray.copy(.2f), RoundedCornerShape(5.dp))
            .border(
                if (photoUrl != null) 0.dp else 1.dp,
                if (photoUrl != null) Color.Transparent else Color.Gray,
                RoundedCornerShape(5.dp)
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = "$photoUrl",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(5.dp))
        )
        if (showKebabMenu) {
            KebabMenu(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(5.dp, 3.dp)
                    .size(25.dp),
                onClick = {
                    onClickKebabMenu()
                }
            )
        }
        if (showPrivacyIcon) {
            Icon(
                painter = painterResource(id = if (privacy == "Public") R.drawable.world else R.drawable.padlock),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = 5.dp, x = -(15.dp))
                    .size(20.dp)
            )
        }
    }
}