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
import com.samsantech.souschef.viewmodel.SearchRecipesViewModel

@Composable
//<<<<<<< master
fun SearchScreen(
    paddingValues: PaddingValues,
    searchRecipesViewModel: SearchRecipesViewModel
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
//=======
fun SearchScreen(paddingValues: PaddingValues) {
//    var searchQuery by remember { mutableStateOf("") }
//    val allRecipes = listOf(
//        Recipe("Spaghetti Carbonara", R.drawable.sphagetti_carbonara),
//        Recipe("Chicken Adobo", R.drawable.chicken_adobo),
//        Recipe("Beef Stroganoff", R.drawable.beef_stroganoff),
//        Recipe("Vegetarian Stir Fry", R.drawable.vegetarian_stirfry),
//        Recipe("Chocolate Lava Cake", R.drawable.chocolate_lavacake),
//        Recipe("Garlic Butter Shrimp", R.drawable.garlic_buttershrimp)
//    )
//    val allVideos = listOf(
//        "https://www.tiktok.com/embed/v2/7128330261154090266",
//        "https://www.tiktok.com/embed/v2/7342462804953222406",
//        "https://www.tiktok.com/embed/v2/7252101476917497093"
//    )
//
//    // Function to filter recipes and videos based on search query
//    val filteredRecipes = allRecipes.filter { it.name.contains(searchQuery, ignoreCase = true) }
//    val filteredVideos = allVideos.filter { it.contains(searchQuery, ignoreCase = true) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(paddingValues)
//    ) {
//        Spacer(
//            modifier = Modifier
//                .background(Color(22, 166, 55, 255))
//                .fillMaxWidth()
//                .height(100.dp)
//        )
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(
//                    top = 50.dp, start = 20.dp, end = 20.dp
//                )
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = "SOUSCHEF",
//                    fontSize = 28.sp,
//                    fontWeight = FontWeight(700),
//                    color = Color(255, 207, 81, 255)
//                )
//                Icon(
//                    imageVector = Icons.Filled.Menu,
//                    contentDescription = null,
//                    tint = Color.White,
//                    modifier = Modifier
//                        .size(40.dp)
//                )
//            }
//
//            // Search bar for entering the search query
//            Spacer(modifier = Modifier.height(20.dp))
//            SearchBar(searchQuery = searchQuery, onSearchQueryChanged = { searchQuery = it })
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // Display filtered recipes
//            if (filteredRecipes.isNotEmpty()) {
//                Text(
//                    text = "Recipes",
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom = 10.dp)
//                )
//                RecipeFeed(recipes = filteredRecipes)
//            }
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // Display filtered videos
//            if (filteredVideos.isNotEmpty()) {
//                Text(
//                    text = "Videos",
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom = 10.dp)
//                )
//                TikTokVideoListEmbedded(tiktokLinks = filteredVideos)
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SearchBar(searchQuery: String, onSearchQueryChanged: (String) -> Unit) {
//    TextField(
//        value = searchQuery,
//        onValueChange = onSearchQueryChanged,
//        placeholder = { Text("Search for recipes or videos") },
//        leadingIcon = {
//            Icon(
//                imageVector = Icons.Filled.Search,
//                contentDescription = "Search Icon",
//                tint = Color.Black
//            )
//        },
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp)
//            .clip(RoundedCornerShape(50.dp)),
//        colors = TextFieldDefaults.textFieldColors(
//            //containerColor = Color.Gray,
//            focusedIndicatorColor = Color(22, 166, 55, 255),
//            unfocusedIndicatorColor = Color.Transparent
//        ),
//        singleLine = true
//    )
//}
//
//
//@Composable
//fun RecipeFeed(recipes: List<Recipe>) {
//    Column {
//        recipes.forEach { recipe ->
//            RecipeCard(recipe)
//            Spacer(modifier = Modifier.height(15.dp))
//        }
//    }
//}
//
//@Composable
//fun RecipeCard(recipe: Recipe) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(10.dp))
//            .background(Color(245, 245, 220))
//            .clickable {}
//    ) {
//        Row(
//            modifier = Modifier.padding(15.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(recipe.imageRes),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(RoundedCornerShape(10.dp)),
//                contentScale = ContentScale.Crop
//            )
//
//            Spacer(modifier = Modifier.width(15.dp))
//
//            Column {
//                Text(
//                    text = recipe.name,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = "Learn to make ${recipe.name}!",
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun TikTokVideoListEmbeddedSearch(tiktokLinks: List<String>) {
//    Column(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        tiktokLinks.forEach { link ->
//            TikTokVideoPlayerSearch(link = link)
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//    }
//}
//
//@Composable
//fun TikTokVideoPlayerSearch(link: String) {
//    AndroidView(
//        factory = { context ->
//            WebView(context).apply {
//                settings.javaScriptEnabled = true
//                settings.domStorageEnabled = true
//                loadUrl(link)
//            }
//        },
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(10.dp))
//            .background(Color.LightGray)
//    )
//}
//
//data class Recipe(val name: String, val imageRes: Int)
}

//>>>>>>> nico

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