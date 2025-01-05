package com.samsantech.souschef.viewmodel

import com.samsantech.souschef.data.User
import com.samsantech.souschef.firebase.FirebaseAuthManager
import kotlinx.coroutines.flow.MutableStateFlow

class AuthViewModel(
    private val firebaseAuthManager: FirebaseAuthManager,
) {
    val signUpInformation = MutableStateFlow<User>(User())
    val otpState = MutableStateFlow("")
    val newPassword = MutableStateFlow("")

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        firebaseAuthManager.login(email, password) { isSuccess, error ->
            onComplete(isSuccess, error)
        }
    }

    fun logout() {
        firebaseAuthManager.logout()
    }

    fun signUp(isSuccess: (Boolean, String?) -> Unit) {
        firebaseAuthManager.signUp(signUpInformation.value) { success, error ->
            isSuccess(success, error)
        }
    }

    fun setSignUpInformation(displayName: String? = null, username: String? = null, email: String? = null, password: String? = null) {
        signUpInformation.value = User(
            displayName = displayName?: signUpInformation.value.displayName,
            username = username?: signUpInformation.value.username,
            email = email?: signUpInformation.value.email,
            password = password?: signUpInformation.value.password
        )
    }

    fun isUserVerified(): Boolean {
        return firebaseAuthManager.isUserVerified()
    }

    fun sendEmailVerification(callback: (Boolean, String?) -> Unit) {
        firebaseAuthManager.sendEmailVerification() { isSuccess, error ->
            callback(isSuccess, error)
        }
    }

    fun changePassword(oldPassword: String, newPassword: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuthManager.changePassword(oldPassword, newPassword) { isSuccess, error ->
            callback(isSuccess, error)
        }
    }

//    fun resetPassword(email: String, callback: (Boolean, String?) -> Unit) {
//        firebaseAuthManager.sendResetEmail(email) { isSuccess, error ->
//            callback(isSuccess, error)
//        }
//    }

    // Send OTP to email
    fun sendOtpToEmail(email: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuthManager.sendOtpToEmail(email) { isSuccess, error ->
            callback(isSuccess, error)
        }
    }

    // Verify OTP
    fun verifyOtp(email: String, otp: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuthManager.verifyOtp(email, otp) { isSuccess, error ->
            callback(isSuccess, error)
        }
    }

    // Reset password after OTP verification
    fun resetPassword(newPassword: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuthManager.resetPassword(newPassword) { isSuccess, error ->
            callback(isSuccess, error)
        }
    }
}