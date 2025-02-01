package com.samsantech.souschef.viewmodel

import android.content.Context
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.registerInsights
import com.algolia.instantsearch.insights.sharedInsights
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.ObjectID
import com.algolia.search.model.insights.EventName
import com.algolia.search.model.insights.UserToken
import com.samsantech.souschef.data.UserViewModelProvider

class AlgoliaInsightsViewModel(private val context: Context) {
    private val userViewModel: UserViewModel
        get() = UserViewModelProvider.userViewModel
    private val appID = ApplicationID("JLQPKQBVUP")
    private val apiKey = APIKey("26ef1633753e107ebeecd0d69264f86e")
    private val indexName = IndexName("souschef-recipes")

    fun updateUserToken(userId: String?) {
        if (userId.isNullOrEmpty()) {
            sharedInsights = null
        } else {
            val configuration = Insights.Configuration(
                connectTimeoutInMilliseconds = 5000,
                readTimeoutInMilliseconds = 5000,
                defaultUserToken = UserToken(userId)
            )
            registerInsights(context, appID, apiKey, indexName, configuration)
            sharedInsights(indexName).apply {
                minBatchSize = 1
                userToken = UserToken(userId)
            }
        }
    }

    fun sendViewedARecipeEvent(objectId: String) {
        sharedInsights?.viewedObjectIDs(
            eventName = EventName("Viewed a recipe"),
            objectIDs = listOf(ObjectID(objectId))
        )
        incrementSentEventsCount()
    }

    fun sendAddedToFavoritesEvent(objectId: String) {
        sharedInsights?.convertedObjectIDs(
            eventName = EventName("Added recipe to favorites"),
            objectIDs = listOf(ObjectID(objectId))
        )
        incrementSentEventsCount()
    }

    fun sendSharedARecipeEvent(objectId: String) {
        sharedInsights?.convertedObjectIDs(
            eventName = EventName("Shared a recipe"),
            objectIDs = listOf(ObjectID(objectId))
        )
        incrementSentEventsCount()
    }

    fun sendRatedARecipeEvent(objectId: String) {
        sharedInsights?.convertedObjectIDs(
            eventName = EventName("Rated a recipe"),
            objectIDs = listOf(ObjectID(objectId))
        )
        incrementSentEventsCount()
    }

    private fun incrementSentEventsCount() {
        val user = userViewModel.user.value
        if (user != null && user.sentEventsCount < 30.0) {
            userViewModel.incrementSentEventsCount()
        }
    }
}