package com.samsantech.souschef

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.storage
import com.samsantech.souschef.data.CookingAssistantViewModelProvider
import com.samsantech.souschef.data.NetworkStateProvider
import com.samsantech.souschef.data.OwnRecipesViewModelProvider
import com.samsantech.souschef.data.SharedViewModelProvider
import com.samsantech.souschef.data.UserViewModelProvider
import com.samsantech.souschef.firebase.FirebaseAuthManager
import com.samsantech.souschef.firebase.FirebaseRecipeManager
import com.samsantech.souschef.firebase.FirebaseUserManager
import com.samsantech.souschef.ui.theme.SousChefTheme
import com.samsantech.souschef.utils.BluetoothHelper
import com.samsantech.souschef.utils.NetworkHelper
import com.samsantech.souschef.utils.TextToSpeechManager
import com.samsantech.souschef.viewmodel.AlgoliaInsightsViewModel
import com.samsantech.souschef.viewmodel.AuthViewModel
import com.samsantech.souschef.viewmodel.CookingAssistantViewModel
import com.samsantech.souschef.viewmodel.HomeViewModel
import com.samsantech.souschef.viewmodel.SharedViewModel
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel
import com.samsantech.souschef.viewmodel.RecipesViewModel
import com.samsantech.souschef.viewmodel.SearchRecipesViewModel
import com.samsantech.souschef.viewmodel.UserViewModel

class MainActivity : ComponentActivity(), NetworkHelper.NetworkChangeListener {
    private val networkHelper = NetworkHelper()
    private lateinit var networkChangeReceiver: BroadcastReceiver
    private var isNetworkAvailable = mutableStateOf(false)

    private lateinit var bluetoothHelper: BluetoothHelper
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothReceiver: BroadcastReceiver
    private var isBluetoothManaged = false

    private var textToSpeechManager : TextToSpeechManager? = null
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var cookingAssistantViewModel: CookingAssistantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SousChefTheme {
                textToSpeechManager = TextToSpeechManager(this.applicationContext)

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
                val functions = FirebaseFunctions.getInstance()

                val firebaseUserManager = FirebaseUserManager(auth, db, storage)
                val firebaseAuthManager = FirebaseAuthManager(auth, db, firebaseUserManager, functions)
                val firebaseRecipeManager = FirebaseRecipeManager(auth, db, storage)

                val algoliaInsightsViewModel = AlgoliaInsightsViewModel(this.applicationContext)
                val homeViewModel = viewModel<HomeViewModel>()
                val searchRecipesViewModel = viewModel<SearchRecipesViewModel>()
                sharedViewModel = SharedViewModel(algoliaInsightsViewModel, homeViewModel, searchRecipesViewModel)
                val authViewModel = AuthViewModel(firebaseAuthManager)
                val userViewModel = UserViewModel(firebaseAuthManager, firebaseUserManager, sharedViewModel, homeViewModel)
                val recipesViewModel = RecipesViewModel(firebaseRecipeManager)
                val ownRecipesViewModel = OwnRecipesViewModel(userViewModel, firebaseRecipeManager, recipesViewModel)
                cookingAssistantViewModel = CookingAssistantViewModel(context = this.applicationContext, textToSpeechManager = textToSpeechManager!!)
                CookingAssistantViewModelProvider.cookingAssistantViewModel = cookingAssistantViewModel
                OwnRecipesViewModelProvider.ownRecipesViewModel = ownRecipesViewModel
                UserViewModelProvider.userViewModel = userViewModel
                SharedViewModelProvider.sharedViewModel = sharedViewModel

                val user = auth.currentUser
                sharedViewModel.updateAlgoliaQueriesUserToken(user?.uid)

                SousChefApp(
                    systemNavigationBarHeight,
                    user,
                    activity = this,
                    context = this,
                    sharedViewModel,
                    authViewModel,
                    userViewModel,
                    ownRecipesViewModel,
                    recipesViewModel,
                    searchRecipesViewModel,
                    cookingAssistantViewModel,
                    homeViewModel,
                    algoliaInsightsViewModel
                )
            }
        }

        networkChangeReceiver = networkHelper.networkChangeReceiver(this)
        registerReceiver(networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        NetworkStateProvider.isNetworkAvailable = isNetworkAvailable

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
        {
                manageBluetooth()
        }
    }

    override fun onRestart() {
        super.onRestart()

        if (!isBluetoothManaged && Build.VERSION.SDK_INT < Build.VERSION_CODES.S || !isBluetoothManaged && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            == PackageManager.PERMISSION_GRANTED) {
            manageBluetooth()
        }
    }

//    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onDestroy() {
        super.onDestroy()
        textToSpeechManager?.destroy()
        if (cookingAssistantViewModel.cookingAssistantState.value.isCooking) {
            sharedViewModel.stopCookingAssistantService(this)
        }

        unregisterReceiver(networkChangeReceiver)

        bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, null)
        unregisterReceiver(bluetoothReceiver)
    }

    override fun onNetworkChanged(isNetworkAvailable: Boolean) {
        this.isNetworkAvailable.value = isNetworkAvailable
    }

    private fun manageBluetooth() {
        isBluetoothManaged = true

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        bluetoothHelper = BluetoothHelper(audioManager)

        val bluetoothProfileListener = bluetoothHelper.bluetoothProfileListener
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothAdapter.getProfileProxy(this, bluetoothProfileListener, BluetoothProfile.HEADSET)

        bluetoothReceiver = bluetoothHelper.bluetoothReceiver()
        registerReceiver(bluetoothReceiver, IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED))
    }
}