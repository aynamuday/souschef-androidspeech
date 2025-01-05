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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.samsantech.souschef.R
import com.samsantech.souschef.Recipe
import com.samsantech.souschef.ui.components.FormOutlinedTextField
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController, paddingValues: PaddingValues) {
//    BackHandler {
//        activity.finish()
//    }
//    Scaffold(
//        bottomBar = {
//            BottomNavigationBar()
//        }
//    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
//            .pointerInput(Unit) {
//                detectTapGestures {
//                    isFocused = false
//                    isExpanded = false
//                }
//            }
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
//                    bottom = if (cookingAssistantState.isCooking) 110.dp else 20.dp
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
//                        .padding(end = 10.dp)
                            .size(40.dp)
//                        .clickable { isExpanded = true }
                    )
                }


//                Spacer(modifier = Modifier.height(10.dp))

//                var searchInput by remember {
//                    mutableStateOf("")
//                }

//            SearchBar()
//                FormOutlinedTextField(value = "", onValueChange = {}, )

                Box {
                    Column {
                        Spacer(modifier = Modifier.height(20.dp))
//                    ImageSlider(context, allRecipes, onNavigateToRecipe)
                    }
//                var searchResults by remember { mutableStateOf(emptyList<Recipe>()) }

//                recipeViewModel.getSearchRecipesResults(searchInput) { recipes ->
//                    if (recipes != null) {
//                        searchResults = recipes
//                    }
//                }
//                if (isFocused) {
//                    Column(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(10.dp))
//                            .background(Color(255, 207, 81, 255))
//                            .padding(top = 1.dp)
//                            .heightIn(max = 200.dp)
//                            .verticalScroll(rememberScrollState())
//                    ) {
//                        searchResults.forEach{ recipe ->
//                            SearchResultsItem(context, recipe, onNavigateToRecipe)
//                        }
//                    }
//                }

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
//    }
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

    Column {
        recipes.forEach { recipe ->
            RecipeCard(recipe = recipe, navController = navController)
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
fun RecipeCard(recipe: Pair<String, Int>, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(245, 245, 220))
            .clickable {
                // Navigate to RecipeScreen with recipe name and image resource ID
                navController.navigate("recipe/${recipe.first}/${recipe.second}")
            }
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = recipe.second),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(15.dp))

            Column {
                Text(
                    text = recipe.first,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Learn to cook ${recipe.first}!",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
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
data class RecipeData(val name: String, val imageRes: Int)

//@Composable
//fun DisplayUserGuide(context: Context) {
//    val displayUserGuidePopUp = remember {
//        mutableStateOf(false)
//    }
//
//    LaunchedEffect(Unit) {
//        delay(100)
//        displayUserGuidePopUp.value = true
//    }
//
////    if (displayUserGuidePopUp.value &&
////        PreferencesManager.getAppOpenCount(context) <= 3 &&
////        PreferencesManager.getLeaveHomeCount(context) == 0) {
////        UserGuidePopUpUi()
////    }
//}
//
//@Composable
//fun MenuBarItem(name: String, iconDrawable: Int, onNavigateToScreen: () -> Unit) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onNavigateToScreen() }
//            .padding(horizontal = 20.dp, vertical = 10.dp)
//    ) {
//        Icon(
//            painter = painterResource(id = iconDrawable),
//            contentDescription = null,
//            modifier = Modifier
//                .size(15.dp)
//        )
//        Spacer(modifier = Modifier.width(10.dp))
//        Text(
//            text = name,
//            fontSize = 18.sp
//        )
//    }
//}
//
//@Composable
//fun MenuBar(onNavigateToUserGuideScreen: () -> Unit,
//            onNavigateToReferencesScreen: () -> Unit,
//            onNavigateToAboutUsScreen: () -> Unit,
//            isExpanded: (Boolean) -> Unit) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize(),
//        contentAlignment = Alignment.TopEnd
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxHeight()
//                .width(200.dp)
//                .background(Color.White)
//                .zIndex(3f)
//                .pointerInput(Unit)
//                {
//                    awaitPointerEventScope {
//                        while (true) {
//                            awaitPointerEvent()
//                        }
//                    }
//                }
//        ) {
//            Spacer(modifier = Modifier.height(20.dp))
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(20.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.Menu,
//                    contentDescription = null,
//                    tint = Color.Black,
//                    modifier = Modifier
//                        .padding(top = 18.dp, end = 10.dp)
//                        .size(25.dp)
//                        .clickable { isExpanded(false) }
//                        .align(Alignment.TopEnd)
//                )
//            }
//            Spacer(modifier = Modifier.height(20.dp))
////            MenuBarItem("User Guide", R.drawable.manual_icon, onNavigateToUserGuideScreen)
////            MenuBarItem("References", R.drawable.link_icon, onNavigateToReferencesScreen)
////            MenuBarItem("About us", R.drawable.group_icon, onNavigateToAboutUsScreen)
//        }
//    }
//}
//
//@Composable
//fun SearchResultsItem() {
////    context: Context, recipe: Recipe, onNavigateToRecipe: (recipeId: Int) -> Unit
//    Row(
//        modifier = Modifier
////            .clickable { recipe.recipe.id?.let { onNavigateToRecipe(it) } }
//            .padding(start = 1.dp, bottom = 1.dp, end = 1.dp)
//            .fillMaxWidth()
//            .background(Color.White)
//            .padding(start = 15.dp, top = 12.dp, end = 12.dp, bottom = 12.dp)
//    ) {
////        Image(
////            bitmap = BitmapFactory.decodeStream(recipe.recipe.imagePath?.let { context.assets.open(it) }).asImageBitmap(),
////            contentDescription = null,
////            contentScale = ContentScale.Crop,
////            modifier = Modifier
////                .height(50.dp)
////                .width(50.dp)
////                .clip(RoundedCornerShape(5.dp))
////        )
////        Column {
////            recipe.recipe.name?.let {
////                Text(
////                    text = it,
////                    modifier = Modifier
////                        .padding(start = 15.dp)
////                )
////            }
////            Spacer(modifier = Modifier.height(5.dp))
////            Row {
////                recipe.recipe.totalHours?.let { totalHours ->
////                    if (totalHours != 0) {
////                        if(totalHours > 1) {
////                            Text(
////                                text = "$totalHours hours",
////                                fontSize = 14.sp,
////                                modifier = Modifier
////                                    .padding(start = 15.dp, end = 5.dp)
////                            )
////                        } else {
////                            Text(
////                                text = "$totalHours hour",
////                                fontSize = 14.sp,
////                                modifier = Modifier
////                                    .padding(start = 15.dp, end = 5.dp)
////                            )
////                        }
////                        recipe.recipe.totalMinutes?.let { totalMinutes ->
////                            if (totalMinutes != 0) {
////                                Text(
////                                    text = "$totalMinutes minutes",
////                                    fontSize = 14.sp
////                                )
////                            }
////                        }
////                    } else {
////                        recipe.recipe.totalMinutes?.let { totalMinutes ->
////                            if (totalMinutes != 0) {
////                                Text(
////                                    text = "$totalMinutes minutes",
////                                    fontSize = 14.sp,
////                                    modifier = Modifier
////                                        .padding(start = 15.dp)
////                                )
////                            }
////                        }
////                    }
////                }
////            }
////        }
//    }
//}
//
//@Composable
//fun SearchBar() {
////    focus: (isFocused: Boolean) -> Unit,search: (searchInput: String) -> Unit
//    var searchInput by remember {
//        mutableStateOf("")
//    }
//
////    search(searchInput)
//
//    Box(
//        modifier = Modifier
//            .clip(RoundedCornerShape(8.dp))
//            .background(Color.LightGray)
//            .padding(0.5.dp)
//    ) {
//        BasicTextField(
//            value = searchInput,
//            onValueChange = { input ->
//                searchInput = input.trimStart()
////                focus(true)
//            },
//            singleLine = true,
//            textStyle = TextStyle(fontSize = 16.sp),
//            modifier = Modifier
//                .background(Color.White)
//                .padding(10.dp)
//                .height(20.sp.value.dp)
//                .fillMaxWidth(),
//            decorationBox = { innerTextField ->
//                if (searchInput.isEmpty()) {
//                    Text(
//                        text = "Search",
//                        color = Color.Gray.copy(alpha = 0.5f)
//                    )
//                }
//                innerTextField()
//            }
//        )
//    }
//}
//
//@Composable
//fun ImageSliderItem() {
////    context: Context, recipe: Recipe, onNavigateToRecipe: () -> Unit
////    Image(
////        bitmap = BitmapFactory.decodeStream(recipe.recipe.imagePath?.let { context.assets.open(it) }).asImageBitmap(),
////        contentDescription = null,
////        contentScale = ContentScale.Crop,
////        modifier = Modifier
////            .fillMaxWidth()
////            .height(190.dp)
////            .clip(RoundedCornerShape(30.dp))
////            .clickable { onNavigateToRecipe() }
////    )
//}
//
//@Composable
//fun ImageSliderIndicator(color: Color) {
//    Box(
//        modifier = Modifier
//            .clip(CircleShape)
//            .background(color)
//            .size(5.dp)
//    )
//}
//
//@Composable
//fun ImageSlider() {
////    context: Context, recipes: List<Recipe>?, onNavigateToRecipe: (recipeId: Int) -> Unit
////    val index = remember { recipes?.indices?.let { mutableIntStateOf(it.random()) } }
////    val isTrue = remember { mutableStateOf(false) }
//
////    if (recipes != null) {
////        if (recipes.isNotEmpty()) {
////            LaunchedEffect(key1 = Unit) {
////                while (true) {
////                    delay(3000)
////                    if (index != null) {
////                        index.intValue = (recipes.indices.random())
////                    }
////                    isTrue.value = !isTrue.value
////                }
////            }
////        }
////    }
//
////    Column(
////        modifier = Modifier.fillMaxWidth()
////    ) {
////        if (recipes != null) {
////            Box(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .height(190.dp)
////                    .clip(RoundedCornerShape(30.dp))
////                    .border(if (recipes.isEmpty()) 2.dp else (-1).dp, Color.White)
////            ) {
////                if (recipes.isNotEmpty()) {
////                    val recipe = recipes[index?.intValue!!]
////                    ImageSliderItem(context, recipe) { recipe.recipe.id?.let { onNavigateToRecipe(it) } }
////                }
////            }
////        }
////
////        Row(
////            modifier = Modifier
////                .fillMaxWidth()
////                .padding(top = 18.dp),
////            horizontalArrangement = Arrangement.Center
////        ) {
////            ImageSliderIndicator(if (isTrue.value) Color.Black else Color.Gray)
////            Spacer(modifier = Modifier.width(5.dp))
////            ImageSliderIndicator(if (isTrue.value) Color.Gray else Color.Black)
////        }
////    }
//}

//@Composable
//fun RecipeCategoryCard(imageRes: Int, categoryName: String) {
////    onNavigateToRecipeBrowser: (category: String) -> Unit
//    Box(
//        modifier = Modifier
//            .clip(RoundedCornerShape(10.dp))
//            .clickable { }) {
////        onNavigateToRecipeBrowser(categoryName)
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color.Gray.copy(.2f))
//                .padding(15.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(id = imageRes),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(110.dp)
//                    .weight(1f)
//                    .clip(RoundedCornerShape(10.dp)),
//                contentScale = ContentScale.Crop,
//            )
//            Box(
//                modifier = Modifier.weight(2f),
//                contentAlignment = Alignment.Center) {
//                Text(
//                    text = categoryName,
//                    fontSize = 22.sp,
//                    fontWeight = FontWeight(600)
//                )
//            }
//
//        }
//    }
//
//}
//
//@Composable
//fun RecipeCategoriesList() {
////    onNavigateToRecipeBrowser: (category: String) -> Unit
//    val recipeCategories = listOf(
//        Pair(R.drawable.category_beef, "Beef"),
//        Pair(R.drawable.category_chicken, "Chicken"),
//        Pair(R.drawable.category_pork, "Pork"),
//        Pair(R.drawable.category_seafood, "Seafood"),
//        Pair(R.drawable.category_vegetable, "Vegetable"),
//        Pair(R.drawable.category_dessert, "Dessert")
//    )
//
//    Column {
//        recipeCategories.forEach { pair ->
//            RecipeCategoryCard(
//                pair.first,
//                categoryName = pair.second
//            )
////            { onNavigateToRecipeBrowser(pair.second.lowercase()) }
//
//            Spacer(modifier = Modifier.height(20.dp))
//        }
//    }
//}

