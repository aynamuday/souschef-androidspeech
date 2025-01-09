package com.samsantech.souschef.viewmodel

import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.firebase.FirebaseRecipeManager
import kotlinx.coroutines.flow.MutableStateFlow

class RecipesViewModel (
    private val firebaseRecipeManager: FirebaseRecipeManager
) {
    val favoriteRecipes: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
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

    fun getRecipe(id: String, callback: (Boolean, String?, Recipe?) -> Unit) {
        firebaseRecipeManager.getRecipe(id) { isSuccess, error, recipe ->
            callback(isSuccess, error, recipe)
        }
    }

    fun toggleFavoriteRecipe(recipeId: String, isAdd: Boolean, callback: (Boolean) -> Unit) {
        val updatedFavorites = if (isAdd) {
            favoriteRecipes.value + recipeId
        } else {
            favoriteRecipes.value - recipeId
        }

        favoriteRecipes.value = updatedFavorites

        firebaseRecipeManager.addFavoriteRecipe(recipeId.toString(), isAdd) { isSuccess ->
            callback(isSuccess)
        }
    }
//    fun loadFavoriteRecipes() {
//        firebaseUserManager.getFavoriteRecipes { recipes ->
//            favoriteRecipes.value = recipes.toSet()  // Convert to Set
//
//        }
//    }
}