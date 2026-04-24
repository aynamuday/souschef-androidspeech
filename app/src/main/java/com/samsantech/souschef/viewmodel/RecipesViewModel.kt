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
        refreshFavoriteRecipes()
    }

    private fun refreshRecipes() {
        firebaseRecipeManager.getAllRecipes { recipes ->
            allRecipes.value = recipes
        }
    }

    private fun refreshFavoriteRecipes() {
        firebaseRecipeManager.getUserFavoriteRecipes { favorites ->
            favoriteRecipes.value = favorites.toSet()
        }
    }

    fun getRecipe(id: String, callback: (Boolean, String?, Recipe?) -> Unit) {
        firebaseRecipeManager.getRecipe(id) { isSuccess, error, recipe ->
            callback(isSuccess, error, recipe)
        }
    }

    fun toggleFavorite(recipeId: String, isAdd: Boolean, callback: (Boolean) -> Unit) {
        firebaseRecipeManager.addFavoriteRecipe(recipeId, isAdd) { isSuccess ->
            if (isSuccess) {
                if (isAdd) {
                    val updatedFavorites = favoriteRecipes.value.toMutableList()
                    updatedFavorites.add(0, recipeId)
                    favoriteRecipes.value = updatedFavorites.toSet()
                } else {
                    favoriteRecipes.value -= recipeId
                }
            }
            callback(isSuccess)
        }
    }

    fun removeFromFavorites(recipeId: String, callback: (Boolean) -> Unit) {
        firebaseRecipeManager.removeCurrentUserFavorite(recipeId) { isSuccess ->
            if (isSuccess) {
                favoriteRecipes.value -= recipeId
            }
            callback(isSuccess)
        }
    }

    fun rateRecipe(recipeId: String, rating: Float, callback: (Boolean, Float?) -> Unit) {
        firebaseRecipeManager.rateRecipe(recipeId, rating) { isSuccess, updatedAverageRating ->
            if (isSuccess) {
                // Update the local recipe list with the new rating
                val updatedRecipes = allRecipes.value.map { recipe ->
                    if (recipe.id == recipeId) {
                        recipe.copy(userRating = rating, averageRating = updatedAverageRating)
                    } else recipe
                }
                allRecipes.value = updatedRecipes
            }
            callback(isSuccess, updatedAverageRating)
        }
    }

    fun getUserRatingForRecipe(recipeId: String, callback: (Float?) -> Unit) {
        firebaseRecipeManager.getUserRating(recipeId) { rating ->
            callback(rating)
        }
    }

    fun removeRecipe(recipeId: String, callback: (Boolean) -> Unit) {
        firebaseRecipeManager.removeRecipe(recipeId) { isSuccess ->
            if (isSuccess) {
                // Remove the recipe from the local list
                allRecipes.value = allRecipes.value.filter { it.id != recipeId }
            }
            callback(isSuccess)
        }
    }

    fun setSeenPost(recipeId: String) {
        firebaseRecipeManager.setSeenPost(recipeId)
    }
}