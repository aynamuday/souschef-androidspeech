package com.samsantech.souschef.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(modifier: Modifier = Modifier, value: String, onValueChange: (String) -> Unit,
                  label: @Composable() (() -> Unit)? = null,
                  textAlign: TextAlign = TextAlign.Start,
                  maxLines: Int = Int.MAX_VALUE, placeholder: String = "") {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        textStyle = LocalTextStyle.current.merge(
            textAlign = textAlign,
        ),
        maxLines = maxLines,
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black
        ),
        placeholder = {
            Text(text = placeholder, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    )
}