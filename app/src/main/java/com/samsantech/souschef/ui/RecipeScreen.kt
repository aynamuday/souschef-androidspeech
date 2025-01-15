package com.samsantech.souschef.ui

import androidx.compose.runtime.getValue
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.samsantech.souschef.ui.components.FiveStarRate
import com.samsantech.souschef.ui.components.KebabMenu
import com.samsantech.souschef.ui.components.OwnRecipeActionMenu
import com.samsantech.souschef.ui.components.ProgressSpinner
import com.samsantech.souschef.ui.components.UserNamePhoto
import com.samsantech.souschef.utils.getRecipeTimeText
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel
import com.samsantech.souschef.viewmodel.RecipesViewModel
import com.samsantech.souschef.viewmodel.UserViewModel

//val sharedViewModel = SharedViewModel()
@Composable
fun RecipeScreen(
    activity: Activity,
    context: Context,
    recipesViewModel: RecipesViewModel,
    onNavigateToPreviousScreen: () -> Unit,
    userViewModel: UserViewModel,
    ownRecipesViewModel: OwnRecipesViewModel,
    onNavigateToCreateRecipeOne: () -> Unit,
    onNavigateToProfile: () -> Unit
) {

    val user by userViewModel.user.collectAsState()
    val recipe: Recipe by recipesViewModel.displayRecipe.collectAsState()
    val favoriteRecipes by recipesViewModel.favoriteRecipes.collectAsState()
//    val cookingAssistantState by cookingAssistantViewModel.cookingAssistantState.collectAsState()

//    val displayVoiceCommandPopUp = remember {
//        mutableStateOf(false)
//    }
    var showRecipeActionMenu by remember {
        mutableStateOf(false)
    }
    var recipeWithAction: Recipe? by remember {
        mutableStateOf(null)
    }
    var loading by remember {
        mutableStateOf(false)
    }

    val userRating = remember { mutableStateOf(recipe.userRating ?: 0f) }
    val averageRating = recipe.averageRating ?: 0f
    val isFavorite = recipe.id in favoriteRecipes


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
//            .padding(bottom = if (cookingAssistantState.isCooking) 130.dp else 40.dp)
            .padding(bottom = 32.dp)
            .pointerInput(Unit) {
                detectTapGestures {
//                    displayVoiceCommandPopUp.value = false
                }
            }
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
            }
        }

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {

            RecipeMetadata(
                recipe = recipe,
                isFavorite = isFavorite,
                recipesViewModel,
                rating = userRating.value,
                averageRating = averageRating,
                onRateRecipe = { newRating ->
                    recipe.id?.let {
                        recipesViewModel.rateRecipe(it, newRating) { success ->
                            if (success) {
                                userRating.value = newRating
                            }
                        }
                    }
                },
                onLikeRecipe = {}
            )
            Spacer(modifier = Modifier.height(20.dp))
            RecipeIngredients(recipe.ingredients)
            Spacer(modifier = Modifier.height(20.dp))
            RecipeInstructions(instructions = recipe.instructions)
        }
    }

//    if (displayVoiceCommandPopUp.value) {
//        VoiceCommandsPopUp(
//            isWhereToViewTextIsVisible = false,
//            isGoBackIconVisible = false,
//            isGoBackIconClicked = {}
//        ) { isCloseIconClicked ->
//            if (isCloseIconClicked) {
//                displayVoiceCommandPopUp.value = false
//            }
//        }
//    }

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
    recipe: Recipe,
    isFavorite: Boolean,
    recipesViewModel: RecipesViewModel,
    rating: Float,
    averageRating: Float,
    onRateRecipe: (Float) -> Unit,
    onLikeRecipe: () -> Unit
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
                Text(
                    text = recipe.description,
                    modifier = Modifier
                        .padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(
                if (recipe.description.isNotEmpty() && recipe.description != "null") 16.dp else 8.dp
            ))
            //Star Rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                FiveStarRate(
                    rating = rating,
                    onRateRecipe = onRateRecipe)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "( ${"%.1f".format(averageRating)} ratings)", fontSize = 12.sp)
            }
            Text(
                text = "Leave a rating", // or edit rating if rated already
                fontSize = 12.sp,
                modifier = Modifier
                    //.clickable { }
                    .padding(top = 8.dp),
                fontStyle = FontStyle.Italic
            )
        }
        Spacer(modifier = Modifier.width(32.dp))
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "7.5k",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                    )
                    Text(
                        text = "likes",
                        modifier = Modifier
                            .offset(y = -(3.dp)),
                        fontSize = 12.sp,
                    )
                }
                Column {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { },
                        tint = Color(0xfff73056)
                    )
                }
            }
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                contentDescription = null,
                tint = if (isFavorite) Color.Yellow else Color.Gray,
                modifier = Modifier
                    .padding(0.dp, top = 8.dp)
                    .size(28.dp)
                    .clickable {
                        recipe.id?.let { id ->
                            recipesViewModel.toggleFavoriteRecipe(id, !isFavorite) {}
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
fun FiveStarRate(
    rating: Float,
    onRateRecipe: (Float) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 1..5) {
            val starColor = if (rating >= i) Color(0xFFFFA500) else Color.Gray
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onRateRecipe(i.toFloat()) }
            )
        }
    }
}

@Composable
fun TimeOrServing(title: String, text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
//            .background(Color(255, 207, 81).copy(.2f), RoundedCornerShape(8.dp))
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = if (title == "Serving") R.drawable.ladle else R.drawable.deadline),
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

//val sharedViewModel = SharedViewModel()
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
//                Box(
//                    modifier = Modifier
//                        .size(8.dp)
//                        .background(Color(255, 207, 81, 255), RoundedCornerShape(50)),
//                )
//                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = ingredient
                )
            }
        }
    }
}

//displayVoiceCommandPopUp: (Boolean) -> Unit,
//recipe: Recipe?, activity: Activity,
//context: Context, cookingAssistantViewModel: CookingAssistantViewModel
@Composable
fun RecipeInstructions(instructions: List<String>) {
//    val cookingAssistantState by cookingAssistantViewModel.cookingAssistantState.collectAsState()

//    val instructions = recipe?.instructions?.sortedBy { it.orderNo }

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

//            val showRecordAudioRationaleDialog = remember {
//                mutableStateOf(false)
//            }
//
//            val showBluetoothConnectRationaleDialog = remember {
//                mutableStateOf(false)
//            }
//
//            val recordAudioPermissionResultLauncher = rememberLauncherForActivityResult(
//                contract = ActivityResultContracts.RequestPermission(),
//                onResult = { isGranted ->
//                    if (isGranted) {
//                        if (!NetworkUtils.isNetworkAvailable(context)) {
//                            Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show()
//                        }
//                        else if(!cookingAssistantState.isCooking) {
//                            if (recipe != null) {
//                                if (recipe.instructions.isNotEmpty()) {
//                                    sharedViewModel.startCookingAssistantService(context, recipe)
//                                }
//                            }
//                        }
//                    }
//                }
//            )

//            IconButton(onClick = {
//                when {
//                    ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
//                            == PackageManager.PERMISSION_GRANTED-> {
//                        if (!NetworkUtils.isNetworkAvailable(context)) {
//                            Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show()
//                        }
//                        else if(!cookingAssistantState.isCooking) {
//                            if (recipe != null) {
//                                if (recipe.instructions.isNotEmpty()) {
//                                    sharedViewModel.startCookingAssistantService(context, recipe)
//                                }
//                            }
//                        } else {
//                            Toast.makeText(context, "Cooking assistant is running.\nTo cook anew, click the Stop icon.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO) -> {
//                        showRecordAudioRationaleDialog.value = true
//                    } else -> {
//                    recordAudioPermissionResultLauncher.launch(Manifest.permission.RECORD_AUDIO)
//                }
//                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    when {
//                        PreferencesManager.getDismissBluetoothConnectPermissionCount(context) < 1 && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
//                                != PackageManager.PERMISSION_GRANTED-> {
//                            showBluetoothConnectRationaleDialog.value = true
//                        }
//                    }
//                }
//            }) {
//                Icon(
//                    painter = painterResource(id = R.drawable.voice_icon),
//                    contentDescription = null,
//                    tint = Color(22, 166, 55, 255),
//                    modifier = Modifier
//                        .size(35.dp)
//                )
//            }

//            IconButton(onClick = {
//                displayVoiceCommandPopUp(true)
//            }) {
//                Icon(
//                    painter = painterResource(id = R.drawable.manual_icon),
//                    contentDescription = null,
//                    tint = Color(22, 166, 55, 255),
//                    modifier = Modifier
//                        .size(25.dp)
//                )
//            }

//            if (showRecordAudioRationaleDialog.value) {
//                PermissionRationaleDialog(
//                    title = "Allow SousChef to access your microphone?",
//                    description = "SousChef uses this to recognize voice commands for hands-free cooking experience.",
//                    onDismiss = { showRecordAudioRationaleDialog.value = false },
//                    onAllow = {
//                        showRecordAudioRationaleDialog.value = false
//                        sharedViewModel.openAppSettings(context)
//                    }
//                )
//            }

//            if (showBluetoothConnectRationaleDialog.value) {
//                PermissionRationaleDialog(
//                    title = "Allow SousChef to access Bluetooth (nearby devices)?",
//                    description = "If you intend to use wireless earphone for voice-activated cooking assistance, this permission must be enabled.",
//                    onDismiss = {
//                        showBluetoothConnectRationaleDialog.value = false
//                        PreferencesManager.incrementDismissBluetoothConnectPermissionCount(context)
//                    },
//                    onAllow = {
//                        showBluetoothConnectRationaleDialog.value = false
//                        sharedViewModel.openAppSettings(context)
//                    }
//                )
//            }
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


