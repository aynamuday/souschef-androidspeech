package com.samsantech.souschef.viewmodel

import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.firebase.FirebaseRecipeManager
import kotlinx.coroutines.flow.MutableStateFlow

class RecipesViewModel (
    private val firebaseRecipeManager: FirebaseRecipeManager
) {
    val allRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val displayRecipe = MutableStateFlow(Recipe())

    init {
        refreshRecipes()
    }

    private fun refreshRecipes() {
        firebaseRecipeManager.getAllRecipes { recipes ->
            allRecipes.value = recipes
        }
    }
}