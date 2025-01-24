package com.samsantech.souschef.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordOutlinedTextField(value: String, onValueChange: (String) -> Unit,
                          label: String? = null, withLeadingIcon: Boolean = false) {
    var showPassword by remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            if (label != null) {
                Text(text = label, fontSize = 14.sp)
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = LocalTextStyle.current.merge(
            fontSize = 16.sp
        ),
        leadingIcon = if (!withLeadingIcon) null else { {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "lock icon"
            )
        } },
        singleLine = true,
        trailingIcon = {
            Icon(
                painter = if (showPassword) { painterResource(id = R.drawable.visibility_vector) }
                    else { painterResource(id = R.drawable.visibility_off_vector) },
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        showPassword = !showPassword
                    }
            )
        },
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (!showPassword) { PasswordVisualTransformation() } else { VisualTransformation.None } ,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black
        ),
    )
}