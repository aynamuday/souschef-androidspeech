package com.samsantech.souschef.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MarqueeText(text: String, containerWidth: Float, fontSize: Int, fontWeight: Int) {
    var textWidth by remember { mutableFloatStateOf(0f) }
    var textSp by remember { mutableIntStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scrollState by infiniteTransition.animateFloat(
        initialValue = (textWidth * 2 + containerWidth),
        targetValue = -(textWidth * 2 + containerWidth),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ((text.length * 200).coerceIn(3000, 20000)), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .width(containerWidth.dp)
            .widthIn(containerWidth.dp)
            .clip(RectangleShape)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .wrapContentWidth(unbounded = true)
                .onGloballyPositioned { coordinates ->
                    textSp = coordinates.size.width
                }
                .graphicsLayer {
                    translationX = if (textWidth > containerWidth) scrollState else 0f
                    },
            maxLines = 1,
            fontSize = fontSize.sp,
            fontWeight = FontWeight(fontWeight)
        )

        textWidth = with(LocalDensity.current) {
            textSp.toDp().value
        }
    }
}