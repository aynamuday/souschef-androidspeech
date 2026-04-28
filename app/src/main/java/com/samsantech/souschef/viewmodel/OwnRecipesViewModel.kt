package com.samsantech.souschef.viewmodel

import android.net.Uri
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.firebase.FirebaseRecipeManager
import com.samsantech.souschef.utils.OwnRecipeAction
import kotlinx.coroutines.flow.MutableStateFlow

class OwnRecipesViewModel(
    private val userViewModel: UserViewModel,
    private val firebaseRecipeManager: FirebaseRecipeManager,
    private val recipesViewModel: RecipesViewModel
) {
    val actionRecipe = MutableStateFlow(Recipe())
    val recipes = MutableStateFlow<List<Recipe>>(listOf())
    val action = MutableStateFlow(OwnRecipeAction.ADD)
    val originalData = MutableStateFlow(Recipe())
    var deletePhotoKey: String? = null

    init {
        getOwnRecipes()
    }

    fun getOwnRecipes() {
        firebaseRecipeManager.getOwnRecipes {
            recipes.value = it
        }
    }

    fun uploadRecipe(callback: (Boolean, String?) -> Unit) {
        val user = userViewModel.user.value

        if (user != null) {
            firebaseRecipeManager.addRecipe(
                recipe = actionRecipe.value,
                user = user,
                callback = { isSuccess, error ->
                    callback(isSuccess, error)
                    if (isSuccess) {
                        resetRecipe()
                    }
                },
                updatedRecipe = { updatedRecipe ->
                    updateRecipes(updatedRecipe)
                }
            )
        }
    }

    fun updateRecipe(data: HashMap<String, Any>, callback: (Boolean, String?) -> Unit) {
        val recipe = actionRecipe.value

        firebaseRecipeManager.updateRecipe(
            data,
            recipe,
            updatedRecipe = { updatedRecipe ->
                updateRecipes(updatedRecipe)
            },
            callback = { isSuccess, error ->
                callback(isSuccess, error)
                if (isSuccess) {
                    updateRecipes(recipe)
                    if (recipesViewModel.displayRecipe.value.id == recipe.id) {
                        recipesViewModel.displayRecipe.value = recipe
                    }
                    if (data["audience"] == "Only me" && recipe.id != null) {
                        recipesViewModel.favoriteRecipes.value = recipesViewModel.favoriteRecipes.value.filter {
                            it != recipe.id
                        }.toSet()
                    }
                    resetRecipe()
                }
            },
            deletePhotoKey = deletePhotoKey
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
                recipesViewModel.favoriteRecipes.value -= recipeId
            }

            callback(isSuccess, error)
        }
    }

    fun resetRecipe() {
        actionRecipe.value = Recipe()
        originalData.value = Recipe()
        deletePhotoKey = null
    }

    private fun updateRecipes(recipe: Recipe) {
        val updatedRecipes = recipes.value.toMutableList()
        val recipeIndexToUpdate = updatedRecipes.indexOfFirst {
            it.id == recipe.id
        }

        if (recipeIndexToUpdate != -1) {
            updatedRecipes[recipeIndexToUpdate] = recipe
        } else {
            updatedRecipes.add(0, recipe)
        }

        recipes.value = updatedRecipes
    }

    fun addPhoto(key: String, value: Uri) {
        val photos = HashMap<String, Uri>(actionRecipe.value.photosUri.toMap())
        photos[key] = value
        actionRecipe.value = actionRecipe.value.copy(
            photosUri = photos
        )
    }

    fun removePhoto(key: String) {
        val photos = HashMap<String, Uri>(actionRecipe.value.photosUri.toMap())
        photos.remove(key)
        actionRecipe.value = actionRecipe.value.copy(
            photosUri = photos
        )
    }

    fun setTitle(title: String) {
        actionRecipe.value = actionRecipe.value.copy(
            title = title
        )
    }

    fun setDescription(description: String) {
        actionRecipe.value = actionRecipe.value.copy(
            description = description
        )
    }

    fun setPrepTimeHr(prepTimeHr: String) {
        actionRecipe.value = actionRecipe.value.copy(
            prepTimeHr = prepTimeHr
        )
    }

    fun setPrepTimeMin(prepTimeMin: String) {
        actionRecipe.value = actionRecipe.value.copy(
            prepTimeMin = prepTimeMin
        )
    }

    fun setCookTimeHr(cookTimeHr: String) {
        actionRecipe.value = actionRecipe.value.copy(
            cookTimeHr = cookTimeHr
        )
    }

    fun setCookTimeMin(cookTimeMin: String) {
        actionRecipe.value = actionRecipe.value.copy(
            cookTimeMin = cookTimeMin
        )
    }

    fun setServing(serving: String) {
        actionRecipe.value = actionRecipe.value.copy(
            serving = serving
        )
    }

    fun setDifficulty(difficulty: String) {
        actionRecipe.value = actionRecipe.value.copy(
            difficulty = difficulty
        )
    }

//    fun addMealType(mealType: String) {
//        actionRecipe.value = actionRecipe.value.copy(
//            mealTypes = actionRecipe.value.mealTypes.plus(mealType)
//        )
//    }

//    fun removeMealType(mealType: String) {
//        actionRecipe.value = actionRecipe.value.copy(
//            mealTypes = actionRecipe.value.mealTypes.minus(mealType)
//        )
//    }

    fun addCategory(category: String) {
        actionRecipe.value = actionRecipe.value.copy(
            categories = actionRecipe.value.categories.plus(category)
        )
    }

    fun removeCategory(category: String) {
        actionRecipe.value = actionRecipe.value.copy(
            categories = actionRecipe.value.categories.minus(category)
        )
    }

    fun addIngredient() {
        val newIngredients = actionRecipe.value.ingredients.toMutableList()
        newIngredients.add("")

        actionRecipe.value = actionRecipe.value.copy(
            ingredients = newIngredients
        )
    }

    fun updateIngredients(ingredientIndex: Int, value: String) {
        val newIngredients = actionRecipe.value.ingredients.mapIndexed { index, ingredient ->
            if(index == ingredientIndex) value else ingredient
        }

        actionRecipe.value = actionRecipe.value.copy(
            ingredients = newIngredients
        )
    }

    fun removeIngredient(ingredientIndex: Int) {
        val newIngredients = actionRecipe.value.ingredients.toMutableList()
        newIngredients.removeAt(ingredientIndex)

        actionRecipe.value = actionRecipe.value.copy(
            ingredients = newIngredients
        )
    }

    fun setIngredients(ingredients: List<String>) {
        actionRecipe.value = actionRecipe.value.copy(
            ingredients = ingredients
        )
    }

    fun addInstruction() {
        val newInstructions = actionRecipe.value.instructions.toMutableList()
        newInstructions.add("")

        actionRecipe.value = actionRecipe.value.copy(
            instructions = newInstructions
        )
    }

    fun updateInstructions(instructionIndex: Int, value: String) {
        val newInstructions = actionRecipe.value.instructions.mapIndexed { index, instruction ->
            if(index == instructionIndex) value else instruction
        }

        actionRecipe.value = actionRecipe.value.copy(
            instructions = newInstructions
        )
    }

    fun removeInstruction(instructionIndex: Int) {
        val newInstructions = actionRecipe.value.instructions.toMutableList()
        newInstructions.removeAt(instructionIndex)

        actionRecipe.value = actionRecipe.value.copy(
            instructions = newInstructions
        )
    }

    fun setInstructions(instructions: List<String>) {
        actionRecipe.value = actionRecipe.value.copy(
            instructions = instructions
        )
    }

    fun addTag(tag: String) {
        actionRecipe.value = actionRecipe.value.copy(
            tags = actionRecipe.value.tags.plus(tag)
        )
    }

    fun removeTag(tag: String) {
        actionRecipe.value = actionRecipe.value.copy(
            tags = actionRecipe.value.tags.minus(tag)
        )
    }

    fun toggleAudience(audience: String) {
        actionRecipe.value = actionRecipe.value.copy(
            audience = audience
        )
    }
    fun updateRecipesUserPhotoUrl(photoUrl: String) {
        val updatedRecipes: MutableList<Recipe> = mutableListOf()

        recipes.value.forEach {
            val recipe = it.copy(userPhotoUrl = photoUrl)
            updatedRecipes.add(0, recipe)
        }
        updatedRecipes.reverse()

        recipes.value = updatedRecipes
    }

    fun updateRecipesUserName(userName: String) {
        val updatedRecipes: MutableList<Recipe> = mutableListOf()

        recipes.value.forEach {
            val recipe = it.copy(userName = userName)
            updatedRecipes.add(0, recipe)
        }
        updatedRecipes.reverse()

        recipes.value = updatedRecipes
    }

    fun getUpdatedRecipeDifference(): HashMap<String, Any> {
        val recipeOne = actionRecipe.value
        val recipeTwo = originalData.value
        val data = hashMapOf<String, Any>()

        if (recipeOne.title != recipeTwo.title) {
            data["title"] = recipeTwo.title
        }
        if (recipeOne.description != recipeTwo.description) {
            data["description"] = recipeTwo.description
        }
        if (recipeOne.prepTimeHr != recipeTwo.prepTimeHr) {
            data["prepTimeHr"] = recipeTwo.prepTimeHr
        }
        if (recipeOne.prepTimeMin != recipeTwo.prepTimeMin) {
            data["prepTimeMin"] = recipeTwo.prepTimeMin
        }
        if (recipeOne.cookTimeHr != recipeTwo.cookTimeHr) {
            data["cookTimeHr"] = recipeTwo.cookTimeHr
        }
        if (recipeOne.cookTimeMin != recipeTwo.cookTimeMin) {
            data["cookTimeMin"] = recipeTwo.cookTimeMin
        }
        if (recipeOne.serving != recipeTwo.serving) {
            data["serving"] = recipeTwo.serving
        }
        if (recipeOne.difficulty != recipeTwo.difficulty) {
            data["difficulty"] = recipeTwo.difficulty
        }
//    if (recipeOne.mealTypes != recipeTwo.mealTypes) {
//        data["mealTypes"] = recipeTwo.mealTypes
//    }
        if (recipeOne.categories != recipeTwo.categories) {
            data["categories"] = recipeTwo.categories
        }
        if (recipeOne.ingredients != recipeTwo.ingredients) {
            data["ingredients"] = recipeTwo.ingredients
        }
        if (recipeOne.instructions != recipeTwo.instructions) {
            data["instructions"] = recipeTwo.instructions
        }
        if (recipeOne.tags != recipeTwo.tags) {
            data["tags"] = recipeTwo.tags
        }
        if (recipeOne.audience != recipeTwo.audience) {
            data["audience"] = recipeTwo.audience
        }

        return data
    }
}