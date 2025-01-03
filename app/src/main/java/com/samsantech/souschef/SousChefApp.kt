package com.samsantech.souschef

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import com.samsantech.souschef.ui.CreateRecipeScreenOne
import com.samsantech.souschef.ui.CreateRecipeScreenThree
import com.samsantech.souschef.ui.CreateRecipeScreenTwo
import com.samsantech.souschef.ui.EditProfileScreen
import com.samsantech.souschef.ui.ResetPasswordScreen
import com.samsantech.souschef.ui.GetStartedScreen
import com.samsantech.souschef.ui.HomeScreen
import com.samsantech.souschef.ui.LoginScreen
import com.samsantech.souschef.ui.OpeningScreen
import com.samsantech.souschef.ui.ProfileScreen
import com.samsantech.souschef.ui.RecipeBrowserScreen
import com.samsantech.souschef.ui.RecipeScreen
import com.samsantech.souschef.ui.ChangePasswordScreen
import com.samsantech.souschef.ui.CreateRecipeScreenFour
import com.samsantech.souschef.ui.SearchScreen
import com.samsantech.souschef.ui.SelectCategoryScreen
import com.samsantech.souschef.ui.SelectCuisinesScreen
import com.samsantech.souschef.ui.SelectDislikesScreen
import com.samsantech.souschef.ui.SelectSkillLevelScreen
import com.samsantech.souschef.ui.SignUpOrLoginScreen
import com.samsantech.souschef.ui.SignUpScreen
import com.samsantech.souschef.ui.UpdateEmailScreen
import com.samsantech.souschef.ui.VerifyEmailScreen
import com.samsantech.souschef.ui.components.ContentBottomNavigationWrapper
import com.samsantech.souschef.viewmodel.AuthViewModel
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel
import com.samsantech.souschef.viewmodel.UserViewModel
import kotlinx.serialization.Serializable

@Composable
fun SousChefApp(
    systemNavigationBarHeight: Dp,
    user: FirebaseUser?,
    activity: ComponentActivity,
    context: Context,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    ownRecipesViewModel: OwnRecipesViewModel
) {
    Box {
        val navController = rememberNavController()


        NavHost(navController = navController, startDestination = Profile) {

            composable<Opening> {
                var afterOpening: Any = GetStarted
                if (user != null) {
                    afterOpening = Home
                }

                OpeningScreen(
                    onNavigateTo = { navController.navigate(route = afterOpening) }
                )
            }
            composable<GetStarted> {
                GetStartedScreen(
                    activity,
                    onNavigateToSignUpOrLogin = { navController.navigate(route = SignUpOrLogin) }
                )
            }
            composable<SignUpOrLogin> {
                SignUpOrLoginScreen(
                    onNavigateToLogin = { navController.navigate(route = Login) },
                    onNavigateToSignUp = { navController.navigate(route = SignUp) }
                )
            }
            composable<Login> {
                LoginScreen(
                    authViewModel,
                    userViewModel,
                    onNavigateToSignUp = { navController.navigate(route = SignUp) },
                    onNavigateToVerifyEmail = { navController.navigate(route = VerifyEmail) },
                    onNavigateToForgotPassword = { navController.navigate(route = ResetPassword) },
                    onNavigateToHome = {
                        navController.navigate(route = Profile) {
                            popUpTo(Profile) { inclusive = true }
                        }
                    },
                    onNavigateToSelectCuisines = {
                        navController.navigate(route = SelectCuisines) {
                            popUpTo(SelectCuisines) { inclusive = true }
                        }
                    }
                )
            }
            composable<VerifyEmail> {
                VerifyEmailScreen(
                    userViewModel,
                    authViewModel,
                    onNavigateToLogin = { navController.navigate(route = Login) }
                )
            }
            composable<ResetPassword> {
                ResetPasswordScreen(
                    authViewModel,
                    onNavigateToLogin = { navController.navigate(route = Login) }
                )
            }
            composable<ChangePassword> {
                ChangePasswordScreen(
                    authViewModel,
                    onNavigateToEditProfile = {
                        navController.navigate(route = EditProfile) {
                            popUpTo(EditProfile) { inclusive = true }
                        }
                    }
                )
            }
            composable<SignUp> {
                SignUpScreen(
                    authViewModel = authViewModel,
                    userViewModel,
                    onNavigateToVerifyEmail = {
                        navController.navigate(route = VerifyEmail) {
                            popUpTo(VerifyEmail) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.navigate(route = Login) },
                )
            }
            composable<SelectCuisines> {
                SelectCuisinesScreen(
                    activity,
                    userViewModel = userViewModel,
                    onNavigateToSelectDislikes = { navController.navigate(route = SelectDislikes) },
                )
            }
            composable<SelectDislikes> {
                SelectDislikesScreen(
                    userViewModel = userViewModel,
                    onNavigateToSelectCuisines = { navController.navigate(route = SelectCuisines) },
                    onNavigateToSelectSkillLevel = { navController.navigate(route = SelectSkillLevel) },
                )
            }
            composable<SelectSkillLevel> {
                SelectSkillLevelScreen(
                    userViewModel = userViewModel,
                    onNavigateToSelectDislikes = { navController.navigate(route = SelectDislikes) },
                    onNavigateToHome = {
                        navController.navigate(route = Profile) {
                            popUpTo(Profile) { inclusive = true }
                        }
                    },
                )
            }
            composable<EditProfile> {
                EditProfileScreen(
                    context,
                    authViewModel = authViewModel,
                    userViewModel = userViewModel,
                    onNavigateToProfile = {
                        navController.navigate(route = Profile) {
                            popUpTo(Profile) { inclusive = true }
                        }
                    },
                    onNavigateToUpdateEmail = { navController.navigate(route = UpdateEmail)},
                    onNavigateToChangePassword = { navController.navigate(route = ChangePassword) },
                    onNavigateToLogin = {
                        navController.navigate(route = Login) {
                            popUpTo(Login) { inclusive = true }
                        }
                    }
                )
            }
            composable<UpdateEmail> {
                UpdateEmailScreen(
                    authViewModel,
                    userViewModel,
                    onNavigateToLogin = {
                        navController.navigate(route = Login) {
                            popUpTo(Login) { inclusive = true }
                        }
                    }
                )
            }
            composable<Home> {
                ContentBottomNavigationWrapper(
                    systemNavigationBarHeight,
                    name = "Home",
                    onNavigateToHome = {
                        navController.navigate(route = Home)
                    },
                    onNavigateToCreateRecipe = {
                        navController.navigate(route = CreateRecipeOne)
                    },
                    onNavigateToSearch = {
                        navController.navigate(route = Search)
                    },
//                    onNavigateToTiktokVideos = {
//                        navController.navigate(route = TiktokVideos)
//                    },
                    onNavigateToProfile = {
                        navController.navigate(route = Profile) {
                            popUpTo(Profile) { inclusive = true }
                        }
                    },
                    ownRecipesViewModel
                ) { paddingValues ->
                    HomeScreen(paddingValues)
                }
            }
            composable<Profile> {
                ContentBottomNavigationWrapper(
                    systemNavigationBarHeight,
                    name = "Profile",
                    onNavigateToHome = {
                        navController.navigate(route = Home)
                    },
                    onNavigateToCreateRecipe = {
                        navController.navigate(route = CreateRecipeOne)
                    },
                    onNavigateToSearch = {
                        navController.navigate(route = Search)
                    },
//                    onNavigateToTiktokVideos = {
//                        navController.navigate(route = TiktokVideos)
//                    },
                    onNavigateToProfile = {
                        navController.navigate(route = Profile) {
                            popUpTo(Profile) { inclusive = true }
                        }
                    },
                    ownRecipesViewModel
                ) { paddingValues ->
                    ProfileScreen(
                        paddingValues,
                        context,
                        userViewModel,
                        ownRecipesViewModel,
                        onNavigateToEditProfile = { navController.navigate(route = EditProfile) },
                        onNavigateToRecipe = { navController.navigate(route = Recipe) },
                        onNavigateToCreateRecipeOne = { navController.navigate(route = CreateRecipeOne) }
                    )
                }
            }
            composable<Search> {
                ContentBottomNavigationWrapper(
                    systemNavigationBarHeight,
                    name = "Search",
                    onNavigateToHome = {
                        navController.navigate(route = Home)
                    },
                    onNavigateToCreateRecipe = {
                        navController.navigate(route = CreateRecipeOne)
                    },
                    onNavigateToSearch = {
                        navController.navigate(route = Search)
                    },
//                    onNavigateToTiktokVideos = {
//                        navController.navigate(route = TiktokVideos)
//                    },
                    onNavigateToProfile = {
                        navController.navigate(route = Profile) {
                            popUpTo(Profile) { inclusive = true }
                        }
                    },
                    ownRecipesViewModel
                ) { paddingValues ->
                    SearchScreen(paddingValues)
                }
            }
            composable<SelectCategory> {
                SelectCategoryScreen()
            }
            composable<RecipeBrowser> {
                RecipeBrowserScreen()
            }
            composable<Recipe> {
                RecipeScreen()
            }
            composable<CreateRecipeOne> {
                CreateRecipeScreenOne(
                    context,
                    ownRecipesViewModel,
                    onNavigateToCreateRecipeTwo = {
                        navController.navigate(route = CreateRecipeTwo)
                    },
                    closeCreateRecipe = {
                        navController.navigate(route = Home) {
                            popUpTo(CreateRecipeOne) { inclusive = true }
                        }
                        ownRecipesViewModel.resetRecipe()
                    }
                )
            }
            composable<CreateRecipeTwo> {
                CreateRecipeScreenTwo(
                    ownRecipesViewModel,
                    onNavigateToCreateRecipeOne = {
                        navController.navigate(route = CreateRecipeOne) {
                            popUpTo(CreateRecipeTwo) { inclusive = true }
                        }
                    },
                    onNavigateToCreateRecipeThree = {
                        navController.navigate(route = CreateRecipeThree)
                    },
                    closeCreateRecipe = {
                        navController.navigate(route = Home) {
                            popUpTo(CreateRecipeOne) { inclusive = true }
                        }
                        ownRecipesViewModel.resetRecipe()
                    }
                )
            }
            composable<CreateRecipeThree> {
                CreateRecipeScreenThree(
                    ownRecipesViewModel,
                    onNavigateToCreateRecipeTwo = {
                        navController.navigate(route = CreateRecipeTwo) {
                            popUpTo(CreateRecipeThree) { inclusive = true }
                        }
                    },
                    onNavigateToCreateRecipeFour = {
                        navController.navigate(route = CreateRecipeFour)
                    },
                    closeCreateRecipe = {
                        navController.navigate(route = Home) {
                            popUpTo(CreateRecipeOne) { inclusive = true }
                        }
                        ownRecipesViewModel.resetRecipe()
                    }
                )
            }
            composable<CreateRecipeFour> {
                CreateRecipeScreenFour(
                    context,
                    ownRecipesViewModel,
                    onNavigateToCreateRecipeThree = {
                        navController.navigate(route = CreateRecipeThree) {
                            popUpTo(CreateRecipeFour) { inclusive = true }
                        }
                    },
                    closeCreateRecipe = {
                        navController.navigate(route = Home) {
                            popUpTo(CreateRecipeOne) { inclusive = true }
                        }
                        ownRecipesViewModel.resetRecipe()
                    }
                )
            }
//            composable<TiktokVideos> {
//                TiktokVideosScreen()
//            }
        }
    }
}

@Serializable
object Opening

@Serializable
object GetStarted

@Serializable
object SignUpOrLogin

@Serializable
object SignUp

@Serializable
object SelectCuisines

@Serializable
object SelectDislikes

@Serializable
object SelectSkillLevel

@Serializable
object Login

@Serializable
object VerifyEmail

@Serializable
object ResetPassword

@Serializable
object ChangePassword

@Serializable
object Profile

@Serializable
object EditProfile

@Serializable
object UpdateEmail

@Serializable
object Home

@Serializable
object SelectCategory

@Serializable
object RecipeBrowser

@Serializable
object Recipe

@Serializable
object CreateRecipeOne

@Serializable
object CreateRecipeTwo

@Serializable
object CreateRecipeThree

@Serializable
object CreateRecipeFour

@Serializable
object Search

//@Serializable
//object TiktokVideos
