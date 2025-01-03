package com.samsantech.souschef.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.samsantech.souschef.R
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.ui.components.ColoredButton
import com.samsantech.souschef.ui.components.ConfirmDialog
import com.samsantech.souschef.ui.components.Dialog
import com.samsantech.souschef.ui.components.DisplayProfileImage
import com.samsantech.souschef.ui.components.Header
import com.samsantech.souschef.ui.components.ProgressSpinner
import com.samsantech.souschef.ui.components.BottomActionMenuPopUp
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher
import com.samsantech.souschef.utils.OwnRecipeAction
import com.samsantech.souschef.utils.convertUriToBitmap
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel
import com.samsantech.souschef.viewmodel.UserViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    paddingValues: PaddingValues,
    context: Context,
    userViewModel: UserViewModel,
    ownRecipesViewModel: OwnRecipesViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToRecipe: () -> Unit,
    onNavigateToCreateRecipeOne: () -> Unit
) {
    val user by userViewModel.user.collectAsState()
    val ownRecipes by ownRecipesViewModel.recipes.collectAsState()

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
    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }
    var successDelete by remember {
        mutableStateOf(false)
    }
    var error:String? by remember {
        mutableStateOf(null)
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
            Header()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(133.dp)
                            .background(Color.LightGray)
                            .clickable { showProfileImage = true },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = "${user?.photoUrl}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(130.dp)
                                .background(Color.LightGray)
                        )
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
                    Text(
                        text = it.username,
                        fontStyle = FontStyle.Italic
                    )
                }
                user?.let {
                    Text(
                        text = it.email
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                ColoredButton(onClick = onNavigateToEditProfile, text = "Settings")
                Spacer(modifier = Modifier.height(16.dp))

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
                            FlowRow(
                                maxItemsInEachRow = 3,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                ownRecipes.forEachIndexed { index, recipe ->
                                    val photoUrl: Uri? = if (recipe.photosUrl["portrait"] != null) {
                                        Uri.parse("${recipe.photosUrl["portrait"]}")
                                    } else if (recipe.photosUrl["square"] != null) {
                                        Uri.parse("${recipe.photosUrl["square"]}")
                                    } else {
                                        Uri.parse("${recipe.photosUrl["landscape"]}")
                                    }

                                    Box(
                                        modifier = Modifier
                                            .zIndex(-1f)
                                            .height(180.dp)
                                            .width((maxWidth / 3) - 10.dp)
                                            .background(Color.White)
                                            .border(
                                                if (photoUrl != null) 0.dp else 1.dp,
                                                if (photoUrl != null) Color.Transparent else Color.Gray,
                                                RoundedCornerShape(5.dp)
                                            )
                                            .clickable { },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = "$photoUrl",
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(5.dp))
                                        )
                                        Icon(
                                            imageVector = Icons.Filled.MoreVert,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .offset(5.dp, 3.dp)
                                                .clip(RoundedCornerShape(100))
                                                .clickable {
                                                    showRecipeActionMenu = !showRecipeActionMenu
                                                    recipeWithAction =
                                                        if (recipeWithAction == null) recipe else null
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else if (show == "favorites") {
                    Box {
                        if (true) {
                            Text(
                                text = "No favorites to show",
                                modifier = Modifier.padding(top = 20.dp),
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color.Black.copy(.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
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

    if (showRecipeActionMenu) {
        BottomActionMenuPopUp(
            options = hashMapOf("Edit" to R.drawable.pencil_icon, "Delete" to R.drawable.delete_icon),
            onClick = { key ->
                if (key == "Delete") {
                    showDeleteConfirmation = true
                    showRecipeActionMenu = false
                } else if (key == "Edit") {
                    showRecipeActionMenu = false
                    ownRecipesViewModel.action.value = OwnRecipeAction.EDIT
                    ownRecipesViewModel.recipe.value = recipeWithAction!!
                    ownRecipesViewModel.originalData.value = recipeWithAction!!
                    onNavigateToCreateRecipeOne()
                }
            },
            onOutsideClick = {
                showRecipeActionMenu = false
                recipeWithAction = null
            }
        )
    }

    if (showDeleteConfirmation) {
        ConfirmDialog(
            message = "Are you sure you want to delete this recipe?",
            buttonOkayName = "Yes",
            onClickCancel = {
                showDeleteConfirmation = false
                recipeWithAction = null
            },
            onClickOkay = {
                showDeleteConfirmation = false

                if (recipeWithAction != null) {
                    loading = true

                    ownRecipesViewModel.deleteRecipe(recipeWithAction!!.id!!, recipeWithAction!!.photosUrl) { isSuccess, err ->
                        loading = false

                        if (isSuccess) {
                            successDelete = true
                        } else {
                            error = err
                        }
                    }

                    recipeWithAction = null
                }
            }
        )
    }

    if (successDelete) {
        Dialog(
            icon = "success",
            message = "Recipe successfully deleted!",
            onCloseClick = {
                successDelete = false
            }
        )
    }

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