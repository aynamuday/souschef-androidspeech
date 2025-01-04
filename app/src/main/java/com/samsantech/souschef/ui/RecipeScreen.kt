package com.samsantech.souschef.ui

import androidx.compose.runtime.getValue
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import coil3.compose.AsyncImage
import com.samsantech.souschef.R
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.viewmodel.RecipesViewModel

//val sharedViewModel = SharedViewModel()
@Composable
fun RecipeScreen(
    activity: Activity,
    context: Context,
    recipesViewModel: RecipesViewModel,
    onNavigateToPreviousScreen: () -> Unit,
) {

    val recipe: Recipe by recipesViewModel.displayRecipe.collectAsState()
//    val cookingAssistantState by cookingAssistantViewModel.cookingAssistantState.collectAsState()

//    val displayVoiceCommandPopUp = remember {
//        mutableStateOf(false)
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
//            .padding(bottom = if (cookingAssistantState.isCooking) 130.dp else 40.dp)
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
                model = "${recipe.photosUrl["portrait"]}",
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
        }



//        RecipeScreenHeader(recipe.title, onNavigateToPreviousScreen)
        Recipe(recipe, context)
        RecipeIngredients(recipe.ingredients)
//        RecipeInstructions(displayVoiceCommandPopUp = { displayVoiceCommandPopUp.value = it },
//            recipe, activity, context, cookingAssistantViewModel)
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
}

@Composable
fun RecipeScreenHeader(recipeName: String, onNavigateToPreviousScreen: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color(22, 166, 55, 255))
            .padding(start = 20.dp, bottom = 20.dp, end = 20.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigateToPreviousScreen() }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrowback),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                )
            }

            Text(
                text = recipeName,
                color = Color(255, 207, 81, 255),
                fontSize = if (recipeName.length > 17) 24.sp else 28.sp,
                lineHeight = if (recipeName.length > 17) 29.sp else 33.sp,
                fontWeight = FontWeight(600)
            )
        }
    }
}

@Composable
fun Recipe(recipe: Recipe, context: Context) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color(255, 214, 0, 220))
//                .padding(start = 15.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(painter = painterResource(id = R.drawable.timer),
//                contentDescription = null,
//                tint = Color.Black,
//                modifier = Modifier
//                    .size(25.dp)
//            )
//            Spacer(modifier = Modifier.width(10.dp))
//            recipe.cookTimeHr?.let {
//                if (it != 0) {
//                    if (it > 1) {
//                        Text(text = "$it hours",
//                            fontWeight = FontWeight(600)
//                        )
//                    } else {
//                        Text(text = "$it hour",
//                            fontWeight = FontWeight(600)
//                        )
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            recipe.recipe.totalMinutes?.let {
//                if (it != 0) {
//                    Text(text = "$it minutes",
//                        fontWeight = FontWeight(600)
//                    )
//                }
//            }
//        }
        
        Text(
            text = recipe.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )
            Text(
                text = recipe.description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp,)
            )
    }
}

@Composable
fun RecipeIngredients(ingredients: List<String>) {
    Column (
        modifier = Modifier.padding(top = 25.dp, start = 20.dp, end = 20.dp)
    ) {
        Text(
            text = "Ingredients",
            fontWeight = FontWeight(600),
            fontSize = 18.sp,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
        for (ingredient in ingredients) {
            Text(
                text = ingredient,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
        }
    }
}

//@Composable
//fun RecipeInstructionsItems(instruction: InstructionEntity) {
//    Row(
//        modifier = Modifier
//            .padding(top = 10.dp),
//        horizontalArrangement = Arrangement.Start
//    ) {
//        Box(
//            modifier = Modifier
//                .size(25.dp)
//                .clip(CircleShape)
//                .background(Color(255, 207, 81, 255)),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = instruction.orderNo.toString(),
//                color = Color.White,
//                fontWeight = FontWeight(600),
//                fontSize = 14.sp
//            )
//        }
//
//        instruction.description?.let {
//            Text(
//                text = it,
//                modifier = Modifier
//                    .padding(start = 10.dp)
//            )
//        }
//    }
//}

//@Composable
//fun RecipeInstructions(displayVoiceCommandPopUp: (Boolean) -> Unit,
//                       recipe: Recipe?, activity: Activity,
//                       context: Context, cookingAssistantViewModel: CookingAssistantViewModel) {
//    val cookingAssistantState by cookingAssistantViewModel.cookingAssistantState.collectAsState()
//
//    val instructions = recipe?.instructions?.sortedBy { it.orderNo }
//
//    Column (
//        modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(
//                text = "Instructions",
//                fontWeight = FontWeight(600),
//                fontSize = 18.sp
//            )
//
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
//
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
//
//            IconButton(onClick = {
//                displayVoiceCommandPopUp(true)
//            })
//            {
//                Icon(
//                    painter = painterResource(id = R.drawable.manual_icon),
//                    contentDescription = null,
//                    tint = Color(22, 166, 55, 255),
//                    modifier = Modifier
//                        .size(25.dp)
//                )
//            }
//
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
//
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
//        }
//
//        if (instructions != null) {
//            for (instruction in instructions) {
//                RecipeInstructionsItems(instruction)
//            }
//        }
//    }
//}