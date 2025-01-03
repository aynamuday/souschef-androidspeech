package com.samsantech.souschef.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.data.User

class FirebaseRecipeManager(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    fun getOwnRecipes(recipes: (List<Recipe>) -> Unit) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            db.collection("recipes")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->
                    val recipesList = mutableListOf<Recipe>()

                    documents.forEach { document ->
                        val data = document.data

                        recipesList.add(Recipe(
                            id = document.id,
                            userId = data["userId"].toString(),
                            userName = data["userName"].toString(),
                            userPhotoUrl = data["userPhotoUrl"].toString(),
                            photosUrl = data["photosUrl"] as? HashMap<String, Uri> ?: hashMapOf(),
                            title = data["title"].toString(),
                            description = if (data["description"] != null) data["description"].toString() else "",
                            prepTimeHr = data["prepTimeHr"].toString(),
                            prepTimeMin = data["prepTimeMin"].toString(),
                            cookTimeHr = data["cookTimeHr"].toString(),
                            cookTimeMin = data["cookTimeMin"].toString(),
                            serving = data["serving"].toString(),
                            difficulty = data["difficulty"].toString(),
                            mealTypes = data["mealTypes"] as? List<String> ?: listOf(),
                            categories = data["categories"] as? List<String> ?: listOf(),
                            ingredients = data["ingredients"] as? List<String> ?: listOf(),
                            instructions = data["instructions"] as? List<String> ?: listOf(),
                            tags = data["tags"] as? List<String> ?: listOf(),
                        ))
                    }

                    recipes(recipesList)
                }
                .addOnFailureListener {
                    println(it)
                }
        }
    }

    fun addRecipe(
        recipe: Recipe,
        user: User,
        callback: (Boolean, String?) -> Unit,
        updatedRecipe: (Recipe) -> Unit
    ) {
        val data = hashMapOf(
            "userId" to user.uid,
            "userName" to user.username,
            "userPhotoUrl" to user.photoUrl,
            "title" to recipe.title,
            "serving" to recipe.serving,
            "difficulty" to recipe.difficulty,
            "mealTypes" to recipe.mealTypes,
            "categories" to recipe.categories,
            "ingredients" to recipe.ingredients,
            "instructions" to recipe.instructions,
        )

        if (recipe.description.isNotEmpty()) data["description"] = recipe.description
        if (recipe.prepTimeHr.isNotEmpty()) data["prepTimeHr"] = recipe.prepTimeHr
        if (recipe.prepTimeMin.isNotEmpty()) data["prepTimeMin"] = recipe.prepTimeMin
        if (recipe.cookTimeHr.isNotEmpty()) data["cookTimeHr"] = recipe.cookTimeHr
        if (recipe.cookTimeMin.isNotEmpty()) data["cookTimeMin"] = recipe.cookTimeMin
        if (recipe.tags.isNotEmpty()) data["tags"] = recipe.tags

        db.collection("recipes")
            .add(data)  // adds the recipe to db
            .addOnSuccessListener { recipeDocRef ->
                val recipeDocRefId = recipeDocRef.id

                // on success, upload the recipe photos
                if (recipe.photosUri.size > 0) {
                    uploadRecipePhotos(
                        recipeDocRefId,
                        recipe.photosUri,
                        recipe,
                        updatedRecipe = {
                            updatedRecipe(it)
                        },
                        callback = { isSuccess, err ->
                            callback(isSuccess, err)
                        }
                    )
                } else {
                    callback(true, null)
                }
            }
            .addOnFailureListener {
                callback(false, getErrorMessage(it))
            }
    }

    fun updateRecipe(
        document: String,
        data: HashMap<String, Any>,
        recipe: Recipe,
        updatedRecipe: (Recipe) -> Unit,
        callback: (Boolean, String?) -> Unit
    ) {
        if (recipe.photosUri.size > 0) {
            uploadRecipePhotos(
                document,
                recipe.photosUri,
                recipe,
                updatedRecipe = {
                    updatedRecipe(it)
                },
                callback = { isSuccess, err ->
                    if (data.isEmpty()) {
                        callback(isSuccess, err)
                    }
                }
            )
        }
        if (data.isNotEmpty()) {
            db.collection("recipes")
                .document(document)
                .update(
                    data
                )
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener {
                    callback(false, getErrorMessage(it))
                }
        }
    }

    fun deleteRecipe(document: String, photos: HashMap<String, Uri>, callback: (Boolean, String?) -> Unit) {
        db.collection("recipes")
            .document(document)
            .delete()
            .addOnSuccessListener {
                callback(true, null)

                val storageRef = storage.reference
                val recipesRef = storageRef.child("recipes/${document}")

                photos.forEach { photo ->
                    val photoRef = recipesRef.child("${photo.key}.jpg")

                    photoRef.delete()
                        .addOnFailureListener {
                            println(it)
                        }
                }
            }
            .addOnFailureListener {
                callback(false, getErrorMessage(it))
            }
    }

    private fun uploadRecipePhotos(
        document: String,
        photosUri: Map<String, Uri>,
        recipe: Recipe,
        updatedRecipe: (Recipe) -> Unit,
        callback: (Boolean, String?) -> Unit
    ) {
        val storageRef = storage.reference
        val recipesRef = storageRef.child("recipes/${document}")

        photosUri.forEach { photo ->
            val uploadRef = recipesRef.child("${photo.key}.jpg")
            val uploadTask = uploadRef.putFile(photo.value)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    println(task.exception)
                }

                uploadRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val url = Uri.parse("$downloadUri")

                    // update the recipe in db - adds the url of photos
                    val recipeRef = db.collection("recipes").document(document)
                    recipeRef.update(
                        mapOf(
                            "photosUrl.${photo.key}" to url
                        )
                    )
                        .addOnSuccessListener {
                            recipe.photosUrl[photo.key] = url
                            recipe.photosUri.remove(photo.key)
                            updatedRecipe(recipe)
                        }
                        .addOnFailureListener {
                            println(it)
                        }
                        .addOnCompleteListener {
                            callback(true, null)
                        }
                } else {
                    callback(true, null)
                }
            }
        }
    }
}