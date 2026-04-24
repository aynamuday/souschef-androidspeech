package com.samsantech.souschef.viewmodel

import android.content.Context
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.registerInsights
import com.algolia.instantsearch.insights.sharedInsights
import com.samsantech.souschef.BuildConfig
import com.samsantech.souschef.data.SharedViewModelProvider
import com.samsantech.souschef.data.UserViewModelProvider

class AlgoliaInsightsViewModel(private val context: Context) {
    private val userViewModel: UserViewModel
        get() = UserViewModelProvider.userViewModel

    private val sharedViewModel: SharedViewModel
        get() = SharedViewModelProvider.sharedViewModel

    fun updateUserToken(userId: String?) {
        if (userId.isNullOrEmpty()) {
            sharedInsights = null
        } else {
            val configuration = Insights.Configuration(
                connectTimeoutInMilliseconds = 5000,
                readTimeoutInMilliseconds = 5000,
                defaultUserToken = userId
            )
            registerInsights(context, BuildConfig.ALGOLIA_APP_ID, BuildConfig.ALGOLIA_API_KEY, BuildConfig.ALGOLIA_INDEX_NAME, configuration).apply {
                loggingEnabled = true
            }
            sharedInsights(BuildConfig.ALGOLIA_INDEX_NAME).apply {
                minBatchSize = 1
                userToken = userId
            }
        }
    }

    fun sendViewedARecipeEvent(objectId: String) {
        sharedInsights?.viewedObjectIDs(
            eventName = "Viewed a recipe",
            objectIDs = listOf(objectId)
        )
        incrementSentEventsCount()
    }

    fun sendAddedToFavoritesEvent(objectId: String) {
        val queryId: String = sharedViewModel.searchQueryId.value ?: ""

        if (queryId.isNotEmpty()) {
            sharedInsights?.convertedObjectIDsAfterSearch(
                eventName = "Added recipe to favorites",
                objectIDs = listOf(objectId),
                timestamp = System.currentTimeMillis(),
                queryID = queryId
            )
        }
        incrementSentEventsCount()
    }

//    fun sendSharedARecipeEvent(objectId: String) {
//        println("event: shared a recipe")
//
//        sharedInsights?.convertedObjectIDs(
//            eventName = "Shared a recipe",
//            objectIDs = listOf(objectId)
//        )
//        incrementSentEventsCount()
//    }

    fun sendRatedARecipeEvent(objectId: String) {
        val queryId: String = sharedViewModel.searchQueryId.value ?: ""

        if (queryId.isNotEmpty()) {
            sharedInsights?.convertedObjectIDsAfterSearch(
                eventName = "Rated a favorites",
                objectIDs = listOf(objectId),
                timestamp = System.currentTimeMillis(),
                queryID = queryId
            )
        }
        incrementSentEventsCount()
    }

    private fun incrementSentEventsCount() {
        val user = userViewModel.user.value
        if (user != null && user.sentEventsCount < 30.0) {
            userViewModel.incrementSentEventsCount()
        }
    }
}