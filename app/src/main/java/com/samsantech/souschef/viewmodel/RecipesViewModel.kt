package com.samsantech.souschef.viewmodel

import com.samsantech.souschef.data.SearchRecipe
import com.samsantech.souschef.firebase.FirebaseRecipeManager
import kotlinx.coroutines.flow.MutableStateFlow

class RecipesViewModel(
    private val firebaseRecipeManager: FirebaseRecipeManager
) : ViewModel() {
    val favoriteRecipes: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val displayAllRecipe = MutableStateFlow<List<Recipe>>(emptyList())
    val allRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val displayRecipe = MutableStateFlow(Recipe())

    init {
        connections += searcher.connectFilterState(filterState)
        connections += searchBoxConnector.connectView(searchBoxState)
        connections += searchBoxConnector.connectPaginator(hitsPaginator)
        connections += loadingConnector.connectView(loadingState)

        val audienceId = FilterGroupID("audienceId", FilterOperator.Or)
        filterState.notify {
            add(
                audienceId,
                setOf(
                    Filter.Facet(Attribute("audience"), "Public")
                )
            )
        }

        refreshRecipes()
    }

    private fun refreshRecipes() {
        firebaseRecipeManager.getAllRecipes { recipes ->
            allRecipes.value = recipes
        }
    }

    fun refreshRecipes() {
        firebaseRecipeManager.getAllRecipes { recipes ->
            displayAllRecipe.value = recipes
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
