package com.samsantech.souschef.ui

import android.Manifest
import androidx.compose.runtime.getValue
import android.app.Activity
import android.content.Context
import android.widget.Toast
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.samsantech.souschef.R
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.ui.theme.Green
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import com.samsantech.souschef.data.CookingAssistantState
import com.samsantech.souschef.data.PreferencesManager
import com.samsantech.souschef.ui.components.ColoredButton
import com.samsantech.souschef.ui.components.KebabMenu
import com.samsantech.souschef.ui.components.ManageVoiceSettings
import com.samsantech.souschef.ui.components.OwnRecipeActionMenu
import com.samsantech.souschef.ui.components.PermissionRationaleDialog
import com.samsantech.souschef.ui.components.ProgressSpinner
import com.samsantech.souschef.ui.components.Rating
import com.samsantech.souschef.ui.components.UserNamePhoto
import com.samsantech.souschef.ui.components.VoiceCommandsGuide
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher
import com.samsantech.souschef.ui.theme.Yellow
import com.samsantech.souschef.utils.NetworkHelper
import com.samsantech.souschef.utils.getRecipeTimeText
import com.samsantech.souschef.viewmodel.AlgoliaInsightsViewModel
import com.samsantech.souschef.viewmodel.CookingAssistantViewModel
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel
import com.samsantech.souschef.viewmodel.RecipesViewModel
import com.samsantech.souschef.viewmodel.SharedViewModel
import com.samsantech.souschef.viewmodel.UserViewModel

@Composable
fun RecipeScreen(
    context: Context,
    activity: Activity,
    recipesViewModel: RecipesViewModel,
    onNavigateToPreviousScreen: () -> Unit,
    userViewModel: UserViewModel,
    ownRecipesViewModel: OwnRecipesViewModel,
    cookingAssistantViewModel: CookingAssistantViewModel,
    sharedViewModel: SharedViewModel,
    algoliaInsightsViewModel: AlgoliaInsightsViewModel,
    onNavigateToCreateRecipeOne: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {

    val user by userViewModel.user.collectAsState()
    val recipe: Recipe by recipesViewModel.displayRecipe.collectAsState()
    val favoriteRecipes by recipesViewModel.favoriteRecipes.collectAsState()
    val cookingAssistantState by cookingAssistantViewModel.cookingAssistantState.collectAsState()
    val voice by cookingAssistantViewModel.voice.collectAsState()

    val displayVoiceCommandPopUp = remember {
        mutableStateOf(false)
    }
    var manageVoiceSettings by remember {
        mutableStateOf(false)
    }
    var showRecipeActionMenu by remember {
        mutableStateOf(false)
    }
    var recipeWithAction: Recipe? by remember {
        mutableStateOf(null)
    }
    var loading by remember {
        mutableStateOf(false)
    }

    val userRating = remember { mutableFloatStateOf(recipe.ratings?.get(user?.uid)?.toFloat() ?: 0f) }
    val isFavorite = recipe.id in favoriteRecipes
    val averageRating = remember { mutableFloatStateOf(if (recipe.ratings.isNullOrEmpty()) { 0f } else {
        recipe.ratings?.values?.average()?.toFloat() ?: 0f
    }) }
    val showRateRecipe = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = if (cookingAssistantState.isCooking) 130.dp else 32.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            AsyncImage(
                model = if (recipe.photosUrl["square"] != null) "${recipe.photosUrl["square"]}" else "${recipe.photosUrl["portrait"]}",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()
                    .height(320.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.arrowback),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .offset(y = 30.dp, x = 10.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable { onNavigateToPreviousScreen() }
            )
            if (recipe.userId == user?.uid) {
                KebabMenu(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = 30.dp, x = -(10.dp)),
                    onClick = {
                            showRecipeActionMenu = !showRecipeActionMenu
                            recipeWithAction = if (recipeWithAction == null) recipe else null
                        }
                )
                Icon(
                    painter = painterResource(id = if (recipe.audience == "Public") R.drawable.world else R.drawable.padlock),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = 32.dp, x = -(40.dp))
                        .size(25.dp)
                )
            }
        }

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            RecipeMetadata(
                userId = user?.uid,
                recipe = recipe,
                isFavorite = isFavorite,
                recipesViewModel = recipesViewModel,
                userRating = userRating.floatValue,
                averageRating = averageRating.floatValue,
                context = context,
                algoliaInsightsViewModel = algoliaInsightsViewModel,
                showRateRecipe = showRateRecipe
            )
            Spacer(modifier = Modifier.height(20.dp))
            RecipeIngredients(recipe.ingredients)
            Spacer(modifier = Modifier.height(20.dp))
            RecipeInstructions(
                context,
                activity,
                instructions = recipe.instructions,
                recipe,
                displayVoiceCommandPopUp = { displayVoiceCommandPopUp.value = it },
                manageBluetoothSettings = { manageVoiceSettings = true },
                cookingAssistantState,
                sharedViewModel
            )
        }
    }

    if (showRateRecipe.value) {
        RateRecipe(
            currentRating = userRating.floatValue,
            showRateRecipe = showRateRecipe,
            onRateRecipe = { newRating ->
                recipe.id?.let {
                    recipesViewModel.rateRecipe(it, newRating) { success, updatedAverageRating ->
                        if (success) {
                            userRating.floatValue = newRating
                            averageRating.floatValue = updatedAverageRating ?: averageRating.floatValue
                        }
                    }
                    // will only send conversion data to algolia if recipe is above 4
                    if (newRating >= 4.0) {
                        algoliaInsightsViewModel.sendRatedARecipeEvent(it)
                    }
                }
            },
            onRemoveRating = {
                recipe.id?.let {
                    recipesViewModel.removeRecipeRating(it) { success, updatedAverageRating ->
                        if (success) {
                            userRating.floatValue = 0f
                            averageRating.floatValue = updatedAverageRating ?: averageRating.floatValue
                        }
                    }
                }
            }
        )
    }

    if (displayVoiceCommandPopUp.value) {
        VoiceCommandsGuide(
            isWhereToViewTextIsVisible = false,
            isGoBackIconVisible = false,
            isGoBackIconClicked = {}
        ) { isCloseIconClicked ->
            if (isCloseIconClicked) {
                displayVoiceCommandPopUp.value = false
            }
        }
    }

    if (manageVoiceSettings) {
        ManageVoiceSettings(
            voice,
            isCloseIconClicked = {
                manageVoiceSettings = false
            },
            onTry = {
                cookingAssistantViewModel.testVoice(it)
            },
            onSave = {
                cookingAssistantViewModel.changeVoice(it)
                cookingAssistantViewModel.stopSynthesis()
                manageVoiceSettings = false
            }
        )
    }

    if (recipe.userId == user?.uid) {
        OwnRecipeActionMenu(
            showRecipeActionMenu = showRecipeActionMenu,
            setShowRecipeActionMenu = { showRecipeActionMenu = it },
            recipeWithAction = recipeWithAction,
            setRecipeWithAction = { recipeWithAction = it },
            ownRecipesViewModel = ownRecipesViewModel,
            onNavigateToCreateRecipeOne = { onNavigateToCreateRecipeOne() },
            setLoading = {
                loading = it
            },
            onDeleted = {
                if (it) {
                    onNavigateToProfile()
                }
            }
        )
    }

    if (loading) {
        ProgressSpinner()
    }
}

@Composable
fun RecipeMetadata(
    userId: String?,
    recipe: Recipe,
    isFavorite: Boolean,
    recipesViewModel: RecipesViewModel,
    userRating: Float,
    averageRating: Float,
    context: Context,
    algoliaInsightsViewModel: AlgoliaInsightsViewModel,
    showRateRecipe: MutableState<Boolean>
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = recipe.title,
                fontSize = if (recipe.title.length < 13) 22.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                color = Green,
                modifier = Modifier
                    .fillMaxHeight(),
            )
            if (recipe.description.isNotEmpty() && recipe.description != "null") {
                Text(text = recipe.description, modifier = Modifier.padding(top = 5.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            val color = if (recipe.difficulty == "Easy") Color(0xff1ea185) else if (recipe.difficulty == "Medium") Color(0xfff29b26) else Color.Red
            Row {
                Text(
                    text = "Skill Level: ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = recipe.difficulty,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .border(1.dp, color, RoundedCornerShape(8.dp))
                        .padding(5.dp, 1.dp),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Rating(averageRating, recipe.ratings?.size ?: 0, 24.dp)

            if (recipe.userId != userId) {
                Text(
                    text = if (userRating > 0) "Edit your rating" else "Leave a rating",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { showRateRecipe.value = true },
                    fontStyle = FontStyle.Italic,
                    color = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.width(32.dp))
        if (recipe.userId != userId) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                contentDescription = null,
                tint = if (isFavorite) Green else Color.Gray,
                modifier = Modifier
                    .padding(0.dp, top = 8.dp)
                    .size(28.dp)
                    .clickable {
                        recipe.id?.let { id ->
                            recipesViewModel.toggleFavorite(id, !isFavorite) {
                                val message = if (isFavorite) {
                                    "Recipe removed from favorites"
                                } else {
                                    recipe.id?.let { algoliaInsightsViewModel.sendAddedToFavoritesEvent(it) }
                                    "Recipe added to favorites"
                                }
                                Toast
                                    .makeText(context, message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    },
            )
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
    UserNamePhoto(photoUri = recipe.userPhotoUrl, userName = recipe.userName)
    Spacer(modifier = Modifier.height(20.dp))
    Spacer(modifier = Modifier
        .fillMaxWidth()
        .height(2.dp)
        .background(Color(255, 207, 81))
    )


    Spacer(modifier = Modifier.height(16.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        TimeOrServing(title = "Serving", text = recipe.serving, modifier = Modifier.weight(1f))
        if (recipe.prepTimeHr.trim() != "0" || recipe.prepTimeMin.trim() != "0") {
            TimeOrServing(title = "Prep Time", text = getRecipeTimeText(recipe.prepTimeHr, recipe.prepTimeMin), modifier = Modifier.weight(1f))
        }
        if (recipe.cookTimeHr.trim() != "0" || recipe.cookTimeMin.trim() != "0") {
            TimeOrServing(title = "Cook Time", text = getRecipeTimeText(recipe.cookTimeHr, recipe.cookTimeMin), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun RateRecipe(
    currentRating: Float,
    showRateRecipe: MutableState<Boolean>,
    onRateRecipe: (Float) -> Unit,
    onRemoveRating: () -> Unit
) {
    val newUserRating = remember{ mutableFloatStateOf(currentRating) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .pointerInput(Unit) {
                detectTapGestures()
            },
        contentAlignment = Alignment.Center,
    ){
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .width(300.dp)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Box (
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                        .clip(CircleShape)
                        .clickable { showRateRecipe.value = false }

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.close_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(15.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (currentRating > 0) "Edit your rating" else "Leave a rating",
                        color = Color(0xFF16A637),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Konkhmer_Sleokcher,
                        modifier = Modifier.clickable {

                        }
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in 1..5) {
                            val starColor = if (newUserRating.floatValue >= i) Color(0xFFFFA500) else Color.Gray
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = starColor,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { newUserRating.floatValue = i.toFloat() }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(7.dp))
                    if (newUserRating.floatValue > 0) {
                        Text(
                            text = "Remove rating",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.clickable { newUserRating.floatValue = 0f }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    ColoredButton(
                        onClick = {
                            newUserRating.floatValue?.let {
                                if (it == currentRating) {
                                    return@let
                                } else if (it == 0f) {
                                    onRemoveRating()
                                } else if (it > 0) {
                                    onRateRecipe(it)
                                }
                            }

                            showRateRecipe.value = false
                        },
                        text = "Save",
                        modifier = Modifier.width(100.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TimeOrServing(title: String, text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Yellow.copy(.1f), RoundedCornerShape(10.dp))
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val iconId = if (title == "Serving") {
            R.drawable.ladle
        } else if (title == "Cook Time") {
            R.drawable.deadline
        } else {
            R.drawable.chopping_board
        }

        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier
                .size(22.dp),
            tint = Color(0xffea632e)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
        )
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RecipeIngredients(ingredients: List<String>) {
    Column {
        Text(
            text = "Ingredients",
            fontWeight = FontWeight(600),
            fontSize = 18.sp
        )
        for (ingredient in ingredients) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = ingredient
                )
            }
        }
    }
}

@Composable
fun RecipeInstructions(
    context: Context,
    activity: Activity,
    instructions: List<String>,
    recipe: Recipe,
    displayVoiceCommandPopUp: (Boolean) -> Unit,
    manageBluetoothSettings: (Boolean) -> Unit,
    cookingAssistantState: CookingAssistantState,
    sharedViewModel: SharedViewModel
) {
    val showRecordAudioRationaleDialog = remember {
        mutableStateOf(false)
    }
    val showBluetoothConnectRationaleDialog = remember {
        mutableStateOf(false)
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Instructions",
                fontWeight = FontWeight(600),
                fontSize = 18.sp
            )

            val recordAudioPermissionResultLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (!NetworkHelper.Companion.NetworkUtils.isNetworkAvailable(context)) {
                        Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show()
                    } else if (isGranted) {
                        if(!cookingAssistantState.isCooking) {
                            if (recipe.instructions.isNotEmpty()) {
                                sharedViewModel.startCookingAssistantService(context, recipe)
                            } else {
                                Toast.makeText(context, "Cooking assistant is running.\nTo cook anew, click the Stop icon.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )

            IconButton(onClick = {
                when {
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                            == PackageManager.PERMISSION_GRANTED-> {
                        if (!NetworkHelper.Companion.NetworkUtils.isNetworkAvailable(context)) {
                            Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show()
                        } else if(!cookingAssistantState.isCooking) {
                            if (recipe.instructions.isNotEmpty()) {
                                sharedViewModel.startCookingAssistantService(context, recipe)
                            }
                        } else {
                            Toast.makeText(context, "Cooking assistant is running.\nTo cook anew, click the Stop icon.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO) -> {
                        showRecordAudioRationaleDialog.value = true
                    } else -> {
                        recordAudioPermissionResultLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    when {
                        PreferencesManager.getDismissBluetoothConnectPermissionCount(context) < 1 && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                                != PackageManager.PERMISSION_GRANTED-> {
                            showBluetoothConnectRationaleDialog.value = true
                        }
                    }
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.voice_icon),
                    contentDescription = null,
                    tint = Color(22, 166, 55, 255),
                    modifier = Modifier
                        .size(35.dp)
                )
            }

            IconButton(onClick = { displayVoiceCommandPopUp(true) }, modifier = Modifier.offset(x = -(8.dp))) {
                Icon(
                    painter = painterResource(id = R.drawable.manual_icon),
                    contentDescription = null,
                    tint = Color.Black.copy(.8f),
                    modifier = Modifier
                        .size(21.dp)
                )
            }

            IconButton(onClick = { manageBluetoothSettings(true) }, modifier = Modifier.offset(x = -(16.dp))) {
                Icon(
                    painter = painterResource(id = R.drawable.music),
                    contentDescription = null,
                    tint = Color.Black.copy(.8f),
                    modifier = Modifier
                        .size(21.dp)
                )
            }

            if (showRecordAudioRationaleDialog.value) {
                PermissionRationaleDialog(
                    title = "Allow SousChef to access your microphone?",
                    description = "SousChef uses this to recognize voice commands for hands-free cooking experience.",
                    onDismiss = { showRecordAudioRationaleDialog.value = false },
                    onAllow = {
                        showRecordAudioRationaleDialog.value = false
                        sharedViewModel.openAppSettings(context)
                    }
                )
            }

            if (showBluetoothConnectRationaleDialog.value) {
                PermissionRationaleDialog(
                    title = "Allow SousChef to access Bluetooth (nearby devices)?",
                    description = "If you intend to use wireless earphone for voice-activated cooking assistance, this permission must be enabled.",
                    onDismiss = {
                        showBluetoothConnectRationaleDialog.value = false
                        PreferencesManager.incrementDismissBluetoothConnectPermissionCount(context)
                    },
                    onAllow = {
                        showBluetoothConnectRationaleDialog.value = false
                        sharedViewModel.openAppSettings(context)
                    }
                )
            }
        }

        instructions.forEachIndexed { index, instruction ->
            RecipeInstructionsItem(index = index+1, instruction = instruction)
        }
    }
}

@Composable
fun RecipeInstructionsItem(index: Int, instruction: String) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(25.dp)
                .clip(CircleShape)
                .background(Color(255, 207, 81, 255)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$index",
                color = Color.White,
                fontWeight = FontWeight(600),
                fontSize = 14.sp
            )
        }

        Text(
            text = instruction,
            modifier = Modifier
                .padding(start = 10.dp)
        )
    }
}
