package com.samsantech.souschef.viewmodel

import android.net.Uri
import androidx.compose.runtime.MutableState
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.firebase.FirebaseRecipeManager
import com.samsantech.souschef.utils.OwnRecipeAction
import com.samsantech.souschef.utils.RecipeScreenNumber
import kotlinx.coroutines.flow.MutableStateFlow

class OwnRecipesViewModel(
    private val userViewModel: UserViewModel,
    private val firebaseRecipeManager: FirebaseRecipeManager,
    private val recipesViewModel: RecipesViewModel
) {
    val recipes = MutableStateFlow<List<Recipe>>(listOf())
    val originalData = MutableStateFlow(Recipe())   // the original recipe value before edit action
    val actionRecipe = MutableStateFlow(Recipe())   // the recipe value to perform action with (add, edit)
    val action = MutableStateFlow(OwnRecipeAction.ADD)

    init {
        getOwnRecipes()
    }

    fun resetRecipe() {
        actionRecipe.value = Recipe()
        originalData.value = Recipe()
    }

    fun getOwnRecipes() {
        firebaseRecipeManager.getOwnRecipes { recipes.value = it }
    }

    fun uploadRecipe(callback: (Boolean, String?) -> Unit) {
        val user = userViewModel.user.value

        if (user != null) {
            firebaseRecipeManager.addRecipe(recipe = actionRecipe.value, user = user,
                callback = { isSuccess, error, createdRecipe ->
                    if (isSuccess) {
                        resetRecipe()
                        createdRecipe?.let { updateRecipes(it, true) }
                    }
                    callback(isSuccess, error)
                }
            )
        }
    }

    fun updateRecipe(updates: HashMap<String, Any>, callback: (Boolean, String?) -> Unit) {
        val recipe = actionRecipe.value

        firebaseRecipeManager.updateRecipe(
            updates = updates,
            recipe = recipe,
            deletePhotoKey = if (originalData.value.photosUrl.containsKey("portrait") && !actionRecipe.value.photosUrl.containsKey("portrait")) "portrait"
            else if (originalData.value.photosUrl.containsKey("square") && !actionRecipe.value.photosUrl.containsKey("square")) "square" else null,
                    // if there was portrait photo in original data but not in action recipe, portrait must be deleted in the database, so deletePhotoKey is set
                    // if there is portrait photo in action recipe but is an updated one, the getUpdatedRecipeDifference will determine it,
                    // and will be uploaded to the database to overwrite the former photo
                    // there should be at least one photo of the recipe
            callback = { isSuccess, error, updatedRecipe ->
                if (isSuccess) {
                    updatedRecipe?.let {updateRecipes(it, false) }
                    if (recipesViewModel.displayRecipe.value.id == recipe.id) {
                        recipesViewModel.displayRecipe.value = recipe
                    }
                    resetRecipe()
                }
                callback(isSuccess, error)
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

    fun getErrors(recipeScreen: RecipeScreenNumber): HashMap<String, String> {
        val recipe = actionRecipe.value
        val errors = hashMapOf<String, String>()

        if (recipeScreen == RecipeScreenNumber.One) {
            if (recipe.photosUrl["square"] == null && recipe.photosUrl["portrait"] == null) {
                errors["photos"] = "At least one photo is required."
            }
            if (recipe.title.isEmpty()) {
                errors["title"] = "Title is required."
            }
            if (recipe.prepTimeHr == "0" && recipe.prepTimeMin == "0"
                && recipe.cookTimeHr == "0" && recipe.cookTimeMin == "0") {
                errors["prepTime"] = "Provide either preparation time or cook time."
                errors["cookTime"] = "Provide either preparation time or cook time."
            }
            if (recipe.difficulty.isEmpty()) {
                errors["difficulty"] = "Difficulty is required."
            }
        }

        return errors
    }

    fun handleErrors(recipeScreenNumber: RecipeScreenNumber,
                     errors: MutableState<HashMap<String, String>>,
                     onNoErrorsAction: () -> Unit) {
        val newErrors = getErrors(recipeScreenNumber)

        if (newErrors.isNotEmpty()) {
            newErrors["general"] = "Check your inputs for errors."
            errors.value = newErrors
        } else {
            onNoErrorsAction()
        }
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
        if (recipeOne.photosUrl != recipeTwo.photosUrl) {
            data["photosUrl"] = recipeTwo.photosUrl
        }
        return data
    }

    private fun updateRecipes(recipe: Recipe, isNew: Boolean) {
        val updatedRecipes = recipes.value.toMutableList()

        if (isNew) {
            updatedRecipes.add(0, recipe)
        } else {
            val recipeIndexToUpdate = updatedRecipes.indexOfFirst { it.id == recipe.id }
            if (recipeIndexToUpdate != -1) updatedRecipes[recipeIndexToUpdate] = recipe
        }

        recipes.value = updatedRecipes
    }

    fun addPhoto(key: String, value: Uri) {
        val photos = HashMap<String, Uri>(actionRecipe.value.photosUrl.toMap())
        photos[key] = value
        actionRecipe.value = actionRecipe.value.copy(
            photosUrl = photos
        )
    }

    fun removePhoto(key: String) {
        val photos = HashMap<String, Uri>(actionRecipe.value.photosUrl.toMap())
        photos.remove(key)
        actionRecipe.value = actionRecipe.value.copy(
            photosUrl = photos
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

    // for adding empty ingredients text field in UI
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
        if (ingredientIndex in newIngredients.indices) {
            newIngredients.removeAt(ingredientIndex)
        }

        actionRecipe.value = actionRecipe.value.copy(
            ingredients = newIngredients
        )
    }

    fun cleanIngredients() {
        val newIngredients = actionRecipe.value.ingredients.toMutableList()
        newIngredients.removeAll { it.trim().isBlank() }

        actionRecipe.value = actionRecipe.value.copy(
            ingredients = newIngredients
        )
    }

    // for adding empty instructions text field in UI
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

    fun cleanInstructions() {
        val newInstructions = actionRecipe.value.instructions.toMutableList()
        newInstructions.removeAll { it.trim().isBlank() }

        actionRecipe.value = actionRecipe.value.copy(
            instructions = newInstructions
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
}