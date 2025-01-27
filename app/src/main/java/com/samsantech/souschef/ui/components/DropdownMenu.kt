package com.samsantech.souschef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties

@Composable
fun DropdownMenu(
    displayText: String,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    selection: List<String>,
    onSelect: (String) -> Unit
) {
    Box(modifier = Modifier.clickable { onToggleExpand() }) {
        Text(
            text = displayText,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                .padding(10.dp),
            textAlign = TextAlign.Center
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
        )
    }
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onToggleExpand()
            },
            modifier = Modifier
                .width(maxWidth)
                .background(Color.White, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp)),
            properties = PopupProperties(
                dismissOnClickOutside = true,
                focusable = true,
                dismissOnBackPress = true
            )
        ) {
            selection.forEach { selection ->
                DropdownMenuItem(
                    text = { Text(text = selection, fontSize = 16.sp) },
                    onClick = {
                        onSelect(selection)
                    },
                )
            }
        }
    }
}