package com.samsantech.souschef.ui

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.samsantech.souschef.R
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.data.SearchRecipe
import com.samsantech.souschef.ui.components.Dialog
import com.samsantech.souschef.ui.components.RecipeCard
import com.samsantech.souschef.ui.components.SearchBottomActionMenuPopUp
import com.samsantech.souschef.ui.components.SearchBox
import com.samsantech.souschef.ui.components.TikTokWebView
import com.samsantech.souschef.ui.components.UserNamePhoto
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.viewmodel.RecipesViewModel
import com.samsantech.souschef.viewmodel.SearchRecipesViewModel

@Composable
fun SearchScreen(
    context: Context,
    paddingValues: PaddingValues,
    searchRecipesViewModel: SearchRecipesViewModel,
    recipesViewModel: RecipesViewModel,
    onNavigateToRecipe: () -> Unit,
//    isCooking: Boolean
) {
    val pagingHits = searchRecipesViewModel.hitsPaginator.pager.flow.collectAsLazyPagingItems()
    val gridState by searchRecipesViewModel.gridState.collectAsState()
    val categoriesRowState = rememberLazyListState()
    val loadingState = searchRecipesViewModel.loadingState
    val search by searchRecipesViewModel.search.collectAsState()
    val hasSearched by searchRecipesViewModel.hasSearched.collectAsState()
    var error by remember { mutableStateOf<String?>(null) }
    var view by remember { mutableStateOf("grid") }
    var displayBottomActionMenuPopUp by remember { mutableStateOf(false) }

    val favoriteRecipes by recipesViewModel.favoriteRecipes.collectAsState()

    BackHandler {
        if (hasSearched) {
            searchRecipesViewModel.hasSearched.value = false
            searchRecipesViewModel.search.value = ""
        }
    }

    Column(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, top = 40.dp, bottom = 20.dp)
            .padding(paddingValues)
//            .padding(bottom = if (isCooking) 150.dp else 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (hasSearched) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            searchRecipesViewModel.search.value = ""
                            searchRecipesViewModel.hasSearched.value = false
                            searchRecipesViewModel.cancelSearch()
                            searchRecipesViewModel.clearCategories()
                        }
                )
            }
            SearchBox(
                search = search,
                onValueChange = {
                    searchRecipesViewModel.search.value = it
                },
                onSubmit = {
                    searchRecipesViewModel.clearCategories()

                    searchRecipesViewModel.search.value = search.trim()
                    if ((!hasSearched && search != "") || hasSearched) {
                        if (!hasSearched) {
                            searchRecipesViewModel.hasSearched.value = true
                        }
                        searchRecipesViewModel.search(search)
                        if (search != "") {
                            loadingState.setIsLoading(true)
                        } else {
                            searchRecipesViewModel.hasSearched.value = false
                        }
                    }
                },
                clearSearch = {
                    searchRecipesViewModel.search.value = ""
                },
                modifier = Modifier.weight(1f)
            )
            if (hasSearched) {
                Icon(
                    imageVector = if (view.lowercase() == "grid") Icons.Filled.GridView else Icons.AutoMirrored.Filled.List,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            displayBottomActionMenuPopUp = true
                        },
                    tint = Color.Black.copy(.6f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (!hasSearched) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                val categories = mapOf(
                    "Chicken" to R.drawable.chicken, "Pork" to R.drawable.pork, "Beef" to R.drawable.beef, "Seafood" to R.drawable.seafood,
                    "Vegetables" to R.drawable.vegetable, "Fruits" to R.drawable.fruits, "Dessert" to R.drawable.dessert, "Drink" to R.drawable.drinks
                )

                Text(
                    text = "Browse Categories",
                    fontWeight = FontWeight(500),
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                categories.forEach {
                    SearchCategoryCard(
                        title = it.key,
                        drawable = it.value,
                        onClick = {
                            searchRecipesViewModel.searchCategory(it.key)
                            searchRecipesViewModel.hasSearched.value = true
                            searchRecipesViewModel.search.value = it.key
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        } else {
            if (loadingState.loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(25.dp),
                        color = Color.White,
                        trackColor = Green,
                        strokeWidth = 3.dp
                    )
                }
            } else {
                if (pagingHits.itemCount <= 0) {
                    Text(
                        text = "No recipes found",
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    BoxWithConstraints {
                        RecipesList(
                            lazyPagingItems = pagingHits,
                            lazyGridState = gridState,
                            maxWidth = maxWidth,
                            maxHeight = maxHeight,
                            onDisplayRecipe = { id ->
                                recipesViewModel.displayRecipe.value = Recipe()

                                recipesViewModel.getRecipe(id) { isSuccess, _, recipe ->
                                    if (isSuccess) {
                                        if (recipe != null) {
                                            recipesViewModel.displayRecipe.value = recipe
                                        }
                                        onNavigateToRecipe()
                                    } else {
                                        error = "A problem occurred while getting the recipe information. Please try again later."
                                    }
                                }
                            },
                            favoriteRecipes = favoriteRecipes,
                            recipesViewModel = recipesViewModel,
                            onToggleFavorite = { recipeId ->
                                val isAdd = !favoriteRecipes.contains(recipeId)

                                recipesViewModel.toggleFavorite(
                                    recipeId,
                                    isAdd
                                ) { isSuccess ->
                                    if (!isSuccess) {
                                        error = "Failed to toggle favorite status. Please try again later."
                                    }

                                    val message = if (!isAdd) {
                                        "Recipe removed from favorites"
                                    } else {
                                        "Recipe added to favorites"
                                    }
                                    Toast
                                        .makeText(context, message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            },
                            view = view
                        )
                    }
                }
            }
        }
    }

    if (error != null) {
        Dialog(icon = "warning", message = error!!) {
            error = null
        }
    }

    if (displayBottomActionMenuPopUp) {
        SearchBottomActionMenuPopUp(
            view = view,
            onSelectView = {
                view = it
            },
            onOutsideClick = {
                displayBottomActionMenuPopUp = false
            }
        )
    }
}

@Composable
fun SearchCategoryCard(title: String, drawable: Int, onClick: () -> Unit) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .fillMaxWidth()
        .clickable {
            onClick()
        }
    ) {
        Image(
            painter = painterResource(id = drawable),
            contentDescription = null,
            modifier = Modifier
                .height(150.dp),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .background(Color.Black.copy(.2f))
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight(600)
            )
        }
    }
}

@Composable
fun RecipesList(
    modifier: Modifier = Modifier,
    maxWidth: Dp,
    maxHeight: Dp,
    lazyPagingItems: LazyPagingItems<SearchRecipe>,
    lazyGridState: LazyGridState,
    onDisplayRecipe: (id: String) -> Unit,
    favoriteRecipes: Set<String>,
    recipesViewModel: RecipesViewModel,
    onToggleFavorite: (String) -> Unit,
    view: String = "grid" // or list
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(if (view == "grid") 2 else 1),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(if (view == "grid") 10.dp else 20.dp),
        modifier = modifier,
        state = lazyGridState
    ) {
        items(lazyPagingItems.itemCount) { index ->
            val item = lazyPagingItems[index] ?: return@items

            val width = if (view == "grid") ((maxWidth/2) - 8.dp) else maxWidth
            val height = if (view == "grid") (width/2)+width else 300.dp

            val recipe: Recipe by recipesViewModel.displayRecipe.collectAsState()

            val userRating = remember { mutableStateOf(recipe.userRating ?: 0f) }
            val averageRating = remember { mutableStateOf(recipe.averageRating ?: 0f) }

            if (item.isTikTok == true) {
                Column {
                    Box(modifier = Modifier.clip(RoundedCornerShape(10.dp))) {
                        if (view == "grid") {
                            TikTokWebView(
                                postId = item.postId,
                                width = width.value.toInt(),
                                height = height.value.toInt()
                            )
                        } else {
                            TikTokWebView(
                                postId = item.postId,
                                width = width.value.toInt(),
                                height = maxHeight.value.toInt()-32
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = item.title,
                            fontWeight = FontWeight(500),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                            fontSize = if (view == "grid") 14.sp else 16.sp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = if (favoriteRecipes.contains(item.objectID)) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                            contentDescription = "Bookmark",
                            tint = if (favoriteRecipes.contains(item.objectID)) Green else Color.Gray,
                            modifier = Modifier
                                .size(if (view == "grid") 18.dp else 22.dp)
                                .clickable {
                                    onToggleFavorite(item.objectID!!)
                                },
                        )
                    }
                }
            } else {
                SearchRecipeItem(
                    itemWidth = width,
                    itemHeight = height,
                    item = item,
                    rating = userRating.value,
                    averageRating = averageRating.value,
                    onRateRecipe = { newRating ->
                        recipe.id?.let {
                            recipesViewModel.rateRecipe(it, newRating) { success, updatedAverageRating ->
                                if (success) {
                                    userRating.value = newRating
                                    averageRating.value = (updatedAverageRating ?: averageRating.value) as Float
                                }
                            }
                        }
                    },
                    onDisplayRecipe = {
                        item.objectID?.let { onDisplayRecipe(it) }
                    },
                    isFavorite = favoriteRecipes.contains(item.objectID),
                    onToggleFavorite = { onToggleFavorite(item.objectID!!) },
                    view = view
                )
            }

        }
    }
}

@Composable
fun SearchRecipeItem(
    itemWidth: Dp,
    itemHeight: Dp,
    item: SearchRecipe,
    onDisplayRecipe: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    view: String = "grid",
    rating: Float,
    averageRating: Float,
    onRateRecipe: (Float) -> Unit,
) {
    val photoUri = if(item.photosUrl["portrait"] != null && item.photosUrl["portrait"] != "") {
        Uri.parse("${item.photosUrl["portrait"]}")
    } else {
        Uri.parse("${item.photosUrl["square"]}")
    }

    Column {
        RecipeCard(
            photoUrl = photoUri,
            onClick = {
                onDisplayRecipe()
            },
            modifier = Modifier
                .width(itemWidth)
                .height(itemHeight)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            fontWeight = FontWeight(500),
            fontSize = if (view == "grid") 14.sp else 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                FiveStar(
//                    rating = rating,
//                    onRateRecipe = { newRating ->
//                        onRateRecipe(newRating) // Pass the updated rating
//                    }
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "( ${"%.1f".format(averageRating)} )",
//                    fontSize = 12.sp
//                )
//            }
            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    imageVector = Icons.Filled.Favorite,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(if (view == "grid") 16.dp else 22.dp)
//                        .clickable {
//                            //onToggleFavorite()
//                        },
//                    //tint = if (isFavorite) Color(0xfff73056) else Color.Gray
//                )
//                Spacer(modifier = Modifier.width(if (view == "grid") 2.dp else 5.dp))
//                Text(text = "999k", fontSize = if (view == "grid") 12.sp else 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserNamePhoto(
                photoUri = item.userPhotoUrl,
                userName = item.userName,
                photoSize = if (view == "grid") 20.dp else 26.dp,
                fontColor = Color.Gray,
                fontSize = if (view == "grid") 12.sp else 14.sp,
                modifier = Modifier.weight(1f),
                spacer = 8.dp
            )
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                contentDescription = "Bookmark",
                tint = if (isFavorite) Green else Color.Gray,
                modifier = Modifier
                    .size(if (view == "grid") 18.dp else 22.dp)
                    .clickable {
                        onToggleFavorite()
                    },
            )
        }
    }
}

@Composable
fun FiveStar(
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
                    .size(14.dp)
                    .clickable { onRateRecipe(i.toFloat()) }
            )
        }
    }
}
