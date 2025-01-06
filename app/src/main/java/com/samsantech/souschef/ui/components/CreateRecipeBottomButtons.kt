package com.samsantech.souschef.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.samsantech.souschef.ui.theme.Green

@Composable
fun CreateRecipeBottomButtons(
    firstButtonText: String,
    onFirstButtonClick: () -> Unit,
    secondButtonText: String,
    onSecondButtonClick: () -> Unit,
    ) {
    Column {
        Spacer(modifier = Modifier.height(30.dp))
        Row {
            ColoredButton(
                onClick = onFirstButtonClick,
                text = firstButtonText,
                containerColor = Color.White,
                contentColor = Color.Black,
                border = BorderStroke(1.dp, Green),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            ColoredButton(
                onClick = onSecondButtonClick,
                text = secondButtonText,
                containerColor = Green,
                contentColor = Color.White,
                border = BorderStroke(1.dp, Green),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}