package com.samsantech.souschef.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.ui.components.ConfirmDialog
import com.samsantech.souschef.ui.components.CreateRecipeBottomButtons
import com.samsantech.souschef.ui.components.FormBasicTextField
import com.samsantech.souschef.ui.components.OwnRecipeHeader
import com.samsantech.souschef.ui.components.ProgressSpinner
import com.samsantech.souschef.ui.components.Dialog
import com.samsantech.souschef.ui.components.ErrorText
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Yellow
import com.samsantech.souschef.utils.OwnRecipeAction
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateRecipeScreenFour(
    context: Context,
    ownRecipesViewModel: OwnRecipesViewModel,
    onNavigateToCreateRecipeThree: () -> Unit,
    closeCreateRecipe: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val recipe by ownRecipesViewModel.recipe.collectAsState()
    val action by ownRecipesViewModel.action.collectAsState()
    val originalData by ownRecipesViewModel.originalData.collectAsState()
    var changes by remember {
        mutableStateOf(hashMapOf<String, Any>())
    }
    var suggestedTags by remember {
        mutableStateOf(
            listOf("filipino", "korean", "american", "vegan", "vegetarian", "gluten-free",
            "low-carb", "salty", "sweet", "spicy", "savory", "sour", "smoky", "baked",
            "grilled", "fried", "roasted", "fermented", "sauteed", "smoothie")
        )
    }
    var newTag by remember {
        mutableStateOf("")
    }
    var loading by remember {
        mutableStateOf(false)
    }
    var success by remember {
        mutableStateOf(false)
    }
    var saveRecipe by remember {
        mutableStateOf(false)
    }
    var errors by remember {
        mutableStateOf(hashMapOf<String, String>())
    }

    BoxWithConstraints(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()
    ) {
        val maxHeight = maxHeight
        Column {
            OwnRecipeHeader(closeCreateRecipe = closeCreateRecipe)
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, top = 10.dp)
                    .fillMaxWidth()
                    .defaultMinSize(Dp.Unspecified, maxHeight)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    Text(text = "Tags", fontWeight = FontWeight.Bold)
                    Text(
                        text = "You may select up to 7 tags.",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = if (errors["tags"] != null && errors["tags"] != "") Color.Red else Color.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            FormBasicTextField(
                                value = newTag,
                                onValueChange = {
                                    newTag = it.lowercase()
                                },
                                paddingValues = PaddingValues(10.dp),
                                borderColor = Green,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Text(
                            text = "+ Add",
                            modifier = Modifier
                                .clickable {
                                    if (newTag.isEmpty()) return@clickable

                                    val updatedSuggestedTags = suggestedTags.toMutableList()
                                    updatedSuggestedTags.add(newTag)
                                    suggestedTags = updatedSuggestedTags

                                    if (recipe.tags.size < 7) {
                                        ownRecipesViewModel.addTag(newTag)
                                    }

                                    newTag = ""
                                }
                                .clip(RoundedCornerShape(8.dp))
                                .background(Yellow)
                                .padding(12.dp, 5.dp)
                        )
                    }
                    if (errors["tags"] != null && errors["tags"] != "") {
                        Spacer(modifier = Modifier.height(12.dp))
                        ErrorText(text = errors["tags"]!!)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        val tags = (suggestedTags.toMutableList() + recipe.tags).distinct()

                        tags.forEach {
                            val isSelected = recipe.tags.contains(it)
                            val borderColor = if (isSelected) Green else Color.Black
                            val backgroundColor = if (isSelected) Green.copy(.3f) else Color.White

                            Text(
                                text = it,
                                modifier = Modifier
                                    .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                                    .background(backgroundColor, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val newErrors = hashMapOf<String, String>()
                                        if (isSelected) {
                                            ownRecipesViewModel.removeTag(it)
                                            newErrors["tags"] = ""
                                        } else if (recipe.tags.size < 7) {
                                            ownRecipesViewModel.addTag(it)
                                            newErrors["tags"] = ""
                                        } else {
                                            newErrors["tags"] = "You may only select up to 7 tags."
                                        }
                                        errors = newErrors
                                    }
                                    .padding(10.dp, 5.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = recipe.audience, fontWeight = FontWeight(600))
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = recipe.audience == "Public",
                                onCheckedChange = {
                                    val audience: String = if (recipe.audience == "Public") {
                                        "Only me"
                                    } else {
                                        "Public"
                                    }
                                    ownRecipesViewModel.toggleAudience(audience)
                                },
                                modifier = Modifier
                                    .height(40.dp),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Green,
                                    checkedTrackColor = Color.Gray.copy(.1f),
                                    checkedBorderColor = Color.Gray.copy(.8f),
                                    uncheckedThumbColor = Color.Black,
                                    uncheckedTrackColor = Color.Gray.copy(.1f),
                                    uncheckedBorderColor = Color.Gray.copy(.8f),
                                )
                            )
                        }
                        Text(
                            text = if (recipe.audience == "Only me") "Only you can see this recipe" else "Other users can see your recipe",
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }

                }

                CreateRecipeBottomButtons(
                    firstButtonText = "Back",
                    onFirstButtonClick = onNavigateToCreateRecipeThree,
                    secondButtonText = if (action == OwnRecipeAction.EDIT) "Save" else "Create",
                    onSecondButtonClick = {
                        if (action == OwnRecipeAction.EDIT) {
                            changes = getRecipesDifference(originalData, recipe)

                            if (changes.isEmpty() && recipe.photosUri.size < 1 && ownRecipesViewModel.deletePhotoKey == null) {
                                closeCreateRecipe()
                            } else {
                                saveRecipe = true
                            }
                        } else {
                            saveRecipe = true
                        }
                    }
                )
            }
        }

        if (saveRecipe) {
            ConfirmDialog(
                message = if (action == OwnRecipeAction.ADD) "Upload recipe?" else "Save changes?",
                buttonOkayName = "Continue",
                onClickCancel = { saveRecipe = false }
            ) {
                saveRecipe = false

                if (action == OwnRecipeAction.ADD) {
                    loading = true
                    ownRecipesViewModel.uploadRecipe { isSuccess, error ->
                        loading = false

                        if (!isSuccess && error != null) {
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        } else {
                            success = true
                        }
                    }
                } else if (action == OwnRecipeAction.EDIT) {
                    loading = true
                    ownRecipesViewModel.updateRecipe(changes) { isSuccess, err ->
                        loading = false

                        if (!isSuccess && err != null) {
                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                        } else {
                            success = true
                        }

                        changes = hashMapOf()
                    }
                }
            }
        }

        if (loading) {
            ProgressSpinner()
        }

        if (success) {
            Dialog(
                icon = "success",
                message = if (action == OwnRecipeAction.EDIT) "Recipe updated successfully!" else "Recipe uploaded successfully!",
                subMessage = null,
                onCloseClick = {
                    if (action == OwnRecipeAction.EDIT) closeCreateRecipe() else onNavigateToProfile()
                    ownRecipesViewModel.action.value = OwnRecipeAction.ADD
                    success = false
                }
            )
        }
    }
}

fun getRecipesDifference(recipeOne: Recipe, recipeTwo: Recipe): HashMap<String, Any> {
    val data = hashMapOf<String, Any>()

    if (recipeOne.title != recipeTwo.title) {
        data["title"] = recipeTwo.title
    }
    if (recipeOne.description != recipeTwo.description) {
        data["description"] = recipeTwo.description
    }
    if (recipeOne.prepTimeHr != recipeTwo.prepTimeHr) {
        data["prepTimeHr"] = recipeTwo.prepTimeHr
    }
    if (recipeOne.prepTimeMin != recipeTwo.prepTimeMin) {
        data["prepTimeMin"] = recipeTwo.prepTimeMin
    }
    if (recipeOne.cookTimeHr != recipeTwo.cookTimeHr) {
        data["cookTimeHr"] = recipeTwo.cookTimeHr
    }
    if (recipeOne.cookTimeMin != recipeTwo.cookTimeMin) {
        data["cookTimeMin"] = recipeTwo.cookTimeMin
    }
    if (recipeOne.serving != recipeTwo.serving) {
        data["serving"] = recipeTwo.serving
    }
    if (recipeOne.difficulty != recipeTwo.difficulty) {
        data["difficulty"] = recipeTwo.difficulty
    }
    if (recipeOne.mealTypes != recipeTwo.mealTypes) {
        data["mealTypes"] = recipeTwo.mealTypes
    }
    if (recipeOne.categories != recipeTwo.categories) {
        data["categories"] = recipeTwo.categories
    }
    if (recipeOne.ingredients != recipeTwo.ingredients) {
        data["ingredients"] = recipeTwo.ingredients
    }
    if (recipeOne.instructions != recipeTwo.instructions) {
        data["instructions"] = recipeTwo.instructions
    }
    if (recipeOne.tags != recipeTwo.tags) {
        data["tags"] = recipeTwo.tags
    }
    if (recipeOne.audience != recipeTwo.audience) {
        data["audience"] = recipeTwo.audience
    }

    return data
}