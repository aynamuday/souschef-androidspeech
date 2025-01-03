package com.samsantech.souschef.viewmodel

import android.net.Uri
import com.samsantech.souschef.data.User
import com.samsantech.souschef.data.UserPreferences
import com.samsantech.souschef.firebase.FirebaseAuthManager
import com.samsantech.souschef.firebase.FirebaseUserManager
import kotlinx.coroutines.flow.MutableStateFlow

class UserViewModel(
    private val firebaseAuthManager: FirebaseAuthManager,
    private val firebaseUserManager: FirebaseUserManager
) {
    val user = MutableStateFlow<User?>(User())
    val signUpPreferences = MutableStateFlow<UserPreferences>(UserPreferences())
    val otherCuisine = MutableStateFlow<String>("")

    init {
        refreshUser()
    }

    fun refreshUser() {
        val currentUser = firebaseAuthManager.getCurrentUser()

        if (currentUser != null) {
            firebaseAuthManager.getUser(currentUser.uid) {
                if (it != null) {
                    user.value = currentUser.email?.let { email ->
                        currentUser.displayName?.let { displayName ->
                            User(
                                uid = currentUser.uid,
                                username = it.username,
                                email = email,
                                displayName = displayName,
                                photoUrl = currentUser.photoUrl.toString()
                            )
                        }
                    }
                }
            }
        } else {
            user.value = null
        }
    }

    fun setProfilePicture(imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        firebaseUserManager.updateProfilePhoto(imageUri) { isSuccess, error ->
            if (isSuccess) {
                refreshUser()
            }
            callback(isSuccess, error)
        }
    }

    fun updateProfile(name: String? = null, username: String? = null, callback: (Boolean, String?) -> Unit) {
        firebaseUserManager.updateProfile(newDisplayName = name, username = username) { isSuccess, error ->
            if (isSuccess) {
                refreshUser()
            }
            callback(isSuccess, error)
        }
    }

    fun updateEmail(newEmail: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseUserManager.updateEmail(newEmail, password) { isSuccess, error ->
            if (isSuccess) {
                refreshUser()
            }
            callback(isSuccess, error)
        }
    }

    fun isUsernameExists(username: String, isExists: (Boolean) -> Unit) {
        firebaseUserManager.isUsernameExists(username) {
            isExists(it)
        }
    }

    fun isEmailExists(email: String, isExists: (Boolean) -> Unit) {
        firebaseUserManager.isEmailExists(email) {
            isExists(it)
        }
    }

    fun isUserPreferencesExists(callback: (Boolean) -> Unit) {
        firebaseUserManager.isUserPreferencesExists() {
            callback(it)
        }
    }

    fun setUserPreferences(isSuccess: (Boolean) -> Unit) {
        signUpPreferences.value.cuisines = signUpPreferences.value.cuisines?.plus(otherCuisine.value)
        firebaseUserManager.updateUserPreferences(signUpPreferences.value) {
            isSuccess(it)
        }
    }

    fun addPreferencesCuisine(cuisine: String) {
        signUpPreferences.value = signUpPreferences.value.copy(
            cuisines = signUpPreferences.value.cuisines?.plus(cuisine) ?: listOf(cuisine)
        )
    }

    fun removePreferencesCuisine(cuisine: String) {
        signUpPreferences.value = signUpPreferences.value.copy(
            cuisines = signUpPreferences.value.cuisines?.minus(cuisine) ?: listOf(cuisine)
        )
    }

    fun clearPreferencesCuisine() {
        signUpPreferences.value = signUpPreferences.value.copy(cuisines = listOf())
    }

    fun addPreferencesDislike(dislike: String) {
        signUpPreferences.value = signUpPreferences.value.copy(
            dislikes = signUpPreferences.value.dislikes?.plus(dislike) ?: listOf(dislike)
        )
    }

    fun removePreferencesDislike(dislike: String) {
        signUpPreferences.value = signUpPreferences.value.copy(
            dislikes = signUpPreferences.value.dislikes?.minus(dislike) ?: listOf(dislike)
        )
    }

    fun clearPreferencesDislikes() {
        signUpPreferences.value = signUpPreferences.value.copy(dislikes = listOf())
    }

    fun setPreferencesSkillLevel(skillLevel: String) {
        signUpPreferences.value = signUpPreferences.value.copy(
            skillLevel = skillLevel
        )
    }

    fun clearPreferencesSkillLevel() {
        signUpPreferences.value = signUpPreferences.value.copy(skillLevel = "")
    }
}