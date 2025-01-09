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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.algolia.instantsearch.android.paging3.flow
import com.samsantech.souschef.R
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.data.SearchRecipe
import com.samsantech.souschef.ui.components.Dialog
import com.samsantech.souschef.ui.components.FiveStarRate
import com.samsantech.souschef.ui.components.RecipeCard
import com.samsantech.souschef.ui.components.SearchBox
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
    val searchBoxState = searchRecipesViewModel.searchBoxState
    val paginator = searchRecipesViewModel.hitsPaginator
    val pagingHits = paginator.flow.collectAsLazyPagingItems()
    val gridState = rememberLazyGridState()
    val categoriesRowState = rememberLazyListState()
    val loadingState = searchRecipesViewModel.loadingState

    var search by remember {
        mutableStateOf("")
    }
    var hasSearched by remember {
        mutableStateOf(false)
    }
    var error by remember {
        mutableStateOf<String?>(null)
    }

    BackHandler {
        if (hasSearched) {
            hasSearched = false
            search = ""
        }
    }

    Column(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 40.dp)
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
                            search = ""
                            hasSearched = false
                            searchRecipesViewModel.cancelSearch()
                        }
                )
            }
            SearchBox(
                search = search,
                onValueChange = {
                    search = it
                },
                onSubmit = {
                    search = search.trim()
                    if ((!hasSearched && search != "") || hasSearched) {
                        if (!hasSearched) {
                            hasSearched = true
                        }
                        searchBoxState.setText(search, true)
                        if (search != "") {
                            loadingState.setIsLoading(true)
                        } else {
                            hasSearched = false
                        }
                    }
                },
                clearSearch = {
                    search = ""
                }
            )
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
                                hasSearched = true
                                search = it.key
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
                            }
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
    lazyPagingItems: LazyPagingItems<SearchRecipe>,
    lazyGridState: LazyGridState,
    maxWidth: Dp,
    onDisplayRecipe: (id: String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
        state = lazyGridState
    ) {
        items(lazyPagingItems.itemCount) { index ->
            val item = lazyPagingItems[index] ?: return@items

            val itemWidth = (maxWidth/2) - 8.dp

            if (item.isTiktok == true) {
                println(item)
            } else {
                SearchRecipeItem(
                    itemWidth = itemWidth,
                    item = item,
                    onDisplayRecipe = {
                        item.objectID?.let { onDisplayRecipe(it) }
                    }
                )
            }

        }
    }
}

@Composable
fun SearchRecipeItem(
    itemWidth: Dp,
    item: SearchRecipe,
    onDisplayRecipe: () -> Unit
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
                .height(itemWidth + itemWidth / 10)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            fontWeight = FontWeight(500)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FiveStarRate(rate = 3.7f, size = 12.dp)
                Spacer(modifier = Modifier.width(5.dp))
                androidx.compose.material3.Text(text = "(1)", fontSize = 12.sp)
            }
            Row {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { },
                    tint = Color(0xfff73056)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = "999k", fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserNamePhoto(
                photoUri = item.userPhotoUrl,
                userName = item.userName,
                photoSize = 20.dp,
                fontColor = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
                spacer = 8.dp
            )
            Icon(
                imageVector = Icons.Filled.BookmarkBorder,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .clickable {
                        onDisplayRecipe()
                    },
            )
        }
    }
}