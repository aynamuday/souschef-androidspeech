package com.samsantech.souschef.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchBottomActionMenuPopUp(
    view: String,
    onOutsideClick: () -> Unit,
    onSelectView: (String) -> Unit
) {
    BottomActionMenuPopUpContainer(
        onOutsideClick = {
            onOutsideClick()
        },
        content = {
            Column {
//                Text(
//                    text = "View",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 18.sp,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 16.dp),
//                    textAlign = TextAlign.Center
//                )
                Spacer(modifier = Modifier.height(12.dp))
                BottomActionMenuItemContainer(onClick = { onSelectView("grid") }) {
                    BottomActionMenuRadioButtonItem(
                        title = "Grid",
                        icon = Icons.Filled.GridView,
                        selected = view.lowercase() == "grid",
                        onClick = {
                            onSelectView("grid")
                        }
                    )
                }
                BottomActionMenuItemContainer(onClick = { onSelectView("list") }) {
                    BottomActionMenuRadioButtonItem(
                        title = "List",
                        icon = Icons.AutoMirrored.Filled.List,
                        selected = view == "list",
                        onClick = {
                            onSelectView("list")
                        }
                    )
                }
            }
        }
    )
}