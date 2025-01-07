package com.samsantech.souschef.ui

import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
import com.samsantech.souschef.data.SearchRecipe
import com.samsantech.souschef.ui.components.RecipeCard
import com.samsantech.souschef.ui.components.SearchBox
import com.samsantech.souschef.ui.components.UserNamePhoto
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.viewmodel.RecipesViewModel

@Composable
fun SearchScreen(
    paddingValues: PaddingValues,
    recipesViewModel: RecipesViewModel
) {
    val searchBoxState = recipesViewModel.searchBoxState
    val paginator = recipesViewModel.hitsPaginator
    val pagingHits = paginator.flow.collectAsLazyPagingItems()
    val gridState = rememberLazyGridState()
    val loadingState = recipesViewModel.loadingState

    var search by remember {
        mutableStateOf("")
    }
    var hasSearched by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 40.dp)
            .padding(paddingValues)
    ) {
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
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (hasSearched) {
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
                        RecipesList(lazyPagingItems = pagingHits, lazyGridState = gridState, maxWidth = maxWidth)
                    }
                }
            }
        } else {
            Column {
                val categories = mapOf(
                    "Chicken" to R.drawable.chicken, "Pork" to R.drawable.pork, "Beef" to R.drawable.beef, "Seafood" to R.drawable.seafood,
                    "Vegetables" to R.drawable.vegetable, "Fruits" to R.drawable.fruits, "Dessert" to R.drawable.dessert, "Drink" to R.drawable.drinks
                )

                Text(
                    text = "Browse Categories",
                    fontWeight = FontWeight(500),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(categories.entries.toList()) {
                        SearchCategoryCard(title = it.key, drawable = it.value)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchCategoryCard(title: String, drawable: Int) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .wrapContentSize()
        .clickable {

        }
    ) {
        Image(
            painter = painterResource(id = drawable),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .size(150.dp)
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
    maxWidth: Dp
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

            SearchRecipeItem(itemWidth = itemWidth, item =item)
        }
    }
}

@Composable
fun SearchRecipeItem(
    itemWidth: Dp,
    item: SearchRecipe
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

            },
            modifier = Modifier
                .width(itemWidth)
                .height(itemWidth + itemWidth / 3)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            fontWeight = FontWeight(500)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            UserNamePhoto(
                photoUri = item.userPhotoUrl,
                userName = item.userName,
                photoSize = 20.dp,
                fontColor = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
                spacer = 5.dp
            )
            Row(modifier = Modifier.weight(.5f)) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    modifier = Modifier
                        .size(15.dp)
                        .clickable { },
                    tint = Color(0xfff73056)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = "999k", fontSize = 12.sp)
            }
        }
    }
}