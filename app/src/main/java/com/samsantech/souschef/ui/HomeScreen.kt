package com.samsantech.souschef.ui

import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.ui.components.Header
import com.samsantech.souschef.viewmodel.RecipesViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    recipesViewModel: RecipesViewModel,
    onNavigateToRecipe: () -> Unit,
    paddingValues: PaddingValues,
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
                    .padding(bottom = if (isCooking) 150.dp else 0.dp)
            ) {
                Text(
                    text = "Discover Recipes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                RecipeFeed(navController, recipes, recipesViewModel, favoriteRecipes, onNavigateToRecipe)
            }
        }
    }
}

@Composable
fun RecipeFeed(
    navController: NavController,
    recipes: List<Recipe>,
    recipesViewModel: RecipesViewModel,
    favoriteRecipes: Set<String>,
    onNavigateToRecipe: () -> Unit
) {
    // Horizontal scrolling layout using LazyRow
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(recipes) { recipe ->
            RecipeCard(
                recipe = recipe,
                navController = navController,
                recipesViewModel = recipesViewModel,
                favoriteRecipes = favoriteRecipes,
                onNavigateToRecipe = onNavigateToRecipe
            )
        }
    }
}


@Composable
fun RecipeCard(
    recipe: Recipe,
    navController: NavController,
    recipesViewModel: RecipesViewModel,
    favoriteRecipes: Set<String>,
    onNavigateToRecipe: () -> Unit
) {
    val isFavorite = recipe.id in favoriteRecipes
    //var rating by remember { mutableStateOf(0) }
    val userRating = recipe.userRating ?: 0f
    val averageRating = recipe.averageRating ?: 0f

    // Determine the photo URL based on available keys
    val photoUrl: Uri? = when {
        recipe.photosUrl["portrait"] != null -> Uri.parse("${recipe.photosUrl["portrait"]}")
        recipe.photosUrl["square"] != null -> Uri.parse("${recipe.photosUrl["square"]}")
        recipe.photosUrl["landscape"] != null -> Uri.parse("${recipe.photosUrl["landscape"]}")
        else -> null
    }

    Box(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(245, 245, 220))
            .clickable {
                recipesViewModel.displayRecipe.value = recipe
                onNavigateToRecipe()
            }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .width(200.dp)
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
                    )
                } else {
                    // Placeholder text or icon when no image is available
                    Text(
                        text = "No Image",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recipe.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Learn to cook ${recipe.title}!",
                fontSize = 12.sp,
                color = Color.Gray
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
                                .size(16.dp)
                                .clickable {
                                    recipesViewModel.rateRecipe(recipe.id ?: "", star.toFloat()) {

                                    }
                                }
                        )
                    }
                }
                Text(
                    text = "${String.format("%.1f", averageRating)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                    contentDescription = "Bookmark",
                    tint = if (isFavorite) Color.Yellow else Color.Gray,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            recipe.id?.let { id ->
                                recipesViewModel.toggleFavoriteRecipe(id, !isFavorite) {}
                            }
                        }
                )
            }
        }
    }
}