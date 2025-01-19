package com.samsantech.souschef.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint.Align
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
//<<<<<<< master
import androidx.compose.material.icons.filled.Close
//=======
import androidx.compose.material.icons.filled.Delete
//>>>>>>> nico
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.samsantech.souschef.R
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.ui.components.ColoredButton
import com.samsantech.souschef.ui.components.Dialog
import com.samsantech.souschef.ui.components.DisplayProfileImage
import com.samsantech.souschef.ui.components.Header
import com.samsantech.souschef.ui.components.ProgressSpinner
import com.samsantech.souschef.ui.components.BottomActionMenuPopUp
import com.samsantech.souschef.ui.components.OwnRecipeActionMenu
import com.samsantech.souschef.ui.components.ProfilePhoto
import com.samsantech.souschef.ui.components.RecipeCard
import com.samsantech.souschef.ui.components.TikTokWebView
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher
import com.samsantech.souschef.utils.convertUriToBitmap
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel
import com.samsantech.souschef.viewmodel.RecipesViewModel
import com.samsantech.souschef.viewmodel.UserViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    paddingValues: PaddingValues,
    context: Context,
    userViewModel: UserViewModel,
    ownRecipesViewModel: OwnRecipesViewModel,
    recipesViewModel: RecipesViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToRecipe: () -> Unit,
    onNavigateToCreateRecipeOne: () -> Unit
) {
    val user by userViewModel.user.collectAsState()
    val allRecipes by recipesViewModel.allRecipes.collectAsState()
    val ownRecipes by ownRecipesViewModel.recipes.collectAsState()
    val favoriteRecipes by recipesViewModel.favoriteRecipes.collectAsState(emptyList())

    val favoriteRecipeList = allRecipes.filter { favoriteRecipes.contains(it.id) }

    var loading by remember {
        mutableStateOf(false)
    }
    var showProfileImage by remember {
        mutableStateOf(false)
    }
    var showGetImageOptions by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var show by remember {
        mutableStateOf("recipes")
    }
    var showRecipeActionMenu by remember {
        mutableStateOf(false)
    }
    var recipeWithAction: Recipe? by remember {
        mutableStateOf(null)
    }
    var error:String? by remember {
        mutableStateOf(null)
    }
    var showMenuBar by remember {
        mutableStateOf(false)
    }

    val activityResultLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let {
                imageUri = it.data
            }
        }
    }

    Box {
        Column {
            Header(onClickMenuBar = {
                showMenuBar = true
            })
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(133.dp)
                            .background(Color.White)
                            .clip(CircleShape)
                            .clickable { showProfileImage = true },
                        contentAlignment = Alignment.Center
                    ) {
                        ProfilePhoto(uri = user?.photoUrl, size = 130.dp)
                    }
                    IconButton(
                        onClick = {
                            showGetImageOptions = true
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFFFD600))
                            .align(Alignment.BottomEnd)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                user?.let {
                    Text(
                        text = it.displayName,
                        fontFamily = Konkhmer_Sleokcher,
                        fontSize = 20.sp,
                        color = Color(0xFF16A637),
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                ),
                            )
                        )
                    )
                }
                user?.let {
                    Column(
                        modifier = Modifier
                            .offset(y = -(8.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = it.username,
                            fontStyle = FontStyle.Italic,
                        )
                        Text(
                            text = it.email,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))

//                ColoredButton(onClick = onNavigateToEditProfile, text = "Settings")
//                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ColoredButton(
                        onClick = { show = "recipes" },
                        text = "Your Recipes",
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(12.dp, 12.dp),
                        containerColor = if (show == "recipes") Green else Color.White,
                        contentColor = if (show == "recipes") Color.White else Green,
                        border = if (show == "recipes")  BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.dp, Color(0xFF16A637))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    ColoredButton(
                        onClick = { show = "favorites" },
                        text = "Favorites",
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(12.dp, 12.dp),
                        containerColor = if (show == "favorites") Green else Color.White,
                        contentColor = if (show == "favorites") Color.White else Green,
                        border = if (show == "favorites")  BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.dp, Color(0xFF16A637))
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (show == "recipes") {
                    BoxWithConstraints {
                        val maxWidth = maxWidth

                        if (ownRecipes.isEmpty()) {
                            Text(
                                text = "No recipes to show",
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth(),
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color.Black.copy(.7f),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                FlowRow(
                                    maxItemsInEachRow = 3,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    ownRecipes.forEach { recipe ->
                                        val photoUrl: Uri? = if (recipe.photosUrl["portrait"] != null) {
                                            Uri.parse("${recipe.photosUrl["portrait"]}")
                                        } else if (recipe.photosUrl["square"] != null) {
                                            Uri.parse("${recipe.photosUrl["square"]}")
                                        } else {
                                            Uri.parse("${recipe.photosUrl["landscape"]}")
                                        }

                                        RecipeCard(
                                            photoUrl = photoUrl,
                                            modifier = Modifier
                                                .width((maxWidth / 3) - 4.dp),
                                            onClick = {
                                                recipesViewModel.displayRecipe.value = recipe
                                                onNavigateToRecipe()
                                            },
                                            showKebabMenu = true,
                                            onClickKebabMenu = {
                                                showRecipeActionMenu = !showRecipeActionMenu
                                                recipeWithAction = if (recipeWithAction == null) recipe else null
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (show == "favorites") {
                    BoxWithConstraints {
                        val maxWidth = maxWidth
                        if (favoriteRecipeList.isEmpty()) {
                            Text(
                                text = "No favorites to show",
                                modifier = Modifier.padding(top = 8.dp),
                                fontStyle = FontStyle.Italic,
                                color = Color.Black.copy(.7f),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                FlowRow(
                                    maxItemsInEachRow = 3,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    favoriteRecipeList.forEach { recipe ->
                                        if (recipe.isTikTok) {
                                            Box (modifier = Modifier.clip(RoundedCornerShape(5.dp))) {
                                                val width = (maxWidth / 3) - 4.dp
                                                recipe.postId?.let {
                                                    TikTokWebView(
                                                        postId = it,
                                                        width = width.value.toInt(),
                                                        height = 180
                                                    )
                                                }
                                            }
                                        } else {
                                            val photoUrl: Uri? = if (recipe.photosUrl["portrait"] != null) {
                                                Uri.parse("${recipe.photosUrl["portrait"]}")
                                            } else {
                                                Uri.parse("${recipe.photosUrl["square"]}")
                                            }

                                            Box(modifier = Modifier) {
                                                RecipeCard(
                                                    photoUrl = photoUrl,
                                                    modifier = Modifier
                                                        .width((maxWidth / 3) - 4.dp),
                                                    onClick = {
                                                        recipesViewModel.displayRecipe.value = recipe
                                                        onNavigateToRecipe()
                                                    },
//                                                showKebabMenu = true,
                                                    onClickKebabMenu = {
                                                        //showRecipeActionMenu = !showRecipeActionMenu
                                                        recipeWithAction =
                                                            if (recipeWithAction == null) recipe else null
                                                    }
                                                )

                                                IconButton(
                                                    onClick = {
                                                        recipe.id?.let {
                                                            recipesViewModel.removeFromFavorites(it) { isSuccess ->
                                                                if (isSuccess) {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Recipe removed from favorites",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                } else {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Failed to remove from favorites",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Remove from favorites"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    AnimatedVisibility(
        visible = showMenuBar,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .zIndex(1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(.4f))
                .animateEnterExit(
                    enter = slideInHorizontally(
                        initialOffsetX = {
                            it
                        }
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = {
                            it
                        }
                    )
                )
                .pointerInput(Unit) {
                    detectTapGestures {
                        showMenuBar = false
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(250.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(Color.White)
                    .padding(top = 50.dp)
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable {
                                showMenuBar = false
                            }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))

                val menu = arrayOf<Menu>(
                    Menu("Edit Profile", Icons.Filled.EditNote, onNavigateToEditProfile)
                )


                menu.forEach {
                    Row(
                        modifier = Modifier
                            .clickable(onClick = it.onClick)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = it.title,
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
            }
        }

    }

    if (showGetImageOptions) {
        BottomActionMenuPopUp(
            options = hashMapOf("Upload from Gallery" to R.drawable.images),
            onClick = {
                showGetImageOptions = false
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            },
            onOutsideClick = {
                showGetImageOptions = false
            }
        )
    }

    if (imageUri != null)
    {
        val bitmap: Bitmap? = convertUriToBitmap(context, imageUri!!)

        if (bitmap != null) {
            DisplayProfileImage(
                bitmap = bitmap,
                onCancel = { imageUri = null },
                withCancelButton = true,
                onOkay = {
                    loading = true

                    userViewModel.setProfilePicture(imageUri!!) { _, error ->
                        if (error != null) {
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }

                        loading = false
                        imageUri = null
                    }
                },
                onOkayText = "Set Profile Photo",
            )
        }
    }

    if (showProfileImage) {
        DisplayProfileImage(
            uri = "${user?.photoUrl}",
            onOkay = {
                showProfileImage = false

                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            },
            onOkayText = "Update Profile Photo",
            onBoxClick = { showProfileImage = false },
            withCloseButton = true,
            onCloseClick = { showProfileImage = false }
        )
    }

    OwnRecipeActionMenu(
        showRecipeActionMenu = showRecipeActionMenu,
        setShowRecipeActionMenu = { showRecipeActionMenu = it },
        recipeWithAction = recipeWithAction,
        setRecipeWithAction = { recipeWithAction = it },
        ownRecipesViewModel = ownRecipesViewModel,
        onNavigateToCreateRecipeOne = { onNavigateToCreateRecipeOne() },
        setLoading = {
            loading = it
        }
    )

    if (error != null) {
        Dialog(
            icon = "warning",
            message = error!!,
            onCloseClick = {
                error = null
            }
        )
    }

    if (loading) {
        ProgressSpinner()
    }
}

data class Menu(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)