package com.samsantech.souschef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Yellow
import com.samsantech.souschef.utils.OwnRecipeAction
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel


@Composable
fun BottomNavigationBar(
    systemNavigationBarHeight: Dp,
    name: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCreateRecipe: () -> Unit,
    onNavigateToSearch: () -> Unit,
    //onNavigateToTiktokVideos: () -> Unit,
    onNavigateToProfile: () -> Unit,
    ownRecipesViewModel: OwnRecipesViewModel,
    modifier: Modifier = Modifier
) {
    val bottomNavigationItems = arrayOf(
        hashMapOf("name" to "Home", "imageVector" to Icons.Filled.Home),
        hashMapOf("name" to "Search", "imageVector" to Icons.Filled.Search),
        hashMapOf("name" to "Create recipe", "imageVector" to Icons.Filled.AddCircle),
        //hashMapOf("name" to "Tiktok videos", "imageVector" to Icons.Filled.PlayArrow),
        hashMapOf("name" to "Profile", "imageVector" to Icons.Filled.Person),
    )

    Column(modifier = Modifier
        .padding(bottom = 0.dp)
        .background(Green)) {
        BottomNavigation(
            backgroundColor = Green,
            modifier = modifier
                .padding(bottom = systemNavigationBarHeight)
                .height(60.dp)
                .fillMaxWidth()
        ) {
            bottomNavigationItems.forEach { item ->
                BottomNavigationItem(
                    selected = item["name"] == name,
                    onClick = {
                        when (item["name"]) {
                            "Home" -> onNavigateToHome()
                            "Create recipe" -> {
                                onNavigateToCreateRecipe()
                                ownRecipesViewModel.resetRecipe()
                                ownRecipesViewModel.action.value = OwnRecipeAction.ADD
                            }
                            "Search" -> onNavigateToSearch()
                            //"Tiktok videos" -> onNavigateToTiktokVideos()
                            "Profile" -> onNavigateToProfile()
                        }
                    },
                    selectedContentColor = Yellow,
                    unselectedContentColor = Color.White,
                    modifier = Modifier.fillMaxSize(),
                    icon = {
                        val imageVector = item["imageVector"] as? ImageVector
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            imageVector?.let { Icon(imageVector = imageVector, contentDescription = null, modifier = Modifier.size(30.dp)) }
                        }
                    },
                )
            }
        }
    }
}
