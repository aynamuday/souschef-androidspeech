package com.samsantech.souschef.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.samsantech.souschef.ui.theme.Green

@Composable
fun SearchBox(
    search: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.weight(1f)) {
            FormBasicTextField(
                value = search,
                onValueChange = {
                    onValueChange(it.lowercase())
                },
                placeholder = "What are you craving?",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSubmit()
                    }
                ),
                paddingValues = PaddingValues(start = 35.dp, top = 8.dp, bottom = 8.dp, end = 12.dp),
                roundCorner = 5.dp,
                backgroundColor = Color.Gray.copy(.2f),
                borderColor = Color.Transparent,
                trailingIcon = Icons.Filled.Search,
            )
        }

        Text(
            text = "Search",
            modifier = Modifier
                .wrapContentSize(unbounded = true)
                .clickable {
                    onSubmit()
                },
            color = Green
        )
    }
}

