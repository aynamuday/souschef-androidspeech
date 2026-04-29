package com.samsantech.souschef.ui

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.samsantech.souschef.data.SearchRecipe
import com.samsantech.souschef.ui.components.Rating
import com.samsantech.souschef.ui.components.UserNamePhoto
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.viewmodel.AlgoliaInsightsViewModel
import com.samsantech.souschef.viewmodel.HomeViewModel
import com.samsantech.souschef.viewmodel.RecipesViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.HashMap

@Composable
fun HomeScreen(
    context: Context,
    paddingValues: PaddingValues,
    homeViewModel: HomeViewModel,
    recipesViewModel: RecipesViewModel,
    algoliaInsightsViewModel: AlgoliaInsightsViewModel,
    onNavigateToRecipe: () -> Unit,
    isCooking: Boolean
) {
    val pagingHits = homeViewModel.hitsPaginator.pager.flow.collectAsLazyPagingItems()
    val lazyState = rememberLazyGridState()
    val loadingState = homeViewModel.loadingState

    val favoriteRecipes by recipesViewModel.favoriteRecipes.collectAsState()

    LaunchedEffect(lazyState) {
        snapshotFlow { lazyState.layoutInfo.visibleItemsInfo }
            .collectLatest { visibleItems ->
                if (visibleItems.isNotEmpty()) {
                    val index = visibleItems.first().index
                    val recipe = pagingHits[index]
                    recipe?.objectID?.let { recipesViewModel.setSeenPost(it) }
                }
            }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(start = 12.dp, end = 12.dp, top = 30.dp, bottom = if (isCooking) 120.dp else 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
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
                    text = "No recipes",
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Box {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        state = lazyState
                    ) {
                        items(pagingHits.itemCount) { index ->
                            val item = pagingHits[index] ?: return@items

                            val averageRating = if (item.ratings.isNullOrEmpty()) { 0f } else {
                                item.ratings?.values?.average()?.toFloat() ?: 0f
                            }
                            val isFavorite = favoriteRecipes.any { it.id == item.objectID }
                                RecipeCard(
                                    recipe = item,
                                    isFavorite = isFavorite,
                                    averageRating,
                                    onDisplayRecipe = { id ->
                                        recipesViewModel.getRecipe(id) { isSuccess, err, recipe ->
                                            if (isSuccess) {
                                                if (recipe != null) {
                                                    recipesViewModel.displayRecipe.value = recipe
                                                    onNavigateToRecipe()
                                                }
                                            } else {
                                                Toast
                                                    .makeText(context, err, Toast.LENGTH_LONG)
                                                    .show()
                                            }
                                        }

                                        algoliaInsightsViewModel.sendViewedARecipeEvent(id)
                                    },
                                    onToggleFavorite = { id ->
                                        val photosUrl = item.photosUrl.mapValues { (_, value) -> value.toUri() } as HashMap<String, Uri>
                                        recipesViewModel.toggleFavorite(id, photosUrl, !isFavorite) {
                                            val message = if (isFavorite) {
                                                "Recipe removed from favorites"
                                            } else {
                                                algoliaInsightsViewModel.sendAddedToFavoritesEvent(id)
                                                "Recipe added to favorites"
                                            }
                                            Toast
                                                .makeText(context, message, Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RecipeCard(
    recipe: SearchRecipe,
    isFavorite: Boolean,
    averageRating: Float,
    onDisplayRecipe: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val photoUrl: Uri? = when {
        recipe.photosUrl["portrait"] != null -> Uri.parse("${recipe.photosUrl["portrait"]}")
        recipe.photosUrl["square"] != null -> Uri.parse("${recipe.photosUrl["square"]}")
        recipe.photosUrl["landscape"] != null -> Uri.parse("${recipe.photosUrl["landscape"]}")
        else -> null
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (recipe.objectID != null) {
                    onDisplayRecipe(recipe.objectID!!)
                }
            }
    ) {
        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = if (photoUrl != null) 0.dp else 1.dp,
                    color = if (photoUrl != null) Color.Transparent else Color.Gray,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (photoUrl != null) {
                AsyncImage(
                    model = "$photoUrl",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .align(Alignment.Center)
                )
            } else {
                androidx.compose.material3.Text(
                    text = "No Image",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Icon(
                imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                contentDescription = "Bookmark",
                tint = if (isFavorite) Green else Color.Gray,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        recipe.objectID?.let { id ->
                            onToggleFavorite(id)
                        }
                    }
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Column {
            Rating(averageRating, recipe.ratings?.size ?: 0)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserNamePhoto(
                    photoUri = recipe.userPhotoUrl,
                    userName = recipe.userName,
                    photoSize = 20.dp,
                    fontColor = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f),
                    spacer = 8.dp
                )
            }
        }
    }
}