package com.samsantech.souschef

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import com.samsantech.souschef.data.NetworkStateProvider
import com.samsantech.souschef.ui.CreateRecipeScreenOne
import com.samsantech.souschef.ui.CreateRecipeScreenThree
import com.samsantech.souschef.ui.CreateRecipeScreenTwo
import com.samsantech.souschef.ui.EditProfileScreen
import com.samsantech.souschef.ui.ResetPasswordScreen
import com.samsantech.souschef.ui.GetStartedScreen
import com.samsantech.souschef.ui.LoginScreen
import com.samsantech.souschef.ui.OpeningScreen
import com.samsantech.souschef.ui.ProfileScreen
import com.samsantech.souschef.ui.ChangePasswordScreen
import com.samsantech.souschef.ui.CreateRecipeScreenFour
import com.samsantech.souschef.ui.HomeScreen
import com.samsantech.souschef.ui.RecipeScreen
import com.samsantech.souschef.ui.SearchScreen
import com.samsantech.souschef.ui.SelectCategoriesScreen
import com.samsantech.souschef.ui.SelectDislikesScreen
import com.samsantech.souschef.ui.SelectSkillLevelScreen
import com.samsantech.souschef.ui.SignUpOrLoginScreen
import com.samsantech.souschef.ui.SignUpScreen
import com.samsantech.souschef.ui.UpdateEmailScreen
import com.samsantech.souschef.ui.VerifyEmailScreen
import com.samsantech.souschef.ui.components.ContentBottomNavigationWrapper
import com.samsantech.souschef.ui.components.CookingAssistantUi
import com.samsantech.souschef.viewmodel.AlgoliaInsightsViewModel
import com.samsantech.souschef.viewmodel.AuthViewModel
import com.samsantech.souschef.viewmodel.CookingAssistantViewModel
import com.samsantech.souschef.viewmodel.HomeViewModel
import com.samsantech.souschef.viewmodel.SharedViewModel
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel
import com.samsantech.souschef.viewmodel.RecipesViewModel
import com.samsantech.souschef.viewmodel.SearchRecipesViewModel
import com.samsantech.souschef.viewmodel.UserViewModel
import kotlinx.serialization.Serializable

@Composable
fun SousChefApp(
    systemNavigationBarHeight: Dp,
    user: FirebaseUser?,
    activity: ComponentActivity,
    context: Context,
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    ownRecipesViewModel: OwnRecipesViewModel,
    recipesViewModel: RecipesViewModel,
    searchRecipesViewModel: SearchRecipesViewModel,
    cookingAssistantViewModel: CookingAssistantViewModel,
    homeViewModel: HomeViewModel,
    algoliaInsightsViewModel: AlgoliaInsightsViewModel
) {
//    val isLoading = sharedViewModel.isLoading.collectAsState()
    val cookingAssistantState by cookingAssistantViewModel.cookingAssistantState.collectAsState()

    Column(modifier = Modifier.padding(bottom = systemNavigationBarHeight)) {
        Box(modifier = Modifier.weight(1f)) {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = Opening) {
                composable<Opening> {
                    var afterOpening: Any = GetStarted
                    if (user != null) {
                        afterOpening = Home
                    }

                    OpeningScreen(
                        onNavigateTo = { navController.navigate(route = afterOpening) {
                            popUpTo<Opening> { inclusive = true }
                        } }
                    )
                }
                composable<GetStarted> {
                    GetStartedScreen(
                        activity,
                        onNavigateToSignUpOrLogin = {
                            navController.navigate(route = SignUpOrLogin) {
                                popUpTo<GetStarted> { inclusive = true }
                            }
                        }
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
                        ownRecipesViewModel,
                        onNavigateToSignUp = { navController.navigate(route = SignUp) {
                            popUpTo<SignUp> { inclusive = true }
                        } },
                        onNavigateToVerifyEmail = { navController.navigate(route = VerifyEmail) },
                        onNavigateToForgotPassword = { navController.navigate(route = ResetPassword) {
                            popUpTo<ResetPassword> { inclusive = true }
                        } },
                        onNavigateToHome = {
                            navController.navigate(route = Home) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        },
                        onNavigateToSelectCategories = {
                            navController.navigate(route = SelectCategories) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    )
                }
                composable<VerifyEmail> {
                    VerifyEmailScreen(
                        authViewModel,
                        onNavigateToLogin = { navController.navigate(route = Login) {
                            popUpTo<VerifyEmail> { inclusive = true }
                            popUpTo<Login> { inclusive = true }
                        } }
                    )
                }
                composable<ResetPassword> {
                    ResetPasswordScreen(
                        authViewModel,
                        onNavigateToLogin = { navController.navigate(route = Login) {
                            popUpTo<Login> { inclusive = true }
                        } }
                    )
                }
                composable<ChangePassword> {
                    ChangePasswordScreen(
                        authViewModel,
                        onNavigateToEditProfile = {
                            navController.navigate(route = EditProfile) {
                                popUpTo(EditProfile) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            navController.navigate(route = Login) {
                                popUpTo(navController.graph.id) { inclusive = true }
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
                        onNavigateToLogin = { navController.navigate(route = Login) {
                            popUpTo<Login> { inclusive = true }
                        } },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
                composable<SelectCategories> {
                    SelectCategoriesScreen(
                        activity,
                        userViewModel = userViewModel,
                        onNavigateToHome = {
                            navController.navigate(route = Home) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
//                        onNavigateToSelectDislikes = { navController.navigate(route = SelectDislikes) },
                    )
                }
                composable<SelectDislikes> {
                    SelectDislikesScreen(
                        userViewModel = userViewModel,
                        onNavigateToSelectCategories = { navController.navigate(route = SelectCategories) {
                            popUpTo<SelectCategories> { inclusive = true }
                        } },
                        onNavigateToSelectSkillLevel = { navController.navigate(route = SelectSkillLevel) },
                    )
                }
                composable<SelectSkillLevel> {
                    SelectSkillLevelScreen(
                        userViewModel = userViewModel,
                        onNavigateToSelectDislikes = { navController.navigate(route = SelectDislikes) {
                            popUpTo<SelectDislikes> { inclusive = true }
                        } },
                        onNavigateToHome = {
                            navController.navigate(route = Home) {
                                popUpTo(navController.graph.id) { inclusive = true }
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
                                popUpTo(route = Profile) { inclusive = true }
                            }
                        },
                        onNavigateToUpdateEmail = { navController.navigate(route = UpdateEmail)},
                        onNavigateToChangePassword = { navController.navigate(route = ChangePassword) },
                        onNavigateToLogin = {
                            navController.navigate(route = Login) {
                                popUpTo(navController.graph.id) { inclusive = true }
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
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    )
                }
                composable<Home> {
                    ContentBottomNavigationWrapper(
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
                        onNavigateToProfile = {
                            navController.navigate(route = Profile) {
                                popUpTo(Profile) { inclusive = true }
                            }
                        },
                        ownRecipesViewModel
                    ) { paddingValues ->
                        HomeScreen(
                            context,
                            paddingValues,
                            homeViewModel,
                            recipesViewModel,
                            userViewModel,
                            algoliaInsightsViewModel,
                            onNavigateToRecipe = {
                                navController.navigate(route = Recipe)
                            },
                            cookingAssistantState.isCooking
                        )
                        DisplayCookingAssistantUi(
                            context,
                            cookingAssistantViewModel = cookingAssistantViewModel,
                            recipesViewModel = recipesViewModel,
                            sharedViewModel,
                            onNavigateToRecipe = {
                                navController.navigate(route = Recipe) {
                                    popUpTo<Recipe> { inclusive = true }
                                }
                            },
                            bottomHeight = 45.dp,
                            isCooking = cookingAssistantState.isCooking
                        )
                    }
                }
                composable<Profile> {
                    ContentBottomNavigationWrapper(
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
                            recipesViewModel,
                            onNavigateToEditProfile = { navController.navigate(route = EditProfile) },
                            onNavigateToRecipe = { navController.navigate(route = Recipe) },
                            onNavigateToCreateRecipeOne = { navController.navigate(route = CreateRecipeOne) },
                            isCooking = cookingAssistantState.isCooking
                        )
                        DisplayCookingAssistantUi(
                            context,
                            cookingAssistantViewModel = cookingAssistantViewModel,
                            recipesViewModel = recipesViewModel,
                            sharedViewModel,
                            onNavigateToRecipe = {
                                navController.navigate(route = Recipe) {
                                    popUpTo<Recipe>() { inclusive = true }
                                }
                            },
                            bottomHeight = 45.dp,
                            isCooking = cookingAssistantState.isCooking
                        )
                    }
                }
                composable<Search> {
                    ContentBottomNavigationWrapper(
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
                        onNavigateToProfile = {
                            navController.navigate(route = Profile) {
                                popUpTo(Profile) { inclusive = true }
                            }
                        },
                        ownRecipesViewModel
                    ) { paddingValues ->
                        SearchScreen(
                            context,
                            paddingValues,
                            searchRecipesViewModel,
                            recipesViewModel,
                            algoliaInsightsViewModel,
                            onNavigateToRecipe = {
                                navController.navigate(route = Recipe)
                            }
                        )
                    }
                }
                composable<Recipe> {
                    RecipeScreen(
                        context,
                        activity,
                        recipesViewModel,
                        onNavigateToPreviousScreen = { navController.popBackStack() },
                        userViewModel,
                        ownRecipesViewModel,
                        cookingAssistantViewModel,
                        onNavigateToCreateRecipeOne = {navController.navigate(route = CreateRecipeOne)},
                        onNavigateToProfile = { navController.navigate(route = Profile) {
                            popUpTo(Recipe) {inclusive = true}
                        } },
                        sharedViewModel = sharedViewModel,
                        algoliaInsightsViewModel = algoliaInsightsViewModel
                    )
                    DisplayCookingAssistantUi(
                        context,
                        cookingAssistantViewModel = cookingAssistantViewModel,
                        recipesViewModel = recipesViewModel,
                        sharedViewModel,
                        onNavigateToRecipe = {
                            navController.navigate(route = Recipe) {
                                popUpTo<Recipe> { inclusive = true }
                            }
                        },
                        isCooking = cookingAssistantState.isCooking
                    )
                }
                composable<CreateRecipeOne> {
                    CreateRecipeScreenOne(
                        context,
                        ownRecipesViewModel,
                        onNavigateToCreateRecipeTwo = {
                            navController.navigate(route = CreateRecipeTwo)
                        },
                        closeCreateRecipe = {
                            navController.popBackStack(route = CreateRecipeOne, inclusive = true)
                            ownRecipesViewModel.resetRecipe()
                        }
                    )
                }
                composable<CreateRecipeTwo> {
                    CreateRecipeScreenTwo(
                        ownRecipesViewModel,
                        onNavigateToCreateRecipeOne = {
                            navController.navigate(route = CreateRecipeOne) {
                                popUpTo(CreateRecipeOne) { inclusive = true }
                            }
                        },
                        onNavigateToCreateRecipeThree = {
                            navController.navigate(route = CreateRecipeThree)
                        },
                        closeCreateRecipe = {
                            navController.popBackStack(route = CreateRecipeOne, inclusive = true)
                            ownRecipesViewModel.resetRecipe()
                        }
                    )
                }
                composable<CreateRecipeThree> {
                    CreateRecipeScreenThree(
                        ownRecipesViewModel,
                        onNavigateToCreateRecipeTwo = {
                            navController.navigate(route = CreateRecipeTwo) {
                                popUpTo(CreateRecipeTwo) { inclusive = true }
                            }
                        },
                        onNavigateToCreateRecipeFour = {
                            navController.navigate(route = CreateRecipeFour)
                        },
                        closeCreateRecipe = {
                            navController.popBackStack(route = CreateRecipeOne, inclusive = true)
                            ownRecipesViewModel.resetRecipe()
                        }
                    )
                }
                composable<CreateRecipeFour> {
                    CreateRecipeScreenFour(
                        context,
                        ownRecipesViewModel,
                        onNavigateToProfile = {
                            navController.navigate(route = Profile) {
                                navController.popBackStack(route = CreateRecipeOne, inclusive = true)
                                popUpTo<Profile>()
                            }
                        },
                        onNavigateToCreateRecipeThree = {
                            navController.navigate(route = CreateRecipeThree) {
                                popUpTo(CreateRecipeThree) { inclusive = true }
                            }
                        },
                        closeCreateRecipe = {
                            navController.popBackStack(route = CreateRecipeOne, inclusive = true)
                            ownRecipesViewModel.resetRecipe()
                        }
                    )
                }
            }

//        if (isLoading.value && isNetworkAvailable) {
//            ProgressSpinner()
//        }
        }
        if (!NetworkStateProvider.isNetworkAvailable.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(40, 40, 43))
                    .padding(5.dp)
            ) {
                Text(
                    text = "No connection",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun DisplayCookingAssistantUi(
    context: Context,
    cookingAssistantViewModel: CookingAssistantViewModel,
    recipesViewModel: RecipesViewModel,
    sharedViewModel: SharedViewModel,
    onNavigateToRecipe: () -> Unit,
    bottomHeight: Dp = 0.dp,
    isCooking: Boolean
) {
    if (isCooking) {
        CookingAssistantUi (
            context,
            NetworkStateProvider.isNetworkAvailable.value,
            cookingAssistantViewModel,
            recipesViewModel,
            sharedViewModel,
            onNavigateToRecipe,
            bottomHeight
        )
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
object SelectCategories

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
