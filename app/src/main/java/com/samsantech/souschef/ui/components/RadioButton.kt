package com.samsantech.souschef.ui.components

import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.samsantech.souschef.ui.theme.Green

@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.material3.RadioButton(
        colors = RadioButtonDefaults.colors(
            selectedColor = Green,
            unselectedColor = Color.Black,
            disabledSelectedColor = Green.copy(.7f),
            disabledUnselectedColor = Color.Black.copy(.5f)
        ),
        selected = selected,
        onClick = onClick
    )
}