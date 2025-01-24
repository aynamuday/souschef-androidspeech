package com.samsantech.souschef.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel

@Composable
fun ContentBottomNavigationWrapper(
    name: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCreateRecipe: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    ownRecipesViewModel: OwnRecipesViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold (
        bottomBar = {
            BottomNavigationBar(
                name,
                onNavigateToHome,
                onNavigateToCreateRecipe,
                onNavigateToSearch,
                onNavigateToProfile,
                ownRecipesViewModel
            )
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}