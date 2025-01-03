package com.samsantech.souschef.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel

@Composable
fun ContentBottomNavigationWrapper(
    systemNavigationBarHeight: Dp,
    name: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCreateRecipe: () -> Unit,
    onNavigateToSearch: () -> Unit,
    //onNavigateToTiktokVideos: () -> Unit,
    onNavigateToProfile: () -> Unit,
    ownRecipesViewModel: OwnRecipesViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold (
        bottomBar = {
            BottomNavigationBar(
                systemNavigationBarHeight,
                name,
                onNavigateToHome,
                onNavigateToCreateRecipe,
                onNavigateToSearch,
                //onNavigateToTiktokVideos,
                onNavigateToProfile,
                ownRecipesViewModel
            )
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}