package com.samsantech.souschef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionRationaleDialog(
    title: String,
    description: String,
    onDismiss: () -> Unit,
    onAllow: () -> Unit) {

    AlertDialog(
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(start = 30.dp, top = 30.dp, end = 25.dp, bottom = 20.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth(),
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    lineHeight = 26.sp,
                    color = Color(22, 166, 55, 255),
                    fontWeight = FontWeight(600)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = description,
                    modifier = Modifier
                        .fillMaxWidth(),
                    fontFamily = FontFamily.SansSerif,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End

                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            disabledContentColor = Color.Black,
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            text = "DENY",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 16.sp,
                            fontWeight = FontWeight(500)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = onAllow,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color(22, 166, 55, 255),
                            disabledContentColor = Color(22, 166, 55, 255),
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            text = "ALLOW",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    )
}