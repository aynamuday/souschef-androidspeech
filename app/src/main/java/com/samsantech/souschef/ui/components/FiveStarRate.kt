package com.samsantech.souschef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.samsantech.souschef.R

@Composable
fun FiveStarRate(rate: Float) {
    Star(size = 20.dp, fillPercentage = getStarRatePercentage(rate, 1))
    Star(size = 20.dp, fillPercentage = getStarRatePercentage(rate, 2))
    Star(size = 20.dp, fillPercentage = getStarRatePercentage(rate, 3))
    Star(size = 20.dp, fillPercentage = getStarRatePercentage(rate, 4))
    Star(size = 20.dp, fillPercentage = getStarRatePercentage(rate, 5))
}

fun getStarRatePercentage(rate: Float, order: Int): Float {
    return if (rate > order-1) {
        rate-order+1
    } else if(rate.toInt() == order) {
        1f
    } else {
        0f
    }
}

@Composable
fun Star(size: Dp, color: Color = Color(0xffffc946), fillPercentage: Float = 1f) {
    val starBitmap = ImageBitmap.imageResource(R.drawable.star)

    Box(
        modifier = Modifier
            .size(size - 1.dp)
            .drawWithContent {
                with(drawContext.canvas.nativeCanvas) {
                    val checkPoint = saveLayer(null, null)

                    drawContent()
                    drawImage(
                        image = starBitmap,
                        dstSize = IntSize(
                            width = size
                                .toPx()
                                .toInt(),
                            height = size
                                .toPx()
                                .toInt()
                        ),
                        blendMode = BlendMode.DstIn
                    )

                    restoreToCount(checkPoint)
                }
            },
        contentAlignment = Alignment.TopStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(size)
                .background(color.copy(.3f))
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(size * fillPercentage)
                .background(color)
        )
    }
}
