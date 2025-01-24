package com.samsantech.souschef.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.ui.components.TikTokWebView
import com.samsantech.souschef.ui.components.UserNamePhoto
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.viewmodel.RecipesViewModel
import com.samsantech.souschef.viewmodel.SearchRecipesViewModel
import com.samsantech.souschef.ui.components.Header

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    recipesViewModel: RecipesViewModel,
    onNavigateToRecipe: () -> Unit,
    isCooking: Boolean
) {
    val recipes by recipesViewModel.allRecipes.collectAsState()
    val favoriteRecipes by recipesViewModel.favoriteRecipes.collectAsState()

    Box {
        Column {
            Header()

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(bottom = if (isCooking) 120.dp else 0.dp)
            ) {

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(recipes) { recipe ->
                            if (recipe.isTikTok) {
                                BoxWithConstraints {
                                    val maxWidth = maxWidth
                                    Column {
                                        Box(modifier = Modifier.clip(RoundedCornerShape(10.dp))) {
                                            val width = maxWidth
                                            recipe.postId?.let {
                                                TikTokWebView(
                                                    postId = it,
                                                    width = width.value.toInt(),
                                                    height = 400
                                                )
                                            }
                                        }
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = recipe.title,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))

                                            Icon(
                                                imageVector = if (favoriteRecipes.contains(recipe.id)) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                                                contentDescription = "Bookmark",
                                                tint = if (favoriteRecipes.contains(recipe.id)) Green else Color.Gray,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clickable {
                                                        recipe.id?.let { id ->
                                                            recipesViewModel.toggleFavoriteRecipe(
                                                                id,
                                                                !favoriteRecipes.contains(id)
                                                            ) {}
                                                        }
                                                    }
                                            )
                                        }
                                    }
                                }
                            } else {
                                RecipeCard(
                                    recipe = recipe,
                                    recipesViewModel = recipesViewModel,
                                    favoriteRecipes = favoriteRecipes
                                ) {
                                    onNavigateToRecipe()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RecipeCard(
    recipe: Recipe,
    recipesViewModel: RecipesViewModel,
    favoriteRecipes: Set<String>,
    onNavigateToRecipe: () -> Unit
) {
    val isFavorite = recipe.id in favoriteRecipes
    val userRating = recipe.userRating ?: 0f
    val averageRating = recipe.averageRating ?: 0f

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
                recipesViewModel.displayRecipe.value = recipe
                onNavigateToRecipe()
            }
    ) {
        Box(
            modifier = Modifier
                .height(400.dp)
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
                Text(
                    text = "No Image",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
            }
        }

        Text(
            text = recipe.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                (1..5).forEach { star ->
                    Icon(
                        imageVector = if (star <= userRating) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Rate $star stars",
                        tint = if (star <= userRating) Color(0xFFFFA500) else Color.Gray,
                        modifier = Modifier
                            .size(12.dp)
                            .clickable {
                                recipesViewModel.rateRecipe(
                                    recipe.id ?: "",
                                    star.toFloat()
                                ) { _, _ -> }
                            }
                    )
                }
            }
            Text(
                text = String.format("%.1f", averageRating),
                fontSize = 12.sp,
                color = Color.Gray
            )

            Icon(
                imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                contentDescription = "Bookmark",
                tint = if (isFavorite) Green else Color.Gray,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        recipe.id?.let { id ->
                            recipesViewModel.toggleFavoriteRecipe(id, !isFavorite) {}
                        }
                    }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

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