package com.samsantech.souschef.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import coil3.compose.AsyncImage
import com.samsantech.souschef.R
import com.samsantech.souschef.ui.components.ColoredButton
import com.samsantech.souschef.ui.components.ErrorText
import com.samsantech.souschef.ui.components.FormBasicTextField
import com.samsantech.souschef.ui.components.OwnRecipeHeader
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Yellow
import com.samsantech.souschef.utils.OwnRecipeAction
import com.samsantech.souschef.utils.convertUriToBitmap
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CreateRecipeScreenOne(
    context: Context,
    ownRecipesViewModel: OwnRecipesViewModel,
    onNavigateToCreateRecipeTwo: () -> Unit,
    closeCreateRecipe: () -> Unit
) {
    val recipe by ownRecipesViewModel.recipe.collectAsState()
    val action by ownRecipesViewModel.action.collectAsState()

    var categories by remember {
//        mutableStateOf(arrayOf("Chicken", "Pork", "Beef", "Seafood", "Vegetables", "Dessert", "Drink"))
        mutableStateOf(arrayOf("Chicken", "Pork", "Beef", "Other Meat", "Seafood", "Rice", "Vegetables", "Fruits", "Dessert", "Drink", "Others"))
    }
    val mealTypes = arrayOf("Breakfast", "Lunch", "Dinner", "Snack")
    val difficulty = arrayOf("Easy", "Medium", "Hard")
//    val courses = arrayOf("Main", "Side", "Appetizer", "Dessert", "Drink")
    var portrait by remember {
        mutableStateOf<Uri?>(null)
    }
    var landscape by remember {
        mutableStateOf<Uri?>(null)
    }
    var square by remember {
        mutableStateOf<Uri?>(null)
    }

    var showDifficultyDropdown by remember {
        mutableStateOf(false)
    }
    var showAddCategory by remember {
        mutableStateOf(false)
    }
    var errors by remember {
        mutableStateOf(hashMapOf<String, String>())
    }

    val pickImagePortrait = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            clearError("photos", errors) { newErrors ->
                errors = newErrors
            }

            portrait = uri
//            portrait = convertUriToBitmap(context, uri)
            ownRecipesViewModel.addPhoto("portrait", uri)
        }
    }
    val pickImageLandscape = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            clearError("photos", errors) { newErrors ->
                errors = newErrors
            }

            landscape = uri
//            landscape = convertUriToBitmap(context, uri)
            ownRecipesViewModel.addPhoto("landscape", uri)
        }
    }
    val pickImageSquare = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            clearError("photos", errors) { newErrors ->
                errors = newErrors
            }

            square = uri
//            square = convertUriToBitmap(context, uri)
            ownRecipesViewModel.addPhoto("square", uri)
        }
    }

    Box(modifier = Modifier.background(Color.White)) {
        Column {
            OwnRecipeHeader(closeCreateRecipe)
            Column(
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // GALLERY
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CreateImageContainer(
                        height = 200.dp,
                        width = 113.dp,
                        image  = if (action == OwnRecipeAction.EDIT)
                                    if (portrait != null) portrait
                                    else if (recipe.photosUrl["portrait"] != null) {
                                        Uri.parse(recipe.photosUrl["portrait"].toString())
                                    } else {
                                        null
                                    }
                                else portrait,
                        pickImage = pickImagePortrait,
                        onRemoveClick = {
                            portrait = null
                            ownRecipesViewModel.removePhoto("portrait")
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        CreateImageContainer(
                            height = 113.dp,
                            width = 200.dp,
                            image  = if (action == OwnRecipeAction.EDIT)
                                        if (landscape != null) landscape
                                        else if (recipe.photosUrl["landscape"] != null) {
                                            Uri.parse(recipe.photosUrl["landscape"].toString())
                                        } else {
                                            null
                                        }
                                    else landscape,
                            pickImage = pickImageLandscape,
                            onRemoveClick = {
                                landscape = null
                                ownRecipesViewModel.removePhoto("landscape")
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CreateImageContainer(
                            height = 150.dp,
                            width = 150.dp,
                            image  = if (action == OwnRecipeAction.EDIT)
                                        if (square != null) square
                                        else if (recipe.photosUrl["square"] != null) {
                                            Uri.parse(recipe.photosUrl["square"].toString())
                                        } else {
                                            null
                                        }
                                    else square,
                            pickImage = pickImageSquare,
                            onRemoveClick = {
                                square = null
                                ownRecipesViewModel.removePhoto("square")
                            }
                        )
                    }
                }
                if (errors["photos"] != null && errors["photos"] != "") {
                    Spacer(modifier = Modifier.height(5.dp))
                    ErrorText(text = errors["photos"]!!)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // TITLE
                Column {
//                    Text(text = "Title", fontWeight = FontWeight.Bold)
//                    Spacer(modifier = Modifier.height(10.dp))
                    FormBasicTextField(
                        value = recipe.title,
                        onValueChange = {
                            clearError("title", errors) { newErrors ->
                                errors = newErrors
                            }

                            ownRecipesViewModel.setTitle(it)
                        },
                        placeholder = "What's the title of your recipe?",
                        borderColor = Color.Gray,
                    )
                }
                if (errors["title"] != null && errors["title"] != "") {
                    Spacer(modifier = Modifier.height(5.dp))
                    ErrorText(text = errors["title"]!!)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // DESCRIPTION
                Column {
//                    Text(text = "Description", fontWeight = FontWeight.Bold)
//                    Spacer(modifier = Modifier.height(10.dp))
                    FormBasicTextField(
                        value = recipe.description,
                        onValueChange = {
                            ownRecipesViewModel.setDescription(it)
                        },
                        minLines = 3,
                        placeholder = "Tell us something more about this recipe.",
                        borderColor = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // PREPARATION TIME
                RecipeTime(
                    name = "Preparation Time",
                    hr = recipe.prepTimeHr,
                    maxHr = 48,
                    onHrChange = {
                        clearError("prepTime", errors) { newErrors ->
                            errors = newErrors
                        }

                        ownRecipesViewModel.setPrepTimeHr(it)
                    },
                    min = recipe.prepTimeMin,
                    onMinChange = {
                        clearError("prepTime", errors) { newErrors ->
                            errors = newErrors
                        }

                        ownRecipesViewModel.setPrepTimeMin(it)
                    },
                    errorName = "prepTime",
                    errors = errors,
                    setErrors = {
                        errors = it
                    }
                )
                if (errors["prepTime"] != null && errors["prepTime"] != "") {
                    Spacer(modifier = Modifier.height(5.dp))
                    ErrorText(text = errors["prepTime"]!!, textAlign = TextAlign.End)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // COOK TIME
                RecipeTime(
                    name = "Cook Time",
                    hr = recipe.cookTimeHr,
                    maxHr = 48,
                    onHrChange = {
                        clearError("cookTime", errors) { newErrors ->
                            errors = newErrors
                        }

                        ownRecipesViewModel.setCookTimeHr(it)
                    },
                    min = recipe.cookTimeMin,
                    onMinChange = {
                        clearError("cookTime", errors) { newErrors ->
                            errors = newErrors
                        }

                        ownRecipesViewModel.setCookTimeMin(it)
                    },
                    errorName = "cookTime",
                    errors = errors,
                    setErrors = {
                        errors = it
                    },
                )
                if (errors["cookTime"] != null && errors["cookTime"] != "") {
                    Spacer(modifier = Modifier.height(5.dp))
                    ErrorText(text = errors["cookTime"]!!, textAlign = TextAlign.End)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // SERVING
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Serving", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    NumberFieldWithPlusMinusButtons(
                        value = recipe.serving,
                        valueName = "serving",
                        maxValue = 200,
                        minValue = 1,
                        onValueChange = {
                            clearError("serving", errors) { newErrors ->
                                errors = newErrors
                            }

                            ownRecipesViewModel.setServing(it)
                        },
                        errorName = "serving",
                        errors = errors,
                        setErrors = {
                            errors = it
                        }
                    )
                }
                if (errors["serving"] != null && errors["serving"] != "") {
                    Spacer(modifier = Modifier.height(5.dp))
                    ErrorText(text = errors["serving"]!!, textAlign = TextAlign.End)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // DIFFICULTY
                Column {
                    Text(text = "Difficulty", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Box {
                        Text(
                            text = if (recipe.difficulty != "") recipe.difficulty else "Select difficulty",
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 10.dp)
                                .clickable { showDifficultyDropdown = !showDifficultyDropdown }
                        )
                    }
                    BoxWithConstraints(
                        contentAlignment = Alignment.Center,
                    ) {
                        DropdownMenu(
                            expanded = showDifficultyDropdown,
                            onDismissRequest = { showDifficultyDropdown = false },
                            modifier = Modifier
                                .width(maxWidth)
                                .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
                                .background(Color.White, RoundedCornerShape(10.dp)),
                            properties = PopupProperties(dismissOnClickOutside = true, focusable = true, dismissOnBackPress = true)
                        ) {
                            difficulty.forEach { difficulty ->
                                DropdownMenuItem(
                                    text = { Text(text = difficulty, fontSize = 16.sp) },
                                    onClick = {
                                        clearError("difficulty", errors) { newErrors ->
                                            errors = newErrors
                                        }

                                        ownRecipesViewModel.setDifficulty(difficulty)
                                        showDifficultyDropdown = false
                                    },
                                    modifier = Modifier
                                        .background(if (difficulty == recipe.difficulty) Green.copy(.2f) else Color.White)
                                )
                            }
                        }
                    }
                }
                if (errors["difficulty"] != null && errors["difficulty"] != "") {
                    Spacer(modifier = Modifier.height(5.dp))
                    ErrorText(text = errors["difficulty"]!!)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // MEAL TYPE
                Column {
                    Text(text = "Meal Type", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    mealTypes.forEach { mealType ->
                        val isSelected = recipe.mealTypes.contains(mealType)

                        CreateRecipeCard(
                            mealType,
                            onClick = {
                                clearError("mealTypes", errors) { newErrors ->
                                    errors = newErrors
                                }

                                if (isSelected) {
                                    ownRecipesViewModel.removeMealType(mealType)
                                } else {
                                    ownRecipesViewModel.addMealType(mealType)
                                }
                            },
                            borderColor = if (isSelected) { Green } else Color.Black,
                            backgroundColor = if (isSelected) { Green.copy(.2f) } else Color.White,
                            textColor = if (isSelected) { Color.Black } else Color.Black
                        )
                    }
                }
                if (errors["mealTypes"] != null && errors["mealTypes"] != "") {
                    Spacer(modifier = Modifier.height(5.dp))
                    ErrorText(text = errors["mealTypes"]!!)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // MAIN CATEGORY
                Column {
                    Text(text = "Main Category", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    categories.forEach { category ->
                        val isSelected = recipe.categories.contains(category)

                        CreateRecipeCard(
                            category,
                            onClick = {
                                clearError("categories", errors) { newErrors ->
                                    errors = newErrors
                                }

                                if (isSelected) {
                                    ownRecipesViewModel.removeCategory(category)
                                } else {
                                    if (recipe.categories.size == 3) {
                                        Toast.makeText(context, "Maximum categories is 3.", Toast.LENGTH_LONG).show()
                                    } else {
                                        ownRecipesViewModel.addCategory(category)
                                    }
                                }
                            },
                            borderColor = if (isSelected) { Green } else Color.Black,
                            backgroundColor = if (isSelected) { Green.copy(.2f) } else Color.White,
                            textColor = if (isSelected) { Color.Black } else Color.Black
                        )
                    }
                }
                if (errors["categories"] != null && errors["categories"] != "") {
                    Spacer(modifier = Modifier.height(5.dp))
                    ErrorText(text = errors["categories"]!!)
                }

                // NEXT BUTTON
                Spacer(modifier = Modifier.height(30.dp))
                if (errors["general"] != null && errors["general"] != "") {
                    Spacer(modifier = Modifier.height(5.dp))
                    ErrorText(text = errors["general"]!!, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(5.dp))
                }
                ColoredButton(
                    onClick = {
                        val newErrors = hashMapOf<String, String>()

                        if (action != OwnRecipeAction.EDIT && (recipe.photosUri["portrait"] == null && recipe.photosUri["landscape"] == null && recipe.photosUri["square"] == null)) {
                            newErrors["photos"] = "At least one photo is required."
                        }

                        if (recipe.title.isEmpty()) {
                            newErrors["title"] = "Title is required."
                        }

                        if (recipe.prepTimeHr == "0" && recipe.prepTimeMin == "0"
                            && recipe.cookTimeHr == "0" && recipe.cookTimeMin == "0") {
                            newErrors["prepTime"] = "Provide either preparation time or cook time."
                            newErrors["cookTime"] = "Provide either preparation time or cook time."
                        }

                        if (recipe.difficulty.isEmpty()) {
                            newErrors["difficulty"] = "Difficulty is required."
                        }

                        if (recipe.mealTypes.isEmpty()) {
                            newErrors["mealTypes"] = "Select at least one meal type."
                        }

                        if (recipe.categories.isEmpty()) {
                            newErrors["categories"] = "Select at least one category."
                        }

                        if (newErrors.size != 0) {
                            newErrors["general"] = "Check your inputs for errors."
                            errors = newErrors
                        } else {
                            onNavigateToCreateRecipeTwo()
                        }
                    },
                    text = "Next",
                    containerColor = Green,
                    contentColor = Color.White
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // ADD CATEGORY POPUP
        if (showAddCategory) {
            CreateCategory(
                onCloseClick = { showAddCategory = false },
                onAddCategory = {
                    if (it.trim() != "") {
                        ownRecipesViewModel.addCategory(it)
                        categories = categories.plus(it)
                    }
                    showAddCategory = false
                }
            )
        }
    }
}

@Composable
fun CreateImageContainer(
    height: Dp,
    width: Dp,
    image: Uri?,
    pickImage: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    onRemoveClick: () -> Unit
) {
    Box {
        Box(
            modifier = Modifier
                .height(height)
                .width(width)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .clickable {
                    pickImage.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ){
            if (image != null) {
                AsyncImage(
                    model = "$image",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxSize()
                        .background(Color.White)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.images),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        if (image != null) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier
                    .offset(7.dp, (-7).dp)
                    .size(16.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(50))
                    .background(Color.White, RoundedCornerShape(50))
                    .padding(2.dp)
                    .align(Alignment.TopEnd)
                    .clickable { onRemoveClick() },
                tint = Color.Gray,
            )
        }
    }
}

@Composable
fun CreateRecipeCard(name: String, onClick: () -> Unit, backgroundColor: Color, borderColor: Color, textColor: Color) {
    Text(
        text = name,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .background(backgroundColor, RoundedCornerShape(10.dp))
            .padding(10.dp),
        textAlign = TextAlign.Center,
        color = textColor
    )
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun CreateCategory(onCloseClick: () -> Unit, onAddCategory: (String) -> Unit) {
    var newCategory by remember {
        mutableStateOf("")
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(.5f))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(maxWidth - 48.dp)
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(vertical = 20.dp, horizontal = 24.dp)
        ) {
            Box() {
                Text(text = "Add Category", fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(50))
                        .clickable { onCloseClick() }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            FormBasicTextField(
                value = newCategory,
                onValueChange = {
                    newCategory = it
                },
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onAddCategory(newCategory) }
                ) {
                    Text(
                        text = "Add",
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Yellow)
                            .padding(30.dp, 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeTime(
    name: String,
    hr: String,
    maxHr: Int,
    onHrChange: (value: String) -> Unit,
    min: String,
    onMinChange: (value: String) -> Unit,
    errorName: String,
    errors: HashMap<String, String>,
    setErrors: (HashMap<String, String>) -> Unit
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Column {
            NumberFieldWithPlusMinusButtons(
                value = hr,
                valueName = "hours",
                label = "hr",
                maxValue = maxHr,
                onValueChange = {
                    clearError(errorName, errors) { newErrors ->
                        setErrors(newErrors)
                    }

                    onHrChange(it)
                },
                errorName = errorName,
                errors = errors,
                setErrors = {
                    setErrors(it)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            NumberFieldWithPlusMinusButtons(
                value = min,
                valueName = "minutes",
                maxValue = 60,
                label = "min",
                onValueChange = {
                    clearError(errorName, errors) { newErrors ->
                        setErrors(newErrors)
                    }

                    onMinChange(it)
                },
                errorName = errorName,
                errors = errors,
                setErrors = {
                    setErrors(it)
                }
            )
        }
    }
}

@Composable
fun NumberFieldWithPlusMinusButtons(
    value: String,
    valueName: String,
    maxValue: Int,
    minValue: Int = 0,
    label: String? = null,
    onValueChange: (String) -> Unit,
    errorName: String,
    errors: HashMap<String, String>,
    setErrors: (HashMap<String, String>) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            painter = painterResource(id = R.drawable.minus_icon),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(50))
                .clickable {
                    val intValue = value.toInt()

                    if (intValue == minValue) {
                        onValueChange(maxValue.toString())
                    } else {
                        onValueChange((intValue - 1).toString())
                    }
                },
            tint = Green
        )
        Spacer(modifier = Modifier.width(5.dp))
        FormBasicTextField(
            value = value,
            onValueChange = {
                if (it == value) return@FormBasicTextField

                var error = ""

                val intValue = it.toIntOrNull()
                if (intValue != null || it == "") {
                    if (intValue != null && intValue > maxValue) {
                        error = "Maximum $valueName is $maxValue."
                    } else {
                        val newValue = if (it == "") 0 else intValue?.plus(0)
                        onValueChange(newValue.toString())
                    }
                } else {
                    error = "Can only input numbers."
                }

                if (error != "") {
                    val newErrors = HashMap<String, String>(errors.toMap())
                    newErrors[errorName] = error
                    setErrors(newErrors)
                }
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.width(60.dp),
            placeholderAlign = TextAlign.Center,
            borderColor = Color.Gray,
            padding = 8.dp
        )
        Spacer(modifier = Modifier.width(5.dp))
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(50))
                .clickable {
                    val intValue = value.toInt()

                    if (intValue == maxValue) {
                        onValueChange(minValue.toString())
                    } else {
                        onValueChange((intValue + 1).toString())
                    }
                }
            ,
            tint = Green
        )
        if (label != null) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = label)
        }
    }
}

fun clearError(errorName: String, errors: HashMap<String, String>, setErrors: (HashMap<String, String>) -> Unit) {
    val clearedErrors = HashMap<String, String>(errors.toMap())
    clearedErrors.remove(errorName)
    setErrors(clearedErrors)
}

fun setError(errorName: String, error: String, errors: HashMap<String, String>, setErrors: (HashMap<String, String>) -> Unit) {
    val newErrors = HashMap<String, String>(errors.toMap())
    newErrors[errorName] = error
    setErrors(newErrors)
}