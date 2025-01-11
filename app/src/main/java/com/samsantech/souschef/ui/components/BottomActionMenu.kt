package com.samsantech.souschef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun BottomActionMenuPopUpContainer(onOutsideClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(.4f))
            .zIndex(1f)
            .pointerInput(Unit) {
                detectTapGestures {
                    onOutsideClick()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .align(Alignment.BottomCenter)
                .background(Color.White)
                .padding(bottom = 30.dp, top = 16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun BottomActionMenuItemContainer(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(horizontal = 20.dp)
            .padding(bottom = 8.dp)
    ) {
        content()
    }
}

@Composable
fun BottomActionMenuItemIconTitle(
    icon: ImageVector,
    title: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .background(Color.Gray.copy(.2f))
                .padding(10.dp)
                .size(20.dp)
        )
    }
    Spacer(modifier = Modifier.width(15.dp))
    Text(text = title, fontWeight = FontWeight.Bold)
}

@Composable
fun BottomActionMenuRadioButtonItem(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomActionMenuItemIconTitle(icon = icon, title = title)
    }
    RadioButton(selected = selected) {
        onClick()
    }
}