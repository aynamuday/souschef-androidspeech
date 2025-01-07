package com.samsantech.souschef.viewmodel

import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.firebase.FirebaseRecipeManager
import kotlinx.coroutines.flow.MutableStateFlow

class RecipesViewModel(
    private val firebaseRecipeManager: FirebaseRecipeManager
) {
    val displayRecipe = MutableStateFlow<List<Recipe>>(emptyList())

    init {
        refreshRecipes()
    }

    fun refreshRecipes() {
        firebaseRecipeManager.getAllRecipes { recipes ->
            displayRecipe.value = recipes
        }
    }

}
