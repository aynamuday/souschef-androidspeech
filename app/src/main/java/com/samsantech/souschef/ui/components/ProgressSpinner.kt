package com.samsantech.souschef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.samsantech.souschef.ui.theme.Green

@Composable
fun ProgressSpinner() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(.3f))
            .pointerInput(Unit) {
                detectTapGestures()
            },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(50.dp),
            color = Color.White,
            trackColor = Green,
            strokeWidth = 5.dp
        )
    }
}