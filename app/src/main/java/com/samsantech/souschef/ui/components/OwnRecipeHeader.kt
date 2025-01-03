package com.samsantech.souschef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher

@Composable
fun OwnRecipeHeader(
    closeCreateRecipe: () -> Unit
) {
    Box(
        contentAlignment = Alignment.CenterStart
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = null,
            modifier = Modifier
                .zIndex(2f)
                .padding(top = 32.dp, start = 10.dp)
                .size(20.dp)
                .clip(RoundedCornerShape(50))
                .clickable {
                    closeCreateRecipe()
                },
            tint = Color.White
        )
        Text(
            text = "Create Recipe",
            fontFamily = Konkhmer_Sleokcher,
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .background(Green)
                .padding(top = 32.dp),
            textAlign = TextAlign.Center
        )
    }
    Spacer(modifier = Modifier
        .background(Color.LightGray)
        .height(1.dp)
        .shadow(1.dp, ambientColor = Color.LightGray, spotColor = Color.LightGray)
        .fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(20.dp))
}