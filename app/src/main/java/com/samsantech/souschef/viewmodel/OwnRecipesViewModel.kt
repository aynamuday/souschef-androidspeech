package com.samsantech.souschef.viewmodel

import android.net.Uri
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.firebase.FirebaseRecipeManager
import com.samsantech.souschef.utils.OwnRecipeAction
import kotlinx.coroutines.flow.MutableStateFlow

class OwnRecipesViewModel(
    private val userViewModel: UserViewModel,
    private val firebaseRecipeManager: FirebaseRecipeManager
) {
    val recipe = MutableStateFlow<Recipe>(Recipe())
    val recipes = MutableStateFlow<List<Recipe>>(listOf())
    val action = MutableStateFlow(OwnRecipeAction.ADD)
    val originalData = MutableStateFlow<Recipe>(Recipe())

    init {
        firebaseRecipeManager.getOwnRecipes {
            recipes.value = it
        }
    }

    fun uploadRecipe(callback: (Boolean, String?) -> Unit) {
        val user = userViewModel.user.value

        if (user != null) {
            firebaseRecipeManager.addRecipe(
                recipe = recipe.value,
                user = user,
                callback = { isSuccess, error ->
                    callback(isSuccess, error)
                    resetRecipe()
                },
                updatedRecipe = { updatedRecipe ->
                    updateRecipes(updatedRecipe)
                }
            )
        }
    }

    fun updateRecipe(data: HashMap<String, Any>, callback: (Boolean, String?) -> Unit) {
        firebaseRecipeManager.updateRecipe(
            recipe.value.id!!,
            data, recipe.value,
            updatedRecipe = {updatedRecipe ->
                updateRecipes(updatedRecipe)
            },
            callback = { isSuccess, error ->
                callback(isSuccess, error)
                if (isSuccess) {
                    updateRecipes(recipe.value)
                    resetRecipe()
                    action.value = OwnRecipeAction.ADD
                }
            }
        )
    }

    fun deleteRecipe(recipeId: String, photos: HashMap<String, Uri>, callback: (Boolean, String?) -> Unit) {
        firebaseRecipeManager.deleteRecipe(recipeId, photos) { isSuccess, error ->
            if (isSuccess) {
                val updatedRecipes = recipes.value.toMutableList()
                val recipeIndexToRemove = updatedRecipes.indexOfFirst { it.id == recipeId }

                if (recipeIndexToRemove != -1) {
                    updatedRecipes.removeAt(recipeIndexToRemove)
                }

                recipes.value = updatedRecipes
            }

            callback(isSuccess, error)
        }
    }

    fun resetRecipe() {
        recipe.value = Recipe()
    }

    private fun updateRecipes(recipe: Recipe) {
        val updatedRecipes = recipes.value.toMutableList()
        val recipeIndexToUpdate = updatedRecipes.indexOfFirst { it.id == recipe.id }

        if (recipeIndexToUpdate != -1) {
            updatedRecipes[recipeIndexToUpdate] = recipe
        } else {
            updatedRecipes.add(0, recipe)
        }

        recipes.value = updatedRecipes
    }

    fun addPhoto(key: String, value: Uri) {
        val photos = HashMap<String, Uri>(recipe.value.photosUri.toMap())
        photos[key] = value
        recipe.value = recipe.value.copy(
            photosUri = photos
        )
    }

    fun removePhoto(key: String) {
        val photos = HashMap<String, Uri>(recipe.value.photosUri.toMap())
        photos.remove(key)
        recipe.value = recipe.value.copy(
            photosUri = photos
        )
    }

    fun setTitle(title: String) {
        recipe.value = recipe.value.copy(
            title = title
        )
    }

    fun setDescription(description: String) {
        recipe.value = recipe.value.copy(
            description = description
        )
    }

    fun setPrepTimeHr(prepTimeHr: String) {
        recipe.value = recipe.value.copy(
            prepTimeHr = prepTimeHr
        )
    }

    fun setPrepTimeMin(prepTimeMin: String) {
        recipe.value = recipe.value.copy(
            prepTimeMin = prepTimeMin
        )
    }

    fun setCookTimeHr(cookTimeHr: String) {
        recipe.value = recipe.value.copy(
            cookTimeHr = cookTimeHr
        )
    }

    fun setCookTimeMin(cookTimeMin: String) {
        recipe.value = recipe.value.copy(
            cookTimeMin = cookTimeMin
        )
    }

    fun setServing(serving: String) {
        recipe.value = recipe.value.copy(
            serving = serving
        )
    }

    fun setDifficulty(difficulty: String) {
        recipe.value = recipe.value.copy(
            difficulty = difficulty
        )
    }

    fun addMealType(mealType: String) {
        recipe.value = recipe.value.copy(
            mealTypes = recipe.value.mealTypes.plus(mealType)
        )
    }

    fun removeMealType(mealType: String) {
        recipe.value = recipe.value.copy(
            mealTypes = recipe.value.mealTypes.minus(mealType)
        )
    }

    fun addCategory(category: String) {
        recipe.value = recipe.value.copy(
            categories = recipe.value.categories.plus(category)
        )
    }

    fun removeCategory(category: String) {
        recipe.value = recipe.value.copy(
            categories = recipe.value.categories.minus(category)
        )
    }

    fun addIngredient() {
        val newIngredients = recipe.value.ingredients.toMutableList()
        newIngredients.add("")

        recipe.value = recipe.value.copy(
            ingredients = newIngredients
        )
    }

    fun updateIngredients(ingredientIndex: Int, value: String) {
        val newIngredients = recipe.value.ingredients.mapIndexed { index, ingredient ->
            if(index == ingredientIndex) value else ingredient
        }

        recipe.value = recipe.value.copy(
            ingredients = newIngredients
        )
    }

    fun removeIngredient(ingredientIndex: Int) {
        val newIngredients = recipe.value.ingredients.toMutableList()
        newIngredients.removeAt(ingredientIndex)

        recipe.value = recipe.value.copy(
            ingredients = newIngredients
        )
    }

    fun setIngredients(ingredients: List<String>) {
        recipe.value = recipe.value.copy(
            ingredients = ingredients
        )
    }

    fun addInstruction() {
        val newInstructions = recipe.value.instructions.toMutableList()
        newInstructions.add("")

        recipe.value = recipe.value.copy(
            instructions = newInstructions
        )
    }

    fun updateInstructions(instructionIndex: Int, value: String) {
        val newInstructions = recipe.value.instructions.mapIndexed { index, instruction ->
            if(index == instructionIndex) value else instruction
        }

        recipe.value = recipe.value.copy(
            instructions = newInstructions
        )
    }

    fun removeInstruction(instructionIndex: Int) {
        val newInstructions = recipe.value.instructions.toMutableList()
        newInstructions.removeAt(instructionIndex)

        recipe.value = recipe.value.copy(
            instructions = newInstructions
        )
    }

    fun setInstructions(instructions: List<String>) {
        recipe.value = recipe.value.copy(
            instructions = instructions
        )
    }

    fun addTag(tag: String) {
        recipe.value = recipe.value.copy(
            tags = recipe.value.tags.plus(tag)
        )
    }

    fun removeTag(tag: String) {
        recipe.value = recipe.value.copy(
            tags = recipe.value.tags.minus(tag)
        )
    }
}