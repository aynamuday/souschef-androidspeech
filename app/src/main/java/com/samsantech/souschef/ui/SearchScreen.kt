package com.samsantech.souschef.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
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
import com.samsantech.souschef.ui.components.FiveStarRate
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
    paddingValues: PaddingValues,
    searchRecipesViewModel: SearchRecipesViewModel,
    recipesViewModel: RecipesViewModel,
    onNavigateToRecipe: () -> Unit
) {
    val pagingHits = searchRecipesViewModel.hitsPaginator.pager.flow.collectAsLazyPagingItems()
    val gridState by searchRecipesViewModel.gridState.collectAsState()
    val categoriesRowState = rememberLazyListState()
    val loadingState = searchRecipesViewModel.loadingState
    val search by searchRecipesViewModel.search.collectAsState()
    val hasSearched by searchRecipesViewModel.hasSearched.collectAsState()
    var error by remember {
        mutableStateOf<String?>(null)
    }
    var view by remember {
        mutableStateOf("grid")
    }
    var displayBottomActionMenuPopUp by remember {
        mutableStateOf(false)
    }

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
            Column {
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
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    state = categoriesRowState
                ) {
                    items(categories.entries.toList()) {
                        SearchCategoryCard(
                            title = it.key,
                            drawable = it.value,
                            onClick = {
                                searchRecipesViewModel.searchCategory(it.key)
                                searchRecipesViewModel.hasSearched.value = true
                                searchRecipesViewModel.search.value = it.key
                            }
                        )
                    }
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
        .wrapContentSize()
        .clickable {
            onClick()
        }
    ) {
        Image(
            painter = painterResource(id = drawable),
            contentDescription = null,
            modifier = Modifier
                .size(190.dp),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .size(190.dp)
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
                            imageVector = Icons.Filled.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier
                                .size(if (view == "grid") 18.dp else 22.dp)
                                .clickable {

                                },
                        )
                    }
                }
            } else {
                SearchRecipeItem(
                    itemWidth = width,
                    itemHeight = height,
                    item = item,
                    onDisplayRecipe = {
                        item.objectID?.let { onDisplayRecipe(it) }
                    },
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
    view: String = "grid"
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                FiveStarRate(rate = 3.7f, size = if (view == "grid") 12.dp else 14.dp)
                Spacer(modifier = Modifier.width(5.dp))
                androidx.compose.material3.Text(text = "(1)", fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    modifier = Modifier
                        .size(if (view == "grid") 16.dp else 22.dp)
                        .clickable { },
                    tint = Color(0xfff73056)
                )
                Spacer(modifier = Modifier.width(if (view == "grid") 2.dp else 5.dp))
                Text(text = "999k", fontSize = if (view == "grid") 12.sp else 14.sp)
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
                imageVector = Icons.Filled.BookmarkBorder,
                contentDescription = null,
                modifier = Modifier
                    .size(if (view == "grid") 18.dp else 22.dp)
                    .clickable {

                    },
            )
        }
    }
}