package com.samsantech.souschef.viewmodel

import android.content.Context
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.registerInsights
import com.algolia.instantsearch.insights.sharedInsights
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.ObjectID
import com.algolia.search.model.QueryID
import com.algolia.search.model.insights.EventName
import com.algolia.search.model.insights.UserToken

class AlgoliaInsightsViewModel(context: Context) {
    private val appID = ApplicationID("VP98Z77775")
    private val apiKey = APIKey("f5fad5c803ab4df0ea8c02f45496c71c")
    private val indexName = IndexName("souschef-samsantech")

    private val configuration = Insights.Configuration(
        connectTimeoutInMilliseconds = 5000,
        readTimeoutInMilliseconds = 5000,
//        defaultUserToken = UserToken("5ba5si7dd0ZANIl4iXZs8eJsM4q2")
    )

    init {
        registerInsights(context, appID, apiKey, indexName, configuration)
        sharedInsights(indexName).apply {
            minBatchSize = 1
        }
    }

    fun updateUserToken(userId: String?) {
        sharedInsights(indexName).apply {
            minBatchSize = 1
            userToken = if (userId.isNullOrEmpty()) null else UserToken(userId)
        }
    }

    fun sendViewedARecipeEvent(objectId: String) {
        sharedInsights?.viewedObjectIDs(
            eventName = EventName("Viewed a recipe"),
            objectIDs = listOf(ObjectID(objectId))
        )
    }

    fun sendViewedARecipeAfterSearchEvent(objectId: String, queryID: String, position: Int) {
        sharedInsights?.clickedObjectIDsAfterSearch(
            eventName = EventName("Viewed a recipe"),
            queryID = QueryID(queryID),
            objectIDs = listOf(ObjectID(objectId)),
            positions = listOf(position)
        )
    }

    fun sendAddedToFavoritesEvent(objectId: String) {
        sharedInsights?.convertedObjectIDs(
            eventName = EventName("Added recipe to favorites"),
            objectIDs = listOf(ObjectID(objectId))
        )
    }

    fun sendAddedToFavoritesAfterSearchEvent(objectId: String, queryID: String) {
        sharedInsights?.convertedObjectIDsAfterSearch(
            eventName = EventName("Added recipe to favorites"),
            queryID = QueryID(queryID),
            objectIDs = listOf(ObjectID(objectId))
        )
    }

    fun sendSharedARecipeEvent(objectId: String) {
        sharedInsights?.convertedObjectIDs(
            eventName = EventName("Shared a recipe"),
            objectIDs = listOf(ObjectID(objectId))
        )
    }
}