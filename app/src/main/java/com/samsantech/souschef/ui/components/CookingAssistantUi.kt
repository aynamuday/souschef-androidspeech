package com.samsantech.souschef.ui.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.samsantech.souschef.R
import com.samsantech.souschef.viewmodel.CookingAssistantViewModel
import com.samsantech.souschef.viewmodel.RecipesViewModel
import com.samsantech.souschef.viewmodel.SharedViewModel

@Composable
fun CookingAssistantUi(
    context: Context,
    isNetworkAvailable: Boolean,
    cookingAssistantViewModel: CookingAssistantViewModel,
    recipesViewModel: RecipesViewModel,
    sharedViewModel: SharedViewModel,
    onNavigateToRecipe: () -> Unit,
    bottomHeight: Dp = 0.dp
) {
    val cookingAssistantState = cookingAssistantViewModel.cookingAssistantState.collectAsState()
    val recipe = cookingAssistantState.value.recipe
    var photoUri: Uri? = null
    if (recipe != null) {
        photoUri = if(recipe.photosUrl["portrait"] != null) {
            Uri.parse("${recipe.photosUrl["portrait"]}")
        } else {
            Uri.parse("${recipe.photosUrl["square"]}")
        }
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable {
                        if (recipe != null) {
                            recipesViewModel.displayRecipe.value = recipe
                            onNavigateToRecipe()
                        }
                    }
                    .fillMaxWidth()
                    .background(Color(0xfffffee0))
                    .padding(15.dp, 10.dp)
                    .pointerInput(Unit)
                    {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent()
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = "$photoUri",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    recipe?.title?.let {
                        MarqueeText(
                            text = it,
                            containerWidth = 170f,
                            fontSize = 18,
                            fontWeight = 600
                        )
                    }
                    Text(
                        text = "Current Step: ${cookingAssistantState.value.currentStep}",
                        color = Color.Black.copy(alpha = .8f)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.transcription_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = cookingAssistantState.value.command,
                            maxLines = 1,
                            fontStyle = FontStyle.Italic,
                        )
                    }

                }

                Spacer(modifier = Modifier.width(20.dp))

                IconButton(
                    onClick = {
                        sharedViewModel.stopCookingAssistantService(context)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.stop_icon),
                        contentDescription = null,
                        tint = Color.Black.copy(.9f),
                        modifier = Modifier
                            .size(40.dp)
                    )
                }
            }

//            if (!isNetworkAvailable) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color(40, 40, 43))
//                        .padding(5.dp)
//                ) {
//                    Text(
//                        text = "No connection",
//                        fontSize = 14.sp,
//                        textAlign = TextAlign.Center,
//                        color = Color.White,
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                    )
//                }
//            }

            Spacer(modifier = Modifier.height(bottomHeight))
        }
    }
}