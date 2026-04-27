package com.samsantech.souschef.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.utils.CookingAssistantService
import kotlinx.coroutines.flow.MutableStateFlow

class SharedViewModel(
    private val algoliaInsightsViewModel: AlgoliaInsightsViewModel?,
    private val homeViewModel: HomeViewModel?,
    private val searchRecipesViewModel: SearchRecipesViewModel?
) {
    val searchQueryId = MutableStateFlow(null as String?)

    fun setSearchQueryId(queryId: String?) {
        searchQueryId.value = queryId
    }

    fun updateAlgoliaQueriesUserToken(userId: String?) {
        algoliaInsightsViewModel?.updateUserToken(userId)
        homeViewModel?.updateUserToken(userId)
        searchRecipesViewModel?.updateUserToken(userId)
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(intent)
    }

    fun startCookingAssistantService(context: Context, recipe: Recipe) {
        val cookingAssistantServiceIntent = Intent(context, CookingAssistantService::class.java).apply {
            action = CookingAssistantService.Actions.START.toString()
            putExtra("recipe", recipe)
        }

        ContextCompat.startForegroundService(context, cookingAssistantServiceIntent)
    }

    fun stopCookingAssistantService(context: Context) {
        val cookingAssistantServiceIntent = Intent(context, CookingAssistantService::class.java).apply {
            action = CookingAssistantService.Actions.STOP.toString()
        }

        ContextCompat.startForegroundService(context, cookingAssistantServiceIntent)
    }
}