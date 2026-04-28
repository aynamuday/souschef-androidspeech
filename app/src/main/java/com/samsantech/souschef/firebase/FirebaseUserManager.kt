package com.samsantech.souschef.firebase

import android.net.Uri
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.storage.FirebaseStorage
import com.samsantech.souschef.data.User
import com.samsantech.souschef.data.UserPreferences
import androidx.core.net.toUri

class FirebaseUserManager(private val auth: FirebaseAuth, private val db: FirebaseFirestore, private val storage: FirebaseStorage) {
    fun getUser(uid: String, callback: (User?) -> Unit) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if (user != null) {
                    callback(user)
                } else {
                    callback(null)
                }
            }
    }

    fun getUserPreferences(callback: (UserPreferences?) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            db.collection("preferences")
                .document(user.uid)
                .get()
                .addOnSuccessListener {
                    val userPreferences = it.toObject(UserPreferences::class.java)
                    if (userPreferences != null) {
                        callback(userPreferences)
                    } else {
                        callback(null)
                    }
                }
        }
    }

    fun updateUserPreferences(preferences: UserPreferences, isSuccess: (Boolean) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            db.collection("preferences")
                .document(user.uid)
                .set(preferences)
                .addOnSuccessListener {
                    isSuccess(true)
                    user.reload()
                }
        } else {
            isSuccess(false)
        }
    }

    fun isUserPreferencesExists(callback: (Boolean) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            db.collection("preferences")
                .document(user.uid)
                .get()
                .addOnSuccessListener {
                    callback(it.exists())
                }
                .addOnFailureListener {
                    callback(false)
                }
        }
    }

    fun updateProfile(username: String? = null, newDisplayName: String? = null, email: String? = null, callback: (Boolean, String?) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            if (newDisplayName != null) {
                val profileUpdates = userProfileChangeRequest {
                    displayName = newDisplayName
                }
                user.updateProfile(profileUpdates)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) {
                            callback(false, getErrorMessage(it.exception))
                        } else {
                            callback(true, null)
                            user.reload()
                        }
                    }
            }
            val updatedUser = hashMapOf<String, String>()
            updatedUser["username"] = username ?: ""
            updatedUser["email"] = if (email != null) "$email" else "${user.email}"
            if (updatedUser.isNotEmpty()) {
                db.collection("users")
                    .document(user.uid)
                    .set(updatedUser)
                    .addOnSuccessListener {
                        callback(true, null)
                        user.reload()

                        if (!updatedUser["username"].isNullOrEmpty()) {
                            db.collection("recipes")
                                .whereEqualTo("userId", user.uid)
                                .get()
                                .addOnSuccessListener { recipes ->
                                    if (!recipes.isEmpty) {
                                        recipes.forEach { document ->
                                            db.collection("recipes")
                                                .document(document.id)
                                                .update("userName", updatedUser["username"])
                                        }
                                    }
                                }
                        }
                    }
                    .addOnFailureListener{
                        println(it.message)
                    }
            }
        }
    }

    fun updateProfilePhoto(imageUri: Uri, callback: (Boolean, String?, String?) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            val storageRef = storage.reference
            val userProfileRef = storageRef.child("profile/${user.uid}.jpg")

            val uploadTask = userProfileRef.putFile(imageUri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    callback(false, getErrorMessage(task.exception), null)
                    println(task.exception)
                }

                userProfileRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    val profileUpdates = userProfileChangeRequest {
                        photoUri = "$downloadUri".toUri()
                    }
                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { it ->
                            if (!it.isSuccessful) {
                                callback(false, getErrorMessage(task.exception), null)
                            } else {
                                user.reload()
                                callback(true, null, downloadUri.toString())

                                db.collection("recipes")
                                    .whereEqualTo("userId", user.uid)
                                    .get()
                                    .addOnSuccessListener { recipes ->
                                        if (!recipes.isEmpty) {
                                            recipes.forEach { document ->
                                                db.collection("recipes")
                                                    .document(document.id)
                                                    .update("userPhotoUrl", "$downloadUri".toUri())
                                            }
                                        }
                                    }
                            }
                        }
                } else {
                    callback(false, getErrorMessage(task.exception), null)
                    println(task.exception)
                }
            }
        }
    }

    fun updateEmail(newEmail: String, username: String?, password: String, callback: (Boolean, String?) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            val credential = user.email?.let {
                EmailAuthProvider
                    .getCredential(it, password)
            }

            if (credential != null) {
                user.reauthenticate(credential)
                    .addOnCompleteListener{ task ->
                        if (!task.isSuccessful) {
                            callback(false, getErrorMessage(task.exception))
                        } else {
                            user.verifyBeforeUpdateEmail(newEmail)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        updateProfile(username = username, email = newEmail) { _, err ->
                                            println(err)
                                        }
                                        callback(true, null)
                                        user.reload()
                                    } else {
                                        callback(false, getErrorMessage(it.exception))
                                    }
                                }
                        }
                    }
            }
        }
    }

    fun isUsernameExists(username: String, isExists: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo("username", username)
            .limit(1)
            .get()
            .addOnSuccessListener {
                isExists(!it.isEmpty)
            }
    }

    fun isEmailExists(email: String, isExists: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                isExists(!it.isEmpty)
            }
    }

    fun incrementSentEventsCount() {
        val user = auth.currentUser
        val data: Map<String, Any> = mapOf(
            "sentEventsCount" to FieldValue.increment(1.0),
            "lastSentEventTimestamp" to FieldValue.serverTimestamp()
        )

        user?.uid?.let { userId ->
            db.collection("users")
                .document(userId)
                .update(data)
        }
    }
}