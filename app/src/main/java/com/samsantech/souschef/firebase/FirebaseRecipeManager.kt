package com.samsantech.souschef.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.data.User

class FirebaseRecipeManager(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    fun getAllRecipes(recipes: (List<Recipe>) -> Unit) {
        db.collection("recipes")
            .get()
            .addOnSuccessListener { documents ->
                val recipesList = mutableListOf<Recipe>()

                documents.forEach { document ->
                    val data = document.data
                    recipesList.add(
                        convertDocumentDataToRecipe(document.id, data)
                    )
                }

                recipes(recipesList)
            }
            .addOnFailureListener {
                println(it)
            }
    }

    fun getRecipe(id: String, callback: (Boolean, String?, Recipe?) -> Unit) {
        db.collection("recipes").document(id)
            .get()
            .addOnSuccessListener { document ->
                val data = document.data
                if (data != null) {
                    callback(true, null, convertDocumentDataToRecipe(document.id, data))
                } else {
                    callback(false, "Recipe document is null.", null)
                }
            }
            .addOnFailureListener {
                callback(false, getErrorMessage(it), null)
            }
    }

    fun getOwnRecipes(recipes: (List<Recipe>) -> Unit) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            db.collection("recipes")
                .whereEqualTo("userId", currentUser.uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    val recipesList = mutableListOf<Recipe>()

                    documents.forEach { document ->
                        val data = document.data

                        recipesList.add(
                            convertDocumentDataToRecipe(document.id, data)
                        )
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
//            "mealTypes" to recipe.mealTypes,
//            "categories" to recipe.categories,
            "ingredients" to recipe.ingredients,
            "instructions" to recipe.instructions,
            "audience" to recipe.audience
        )

        if (recipe.description.isNotEmpty()) data["description"] = recipe.description
        if (recipe.prepTimeHr.isNotEmpty()) data["prepTimeHr"] = recipe.prepTimeHr
        if (recipe.prepTimeMin.isNotEmpty()) data["prepTimeMin"] = recipe.prepTimeMin
        if (recipe.cookTimeHr.isNotEmpty()) data["cookTimeHr"] = recipe.cookTimeHr
        if (recipe.cookTimeMin.isNotEmpty()) data["cookTimeMin"] = recipe.cookTimeMin
        if (recipe.categories.isNotEmpty()) data["categories"] = recipe.categories
        if (recipe.mealTypes.isNotEmpty()) data["mealTypes"] = recipe.mealTypes
        if (recipe.tags.isNotEmpty()) data["tags"] = recipe.tags
        data["createdAt"] = FieldValue.serverTimestamp()
        data["updatedAt"] = FieldValue.serverTimestamp()

        db.collection("recipes")
            .add(data)  // adds the recipe to db
            .addOnSuccessListener { recipeDocRef ->
                recipe.id = recipeDocRef.id
                recipe.userId = user.uid
                recipe.userName = user.username
                recipe.userPhotoUrl = user.photoUrl

                // on success, upload the recipe photos
                if (recipe.photosUri.size > 0) {
                    uploadRecipePhotos(
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
                    updatedRecipe(recipe)
                    callback(true, null)
                }
            }
            .addOnFailureListener {
                callback(false, getErrorMessage(it))
            }
    }

    fun updateRecipe(
        data: HashMap<String, Any>,
        recipe: Recipe,
        updatedRecipe: (Recipe) -> Unit,
        callback: (Boolean, String?) -> Unit,
        deletePhotoKey: String?
    ) {
        if (recipe.photosUri.size > 0) {
            uploadRecipePhotos(
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

        if (data["audience"] == "Only me") {
            recipe.id?.let { removeFavoriteFromAllUsers(it) }
        }

        if (deletePhotoKey != null) {
            recipe.id?.let {
                deleteRecipePhoto(it, deletePhotoKey) { isSuccess, err ->
                    if (isSuccess) {
                        recipe.id?.let { recipeId ->
                            db.collection("recipes")
                                .document(recipeId)
                                .update("photosUrl.$deletePhotoKey", FieldValue.delete())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        recipe.photosUrl.remove(deletePhotoKey)
                                        updatedRecipe(recipe)
                                    }
                                    if (data.isEmpty()) {
                                        callback(task.isSuccessful, getErrorMessage(task.exception))
                                    }
                                }
                        }
                    } else {
                        if (data.isEmpty()) {
                            callback(false, err)
                        }
                    }
                }
            }
        }

        if (data.isNotEmpty()) {
            data["updatedAt"] = FieldValue.serverTimestamp()

            recipe.id?.let { recipeId ->
                db.collection("recipes")
                    .document(recipeId)
                    .update(data)
                    .addOnSuccessListener {
                        callback(true, null)
                    }
                    .addOnFailureListener {
                        callback(false, getErrorMessage(it))
                        println(it)
                    }
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

                removeFavoriteFromAllUsers(document)
            }
            .addOnFailureListener {
                callback(false, getErrorMessage(it))
            }
    }

    private fun uploadRecipePhotos(
        photosUri: Map<String, Uri>,
        recipe: Recipe,
        updatedRecipe: (Recipe) -> Unit,
        callback: (Boolean, String?) -> Unit
    ) {
        val storageRef = storage.reference
        val recipesRef = storageRef.child("recipes/${recipe.id}")

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
                    val recipeRef = recipe.id?.let { db.collection("recipes").document(it) }
                    recipeRef?.update(
                        mapOf(
                            "photosUrl.${photo.key}" to url,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )?.addOnSuccessListener {
                        recipe.photosUrl[photo.key] = url
                        recipe.photosUri.remove(photo.key)
                        updatedRecipe(recipe)
                    }?.addOnFailureListener {
                        println(it)
                    }?.addOnCompleteListener {
                        callback(true, null)
                    }
                } else {
                    callback(true, null)
                }
            }
        }
    }

    private fun deleteRecipePhoto(recipeId: String, key: String, callback: (Boolean, String?) -> Unit) {
        val storageRef = storage.reference
        val photoRef = storageRef.child("recipes/$recipeId/${key}.jpg")
        photoRef.delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, getErrorMessage(it.exception))
                    println(it.exception)
                }
            }
    }

    private fun convertDocumentDataToRecipe(id: String, data: MutableMap<String, Any>): Recipe {
        return Recipe(
            id = id,
            userId = data["userId"].toString(),
            userName = data["userName"].toString(),
            userPhotoUrl = data["userPhotoUrl"].toString(),
            photosUrl = data["photosUrl"] as? HashMap<String, Uri> ?: hashMapOf(),
            title = data["title"].toString(),
            description = data["description"].toString(),
            cookTimeHr = data["cookTimeHr"].toString(),
            cookTimeMin = data["cookTimeMin"].toString(),
            prepTimeHr = data["prepTimeHr"].toString(),
            prepTimeMin = data["prepTimeMin"].toString(),
            serving = data["serving"].toString(),
            difficulty = data["difficulty"].toString(),
            mealTypes = data["mealTypes"] as? List<String> ?: listOf(),
            categories = data["categories"] as? List<String> ?: listOf(),
            ingredients = data["ingredients"] as? List<String> ?: listOf(),
            instructions = data["instructions"] as? List<String> ?: listOf(),
            tags = data["tags"] as? List<String> ?: listOf(),
            audience = data["audience"].toString(),
            ratings = data["ratings"] as? HashMap<String, Float>,
            averageRating = (data["averageRating"] as? Double)?.toFloat(),
            userRating = null,
            isTikTok = data["isTikTok"] as? Boolean ?: false,
            postId = data["postId"] as? String,
        )
    }

    fun addFavoriteRecipe(recipeId: String, isAdd: Boolean, callback: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val favoriteRecipes = document.get("favoriteRecipes") as? List<String> ?: listOf()
                    val updatedFavorites = if (isAdd) {
                        if (!favoriteRecipes.contains(recipeId)) {
                            favoriteRecipes.toMutableList().apply { add(recipeId) }
                        } else {
                            return@addOnSuccessListener
                        }
                    } else {
                        favoriteRecipes.toMutableList().apply { remove(recipeId) }
                    }

                    userDocRef.update("favoriteRecipes", updatedFavorites).addOnSuccessListener {
                        callback(true)
                    }.addOnFailureListener {
                        callback(false)
                    }
                }
            }
        }
    }

    fun getUserFavoriteRecipes(callback: (List<String>) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            val userDocRef = db.collection("users").document(user.uid)
            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val favoriteRecipes = document.get("favoriteRecipes") as? List<String> ?: listOf()
                    callback(favoriteRecipes)
                } else {
                    callback(emptyList())
                }
            }.addOnFailureListener {
                callback(emptyList())
            }
        } else {
            callback(emptyList())
        }
    }

    fun removeCurrentUserFavorite(recipeId: String, callback: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    removeUserFavorite(recipeId, document) {
                        callback(it)
                    }
                } else {
                    callback(false)
                }
            }
        }
    }

    private fun removeFavoriteFromAllUsers(recipeId: String) {
        db.collection("users")
            .whereArrayContains("favoriteRecipes", recipeId)
            .get()
            .addOnSuccessListener { users ->
                if (!users.isEmpty) {
                    users.forEach { user ->
                        removeUserFavorite(recipeId, user) {
                            println(it)
                        }
                    }
                }
            }
    }

    private fun removeUserFavorite(recipeId: String, user: DocumentSnapshot, callback: (Boolean) -> Unit) {
        val favoriteRecipes = user.get("favoriteRecipes") as? MutableList<String> ?: mutableListOf()
        favoriteRecipes.remove(recipeId)

        db.collection("users")
            .document(user.id)
            .update("favoriteRecipes", favoriteRecipes)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun rateRecipe(recipeId: String, rating: Float, callback: (Boolean, Float?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false, null)
        val recipeRef = db.collection("recipes").document(recipeId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(recipeRef)
            val currentRatings = snapshot.get("ratings") as? HashMap<String, Float> ?: hashMapOf()
            val updatedRatings = currentRatings.toMutableMap().apply { this[userId] = rating }
            val newAverageRating = updatedRatings.values.average().toFloat()

            currentRatings[userId] = rating

            // Update Firestore
            transaction.update(recipeRef, mapOf(
                "ratings" to updatedRatings,
                "averageRating" to newAverageRating
            ))

            newAverageRating
        }.addOnSuccessListener { newAverageRating ->
            callback(true, newAverageRating)
        }.addOnFailureListener {
            callback(false, null)
        }
    }
}