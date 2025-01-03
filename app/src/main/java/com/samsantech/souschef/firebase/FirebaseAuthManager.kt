package com.samsantech.souschef.firebase

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.samsantech.souschef.data.User

class FirebaseAuthManager(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val firebaseUserManager: FirebaseUserManager,
) {
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

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

    fun signUp(user: User, isSuccess: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener {
                var error: String? = null

                if (!it.isSuccessful) {
                    isSuccess(false, getErrorMessage(it.exception))
                    println(it.exception)
                } else {
                    val signedUpUser = getCurrentUser()
                    if (signedUpUser != null) {
                        firebaseUserManager.updateProfile(newDisplayName = user.displayName) { _, errorMessage ->
                            if (errorMessage != null) {
                                error = errorMessage
                            }
                            println(errorMessage)
                        }
                        createUser(signedUpUser.uid, user.email, user.username) {
                            isSuccess(true, error)
                            println(isSuccess)
                        }
                    }
                }
            }
    }

    private fun createUser(uid: String, email: String, username: String, isSuccess: (Boolean) -> Unit) {
        val user = hashMapOf(
            "username" to username,
            "email" to email
        )

        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                isSuccess(true)
            }
            .addOnFailureListener {
                println(it.message)
                isSuccess(false)
            }
    }

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    auth.currentUser?.sendEmailVerification()
                    onComplete(true, null)
                } else {
                    val exception = task.exception
                    onComplete(false, getErrorMessage(exception))
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun isUserVerified(): Boolean {
        val currentUser = auth.currentUser

        return currentUser?.isEmailVerified ?: false
    }

    fun sendEmailVerification(callback: (Boolean, String?) -> Unit) {
        val currentUser = auth.currentUser

        currentUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, "There was a problem sending a new verification email. Please try again later.")
                }
            }
    }

    fun changePassword(oldPassword: String, newPassword: String, callback: (Boolean, String?) -> Unit) {
        val user = getCurrentUser()

        if (user != null) {
            val credential = user.email?.let {
                EmailAuthProvider
                    .getCredential(it, oldPassword)
            }

            if (credential != null) {
                user.reauthenticate(credential)
                    .addOnCompleteListener{ task ->
                        if (!task.isSuccessful) {
                            callback(false, getErrorMessage(task.exception))
                        } else {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { updatePasswordTask ->
                                    if (!updatePasswordTask.isSuccessful) {
                                        callback(false, getErrorMessage(task.exception))
                                    } else {
                                        user.reload()
                                        callback(true, null)
                                    }
                                }
                        }
                    }
            }
        }
    }

    fun sendResetEmail(email: String, callback: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, getErrorMessage(task.exception))
                }
            }
    }
}