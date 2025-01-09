package com.samsantech.souschef.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.samsantech.souschef.ui.theme.Yellow

@Composable
fun DisplayProfileImage(
    bitmap: Bitmap? = null, uri: String? = null, withCancelButton: Boolean = false,
    onCancel: () -> Unit = {}, onOkay: () -> Unit, onOkayText: String, onBoxClick: () -> Unit = {},
    withCloseButton: Boolean = false, onCloseClick: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures {
                    onCloseClick()
                }
            }
            .fillMaxSize()
            .background(Color.Black.copy(.3f))
            .padding(horizontal = 20.dp)
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(200.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(198.dp)
                                .background(Color.White)
                        )
                    } else if (uri != null) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(198.dp)
                                .background(Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (withCancelButton) {
                        ColoredButton(
                            onClick = onCancel,
                            text = "Cancel",
                            modifier = Modifier.weight(.8f),
                            contentPadding = PaddingValues(12.dp, 12.dp),
                            containerColor = Color.White, contentColor = Color.Black,
                            border = BorderStroke(1.dp, Yellow)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    ColoredButton(
                        onClick = onOkay,
                        text = onOkayText,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(12.dp, 12.dp),
                        containerColor = Yellow, contentColor = Color.Black,
                        border = BorderStroke(1.dp, Yellow)
                    )
                }
            }

            if (withCloseButton) {
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 5.dp, end = 5.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(25.dp))
                }
            }
        }
    }
}