package com.samsantech.souschef

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.samsantech.souschef.firebase.FirebaseAuthManager
import com.samsantech.souschef.firebase.FirebaseRecipeManager
import com.samsantech.souschef.firebase.FirebaseUserManager
import com.samsantech.souschef.ui.theme.SousChefTheme
import com.samsantech.souschef.viewmodel.AuthViewModel
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel
import com.samsantech.souschef.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SousChefTheme {
                val insets = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowInsets = this.window.decorView.rootWindowInsets
                    windowInsets?.getInsets(WindowInsets.Type.systemBars())?.bottom ?: 0
                } else {
                    val resources = this.resources
                    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
                    if (resourceId > 0) {
                        resources.getDimensionPixelSize(resourceId)
                    } else {
                        0
                    }
                }
                val systemNavigationBarHeight = (insets / LocalDensity.current.density).dp

                val auth = Firebase.auth
                val db = Firebase.firestore
                val storage = Firebase.storage

                val firebaseUserManager = FirebaseUserManager(auth, db, storage)
                val firebaseAuthManager = FirebaseAuthManager(auth, db, firebaseUserManager)
                val firebaseRecipeManager = FirebaseRecipeManager(auth, db, storage)

                val user = auth.currentUser

                val authViewModel = AuthViewModel(firebaseAuthManager)
                val userViewModel = UserViewModel(firebaseAuthManager, firebaseUserManager)
                val ownRecipesViewModel = OwnRecipesViewModel(userViewModel, firebaseRecipeManager)

                SousChefApp(
                    systemNavigationBarHeight,
                    user,
                    activity = this,
                    context = this,
                    authViewModel,
                    userViewModel,
                    ownRecipesViewModel
                )
            }
        }
    }
}