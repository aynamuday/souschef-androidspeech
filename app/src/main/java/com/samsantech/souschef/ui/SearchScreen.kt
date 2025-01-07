package com.samsantech.souschef.ui

import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.samsantech.souschef.R

@Composable
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


