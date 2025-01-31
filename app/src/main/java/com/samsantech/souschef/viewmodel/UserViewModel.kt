package com.samsantech.souschef.viewmodel

import android.net.Uri
import com.samsantech.souschef.data.OwnRecipesViewModelProvider
import com.samsantech.souschef.data.User
import com.samsantech.souschef.data.UserPreferences
import com.samsantech.souschef.firebase.FirebaseAuthManager
import com.samsantech.souschef.firebase.FirebaseUserManager
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Calendar

class UserViewModel(
    private val firebaseAuthManager: FirebaseAuthManager,
    private val firebaseUserManager: FirebaseUserManager,
    private val sharedViewModel: SharedViewModel,
    private val homeViewModel: HomeViewModel
) {
    val user = MutableStateFlow<User?>(User())
    val signUpPreferences = MutableStateFlow(UserPreferences())
//    val otherCuisine = MutableStateFlow("")
    private val ownRecipesViewModel: OwnRecipesViewModel
        get() = OwnRecipesViewModelProvider.ownRecipesViewModel

    init {
        refreshUser()
    }

    fun refreshUser() {
        val currentUser = firebaseAuthManager.getCurrentUser()

        if (currentUser != null) {
            firebaseUserManager.getUser(currentUser.uid) {
                if (it != null) {
                    user.value = currentUser.email?.let { email ->
                        currentUser.displayName?.let { displayName ->
                            User(
                                uid = currentUser.uid,
                                username = it.username,
                                email = email,
                                displayName = displayName,
                                photoUrl = currentUser.photoUrl.toString(),
                                sentEventsCount = it.sentEventsCount
                            )
                        }
                    }

                    val lastSentEventTimeStamp = it.lastSentEventTimestamp
                    if (lastSentEventTimeStamp != null) {
                        val currentTime = Calendar.getInstance().time
                        val differenceInMillis = lastSentEventTimeStamp.time - currentTime.time
                        val threeHoursInMillis = 3 * 60 * 60 * 1000

                        if (differenceInMillis >= threeHoursInMillis && it.sentEventsCount < 30) {
                            firebaseUserManager.getUserPreferences { preferences ->
                                if (preferences != null && !preferences.categories.isNullOrEmpty()) {
                                    homeViewModel.updateUserToken(user.value?.uid, preferences.categories)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            user.value = null
        }

        sharedViewModel.updateAlgoliaQueriesUserToken(currentUser?.uid)
    }

    fun setProfilePicture(imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        firebaseUserManager.updateProfilePhoto(imageUri) { isSuccess, error, photoUrl ->
            if (isSuccess) {
                refreshUser()
                if (photoUrl != null) {
                    ownRecipesViewModel.updateRecipesUserPhotoUrl(photoUrl)
                }
            }
            callback(isSuccess, error)
        }
    }

    fun updateProfile(name: String? = null, username: String? = null, callback: (Boolean, String?) -> Unit) {
        firebaseUserManager.updateProfile(newDisplayName = name, username = username) { isSuccess, error ->
            if (isSuccess) {
                refreshUser()

                if (!username.isNullOrEmpty()) {
                    ownRecipesViewModel.updateRecipesUserName(username)
                }
            }
            callback(isSuccess, error)
        }
    }

    fun updateEmail(newEmail: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseUserManager.updateEmail(newEmail, user.value?.username, password) { isSuccess, error ->
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
//        signUpPreferences.value.categories = signUpPreferences.value.categories?.plus(otherCuisine.value)
        firebaseUserManager.updateUserPreferences(signUpPreferences.value) {
            isSuccess(it)
//            userPreferences.value = signUpPreferences.value

            val categories = signUpPreferences.value.categories
            if (!categories.isNullOrEmpty()) {
                homeViewModel.updateUserToken(user.value?.uid, categories)
            }

            signUpPreferences.value = UserPreferences()
        }
    }

    fun addPreferencesCuisine(cuisine: String) {
        signUpPreferences.value = signUpPreferences.value.copy(
            categories = signUpPreferences.value.categories?.plus(cuisine) ?: listOf(cuisine)
        )
    }

    fun removePreferencesCuisine(cuisine: String) {
        signUpPreferences.value = signUpPreferences.value.copy(
            categories = signUpPreferences.value.categories?.minus(cuisine) ?: listOf(cuisine)
        )
    }

    fun clearPreferencesCategories() {
        signUpPreferences.value = signUpPreferences.value.copy(categories = listOf())
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

    fun incrementSentEventsCount() {
        firebaseUserManager.incrementSentEventsCount()
    }
}