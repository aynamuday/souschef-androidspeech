package com.samsantech.souschef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FormBasicTextField(modifier: Modifier = Modifier,
                       value: String,
                       onValueChange: (String) -> Unit,
                       minLines: Int = 1,
                       maxLines: Int = minLines,
                       placeholder: String? = null,
                       textAlign: TextAlign = TextAlign.Start,
                       borderColor: Color = Color.Black,
                       backgroundColor: Color = Color.White,
                       roundCorner: Dp = 10.dp,
                       placeholderAlign: TextAlign = TextAlign.Start,
                       paddingValues: PaddingValues = PaddingValues(12.dp),
                       keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
                       keyboardActions: KeyboardActions = KeyboardActions.Default,
                       leadingIcon: ImageVector? = null,
                       trailingIcon: ImageVector? = null,
                       trailingIconModifier: Modifier = Modifier

                       ) {
    Box {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 5.dp),
                tint = Color.Black.copy(.8f)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            minLines = minLines,
            maxLines = maxLines,
            textStyle = TextStyle(fontSize = 16.sp, textAlign = textAlign),
            modifier = modifier
                .border(1.dp, borderColor, RoundedCornerShape(roundCorner))
                .background(backgroundColor, RoundedCornerShape(roundCorner))
                .padding(paddingValues)
                .fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (placeholder != null) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color.Gray,
                            textAlign = placeholderAlign,
                            fontSize = 14.sp
                        )
                    }
                }

                innerTextField()
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = maxLines == 1
        )
        if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = trailingIconModifier
                    .align(Alignment.CenterEnd)
                    .size(16.dp),
                tint = Color.Black.copy(.7f)
            )
        }
    }
}