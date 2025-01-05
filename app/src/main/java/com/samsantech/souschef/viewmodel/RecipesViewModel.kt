package com.samsantech.souschef.viewmodel

import com.samsantech.souschef.data.Recipe
import kotlinx.coroutines.flow.MutableStateFlow

class RecipesViewModel {
    val displayRecipe = MutableStateFlow<Recipe>(Recipe())
}