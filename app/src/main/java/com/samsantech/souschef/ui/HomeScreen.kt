package com.samsantech.souschef.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.samsantech.souschef.R
import com.samsantech.souschef.ui.components.FormOutlinedTextField
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController, paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
    ) {
        Spacer(
            modifier = Modifier
                .background(Color(22, 166, 55, 255))
                .fillMaxWidth()
                .height(100.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 50.dp, start = 20.dp, end = 20.dp,
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SOUSCHEF",
                    fontSize = 28.sp,
                    fontWeight = FontWeight(700),
                    color = Color(255, 207, 81, 255)
                )
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                )
            }

            Box {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recipe Feed Section
            Text(
                text = "Discover Recipes",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            RecipeFeed(navController)

            Spacer(modifier = Modifier.height(20.dp))

            // TikTok Videos Section
            Text(
                text = "Trending Recipes on TikTok",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            TikTokVideoListEmbedded(
                tiktokLinks = listOf(
                    "https://www.tiktok.com/embed/v2/7128330261154090266",
                    "https://www.tiktok.com/embed/v2/7342462804953222406",
                    "https://www.tiktok.com/embed/v2/7252101476917497093"
                )
            )
        }
    }
}

@Composable
fun RecipeFeed(navController: NavController) {
    val recipes = listOf(
        Pair("Spaghetti Carbonara", R.drawable.sphagetti_carbonara),
        Pair("Chicken Adobo", R.drawable.chicken_adobo),
        Pair("Beef Stroganoff", R.drawable.beef_stroganoff),
        Pair("Vegetarian Stir Fry", R.drawable.vegetarian_stirfry),
        Pair("Chocolate Lava Cake", R.drawable.chocolate_lavacake),
        Pair("Garlic Butter Shrimp", R.drawable.garlic_buttershrimp)
    )

    // Horizontal scrolling layout using LazyRow
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between items
    ) {
        items(recipes) { recipe ->
            RecipeCard(recipe = recipe, navController = navController)
        }
    }
}

@Composable
fun RecipeCard(recipe: Pair<String, Int>, navController: NavController) {
    var isFavorited by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(200.dp) // Adjust width to fit the horizontal scroll better
            .clip(RoundedCornerShape(10.dp))
            .background(Color(245, 245, 220))
            .clickable {
                // Navigate to RecipeScreen with recipe name and image resource ID
                navController.navigate("recipe/${recipe.first}/${recipe.second}")
            }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = recipe.second),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recipe.first,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Learn to cook ${recipe.first}!",
                fontSize = 12.sp,
                color = Color.Gray
            )

            // Heart Icon to mark as favorite
            Icon(
                imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorited) Color.Red else Color.Gray,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        isFavorited = !isFavorited
                    }
                    .align(Alignment.End)
            )
        }
    }
}

@Composable
fun TikTokVideoListEmbedded(tiktokLinks: List<String>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        tiktokLinks.forEach { link ->
            TikTokVideoPlayer(link = link)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TikTokVideoPlayer(link: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                loadUrl(link)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.LightGray)
    )
}
