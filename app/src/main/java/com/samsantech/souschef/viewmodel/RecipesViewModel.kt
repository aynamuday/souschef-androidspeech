package com.samsantech.souschef.viewmodel

import android.net.Uri
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.data.RecipePhotos
import com.samsantech.souschef.firebase.FirebaseRecipeManager
import kotlinx.coroutines.flow.MutableStateFlow

class RecipesViewModel (
    private val userViewModel: UserViewModel,
    private val firebaseRecipeManager: FirebaseRecipeManager
) {

    val favoriteRecipes: MutableStateFlow<List<RecipePhotos>> = MutableStateFlow(listOf())
    val displayRecipe = MutableStateFlow(Recipe())

    fun refreshFavoriteRecipes(bFavoriteRecipes: List<String>) {
        firebaseRecipeManager.getRecipesPhotos(bFavoriteRecipes) {
            favoriteRecipes.value = it
        }
    }

    fun getRecipe(id: String, callback: (Boolean, String?, Recipe?) -> Unit) {
        firebaseRecipeManager.getRecipe(id) { isSuccess, error, recipe ->
            callback(isSuccess, error, recipe)
        }
    }

    fun toggleFavorite(recipeId: String, photosUrl: HashMap<String, Uri>, isAdd: Boolean, callback: (Boolean) -> Unit) {
        firebaseRecipeManager.toggleFavoriteRecipe(recipeId, isAdd) { isSuccess ->
            if (isSuccess) {
                if (isAdd) {
                    val updatedFavorites = favoriteRecipes.value.toMutableList()
                    updatedFavorites.add(0, RecipePhotos(recipeId, photosUrl))
                    favoriteRecipes.value = updatedFavorites.toMutableList()
                } else {
                    val updatedFavorites = favoriteRecipes.value.filter { it.id != recipeId }
                    favoriteRecipes.value = updatedFavorites.toMutableList()
                }
            }
            callback(isSuccess)
        }
    }

    fun rateRecipe(recipeId: String, rating: Float, callback: (Boolean, Float?) -> Unit) {
        firebaseRecipeManager.rateRecipe(recipeId, rating) { isSuccess, updatedAverageRating ->
            callback(isSuccess, updatedAverageRating)
        }
    }

    fun removeRecipeRating(recipeId: String, callback: (Boolean, Float?) -> Unit) {
        firebaseRecipeManager.removeRecipeRating(recipeId) { isSuccess, updatedAverageRating ->
            callback(isSuccess, updatedAverageRating)
        }
    }

    fun setSeenPost(recipeId: String) {
        firebaseRecipeManager.setSeenPost(recipeId)
    }
}