package com.samsantech.souschef.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.data.RecipePhotos
import com.samsantech.souschef.data.User
import androidx.core.net.toUri

// handles all the direct operations related with recipes to Firebase
class FirebaseRecipeManager(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
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
                .addOnFailureListener { println(it) }
        }
    }

    fun addRecipe(recipe: Recipe, user: User, callback: (Boolean, String?, Recipe?) -> Unit) {
        val data = hashMapOf(
            "userId" to user.uid,
            "userName" to user.username,
            "userPhotoUrl" to user.photoUrl,
            "title" to recipe.title,
            "serving" to recipe.serving,
            "difficulty" to recipe.difficulty,
            "ingredients" to recipe.ingredients,
            "instructions" to recipe.instructions,
            "audience" to recipe.audience,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (recipe.description.isNotEmpty()) data["description"] = recipe.description
        if (recipe.prepTimeHr.isNotEmpty()) data["prepTimeHr"] = recipe.prepTimeHr
        if (recipe.prepTimeMin.isNotEmpty()) data["prepTimeMin"] = recipe.prepTimeMin
        if (recipe.cookTimeHr.isNotEmpty()) data["cookTimeHr"] = recipe.cookTimeHr
        if (recipe.cookTimeMin.isNotEmpty()) data["cookTimeMin"] = recipe.cookTimeMin
        if (recipe.categories.isNotEmpty()) data["categories"] = recipe.categories
        if (recipe.tags.isNotEmpty()) data["tags"] = recipe.tags

        db.collection("recipes")
            .add(data)
            .addOnSuccessListener { recipeDocRef ->
                recipe.id = recipeDocRef.id
                recipe.userId = user.uid
                recipe.userName = user.username
                recipe.userPhotoUrl = user.photoUrl

                // on success, upload the recipe photos to storage
                if (recipe.photosUrl.isNotEmpty()) {
                    recipe.id?.let { recipeId ->
                        uploadRecipePhotos(
                            recipeId,
                            recipe.photosUrl,
                            callback = { isSuccess, err, uploadedPhotosUrl ->
                                if (!isSuccess) callback(false, err, null)
                                else {
                                    uploadedPhotosUrl?.let { recipe.photosUrl = it }
                                    callback(true, null, recipe)
                                }
                            }
                        )
                    }
                } else {
                    callback(true, null, recipe)
                }
            }
            .addOnFailureListener { callback(false, getErrorMessage(it), null) }
    }

    fun updateRecipe(updates: HashMap<String, Any>, recipe: Recipe, deletePhotoKey: String?, callback: (Boolean, String?, Recipe?) -> Unit) {
        // to be called later
        fun updateRecipeMetaData() {
            if (deletePhotoKey != null) {
                recipe.id?.let { recipeId ->
                    deleteRecipePhoto(recipeId, deletePhotoKey) { isSuccess, _ ->
                        if (isSuccess) {
                            recipe.photosUrl.remove(deletePhotoKey)
                            if (updates.isEmpty()) {
                                callback(true, null, recipe)
                            }
                        } // no callback for else here
                    }
                }
            }

            if (updates.isNotEmpty()) {
                updates["updatedAt"] = FieldValue.serverTimestamp()

                recipe.id?.let { recipeId ->
                    db.collection("recipes")
                        .document(recipeId)
                        .update(updates)
                        .addOnSuccessListener {
                            callback(true, null, recipe)
                        }
                        .addOnFailureListener {
                            callback(false, getErrorMessage(it), null)
                        }
                }
            }
        }

        if (recipe.photosUrl.isNotEmpty()) {
            recipe.id?.let { recipeId ->
                uploadRecipePhotos(recipeId, recipe.photosUrl) { isSuccess, err, uploadedPhotosUrl ->
                    if (!isSuccess) {
                        callback(false, err, null)
                        return@uploadRecipePhotos
                    }

                    recipe.photosUrl = uploadedPhotosUrl ?: hashMapOf()
                    if (updates.isEmpty() && deletePhotoKey == null) callback(true, null, recipe)
                    else updateRecipeMetaData()
                }
            }
        }
    }

    fun deleteRecipe(recipeId: String, photos: HashMap<String, Uri>, callback: (Boolean, String?) -> Unit) {
        db.collection("recipes")
            .document(recipeId)
            .delete()       // delete recipe from recipes collection
            .addOnSuccessListener {
                callback(true, null)

                // delete recipe from recipesPhotosUrl collection
                db.collection("recipesPhotosUrl")
                    .document(recipeId)
                    .delete()

                // delete photos of recipes from storage
                val storageRef = storage.reference
                val recipesRef = storageRef.child("recipes/${recipeId}")
                photos.forEach { photo ->
                    val photoRef = recipesRef.child("${photo.key}.jpg")
                    photoRef.delete()
                }

                removeFavoriteFromAllUsers(recipeId)
            }
            .addOnFailureListener {
                callback(false, getErrorMessage(it))
            }
    }

    private fun uploadRecipePhotos(
        recipeId: String,
        photosUri: HashMap<String, Uri>,
        callback: (Boolean, String?, HashMap<String, Uri>?) -> Unit
    ) {
        val storageRef = storage.reference
        val recipesRef = storageRef.child("recipes/${recipeId}")
        val photosUrl = mutableMapOf<String, Uri>()
        val photosUriSize = photosUri.size;
        var completedCount = 0
        val forUploadToRecipesCol = mutableMapOf<String, Any>()

        photosUri.forEach { photo ->
            // uploads each photo to storage
            val uploadRef = recipesRef.child("${photo.key}.jpg")
            val uploadTask = uploadRef.putFile(photo.value)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) println(task.exception)
                uploadRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val url = task.result.toString().toUri()
                    photosUrl[photo.key] = url  // for upload to recipesPhotosUrl collection and to be returned with callback
                    forUploadToRecipesCol["photosUrl.${photo.key}"] = url
                }

                completedCount++

                // update the photosUrl field in recipes and recipesPhotosUrl collections
                if (completedCount == photosUriSize) {
                    db.collection("recipes")
                        .document(recipeId)
                        .update(forUploadToRecipesCol)
                        .addOnSuccessListener {
                            db.collection("recipesPhotosUrl")
                                .document(recipeId)
                                .update(HashMap<String, Any>(photosUrl))
                                .addOnSuccessListener {
                                    callback(true, null, HashMap(photosUrl))
                                }
                                .addOnFailureListener {
                                    callback(false, "There was an error uploading the photos.", null)
                                }
                        }
                }
            }
        }
    }

    private fun deleteRecipePhoto(recipeId: String, key: String, callback: (Boolean, String?) -> Unit) {
        val storageRef = storage.reference

        // delete photos from storage
        storageRef.child("recipes/$recipeId/${key}.jpg")
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, null)

                    // delete the photos url in recipes collection
                    db.collection("recipes")
                        .document(recipeId)
                        .update("photosUrl.$key", FieldValue.delete())

                    // delete the photos url in recipesPhotosUrl collection
                    db.collection("recipesPhotosUrl")
                        .document(recipeId)
                        .update(key, FieldValue.delete())
                } else {
                    callback(false, getErrorMessage(it.exception))
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
//            mealTypes = data["mealTypes"] as? List<String> ?: listOf(),
            categories = data["categories"] as? List<String> ?: listOf(),
            ingredients = data["ingredients"] as? List<String> ?: listOf(),
            instructions = data["instructions"] as? List<String> ?: listOf(),
            tags = data["tags"] as? List<String> ?: listOf(),
            audience = data["audience"].toString(),
            ratings = data["ratings"] as? HashMap<String, Double>
//            isTikTok = data["isTikTok"] as? Boolean ?: false,
//            postId = data["postId"] as? String,
        )
    }

    // gets the photos only of recipes, not the whole data
    fun getRecipesPhotos(recipeIds: List<String>, onComplete: (MutableList<RecipePhotos>) -> Unit) {
        val recipesPhotos: MutableList<RecipePhotos> = mutableListOf()

        recipeIds.forEachIndexed { index, recipeId ->
            db.collection("recipesPhotosUrl")
                .document(recipeId)
                .get()
                .addOnSuccessListener { document ->
                    val data = document.data
                    if (data != null) {
                        recipesPhotos.add(RecipePhotos(
                            recipeId,
                            data as? HashMap<String, Uri> ?: hashMapOf()
                        ))
                    }
                }
                .addOnFailureListener {
                    println("error in FirebaseRecipeManager:getFavoriteRecipesPhotos")
                }
                .addOnCompleteListener {
                    if (index == (recipeIds.size - 1)) onComplete(recipesPhotos)
                }
        }
    }

    fun toggleFavoriteRecipe(recipeId: String, isAdd: Boolean, callback: (Boolean) -> Unit) {
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

    // removes the recipe to all users' favorite recipes
    private fun removeFavoriteFromAllUsers(recipeId: String) {
        db.collection("users")
            .whereArrayContains("favoriteRecipes", recipeId)
            .get()      // retrieves all users that have the recipe as a favorite
            .addOnSuccessListener { users ->
                if (!users.isEmpty) {
                    users.forEach { user ->
                        val favoriteRecipes = user.get("favoriteRecipes") as? MutableList<String> ?: mutableListOf()
                        favoriteRecipes.remove(recipeId)

                        db.collection("users")
                            .document(user.id)
                            .update("favoriteRecipes", favoriteRecipes)
                    }
                }
            }
    }

    fun rateRecipe(recipeId: String, rating: Float, callback: (Boolean, Float?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false, null)
        val recipeRef = db.collection("recipes").document(recipeId)

        recipeRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val ratings = document.get("ratings") as? HashMap<String, Float> ?: hashMapOf()
                val updatedRatings = ratings.toMutableMap().apply { this[userId] = rating }

                recipeRef
                    .update("ratings", updatedRatings)
                    .addOnSuccessListener { callback(true, updatedRatings.values.average().toFloat()) }
                    .addOnFailureListener { callback(false, null) }
            } else {
                callback(false, null)
            }
        }
    }

    fun removeRecipeRating(recipeId: String, callback: (Boolean, Float?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false, null)
        val recipeRef = db.collection("recipes").document(recipeId)

        recipeRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val ratings = document.get("ratings") as? HashMap<String, Float> ?: hashMapOf()
                ratings.remove(userId)

                recipeRef
                    .update("ratings", ratings)
                    .addOnSuccessListener { callback(true, if (ratings.isEmpty()) 0f else ratings.values.average().toFloat()) }
                    .addOnFailureListener { callback(false, null) }
            } else {
                callback(false, null)
            }
        }
    }

    fun setSeenPost(recipeId: String) {
        val user = auth.currentUser

        user?.uid.let { userId ->
            db.collection("recipes")
                .document(recipeId)
                .update("seenBy", FieldValue.arrayUnion(userId))
        }
    }
}