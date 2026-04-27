package com.samsantech.souschef.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Rating(averageRating: Float, ratingsSize: Int, starsSize: Dp = 18.dp) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        (1..5).forEach { star ->
            Icon(
                imageVector = if (star <= averageRating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (star <= averageRating) Color(0xFFFFA500) else Color.Gray,
                modifier = Modifier
                    .size(starsSize)
            )
        }
        Text(
            text = "   %.1f".format(averageRating)
                    + if (averageRating > 0) "  (${ratingsSize} rating"
                    + if (ratingsSize > 1) "s)" else ")" else "",
            fontSize = 12.sp,
            color = Color.Gray,
        )
    }
}