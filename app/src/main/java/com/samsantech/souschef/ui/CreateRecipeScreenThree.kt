package com.samsantech.souschef.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.ui.components.CreateRecipeBottomButtons
import com.samsantech.souschef.ui.components.ErrorText
import com.samsantech.souschef.ui.components.FormBasicTextField
import com.samsantech.souschef.ui.components.ImageButton
import com.samsantech.souschef.ui.components.OwnRecipeHeader
import com.samsantech.souschef.ui.theme.Yellow
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel

@Composable
fun CreateRecipeScreenThree(
    ownRecipesViewModel: OwnRecipesViewModel,
    onNavigateToCreateRecipeTwo: () -> Unit,
    onNavigateToCreateRecipeFour: () -> Unit,
    closeCreateRecipe: () -> Unit
) {
    val recipe by ownRecipesViewModel.recipe.collectAsState()

    var error by remember {
        mutableStateOf("")
    }

    BoxWithConstraints(modifier = Modifier.background(Color.White)) {
        val maxHeight = maxHeight
        Column {
            OwnRecipeHeader( closeCreateRecipe = closeCreateRecipe )
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .fillMaxWidth()
                    .defaultMinSize(Dp.Unspecified, maxHeight)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    Text(text = "Instructions", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    recipe.instructions.forEachIndexed { index, instruction ->
                        val stepNumber = index + 1
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Step $stepNumber", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White)
                                    .clickable {
                                        error = ""
                                        ownRecipesViewModel.removeInstruction(index)
                                    },
                                tint = Color.Gray,
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Box {
                            FormBasicTextField(
                                value = instruction,
                                onValueChange = {
                                    error = ""
                                    ownRecipesViewModel.updateInstructions(index, it)
                                },
                                maxLines = 5,
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (error.isNotEmpty()) {
                        ErrorText(text = error, textAlign = TextAlign.Center)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ImageButton(
                            onClick = {
                                error = ""
                                ownRecipesViewModel.addInstruction()
                            },
                            imageVector = Icons.Filled.Add, containerColor = Yellow, contentColor = Color.Black,
                            modifier = Modifier
                                .width(80.dp)
                                .size(32.dp), contentPadding = PaddingValues(0.dp)
                        )
                    }
                }

                CreateRecipeBottomButtons(
                    firstButtonText = "Back",
                    onFirstButtonClick = onNavigateToCreateRecipeTwo,
                    secondButtonText = "Next",
                    onSecondButtonClick = {
                        val newInstructions = recipe.instructions.toMutableList()
                        newInstructions.removeAll { it.trim().isBlank() }

                        if(newInstructions.size == 0) {
                            error = "At least one instruction is required."
                        } else {
                            ownRecipesViewModel.setInstructions(newInstructions)
                            onNavigateToCreateRecipeFour()
                        }
                    }
                )
            }
        }
    }
}