package com.samsantech.souschef.viewmodel

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import com.algolia.instantsearch.android.paging3.Paginator
import com.algolia.instantsearch.android.paging3.searchbox.connectPaginator
import com.algolia.instantsearch.compose.loading.LoadingState
import com.algolia.instantsearch.compose.searchbox.SearchBoxState
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.filter.state.FilterGroupID
import com.algolia.instantsearch.filter.state.FilterOperator
import com.algolia.instantsearch.filter.state.FilterState
import com.algolia.instantsearch.filter.state.add
import com.algolia.instantsearch.loading.LoadingConnector
import com.algolia.instantsearch.loading.connectView
import com.algolia.instantsearch.searchbox.SearchBoxConnector
import com.algolia.instantsearch.searchbox.SearchMode
import com.algolia.instantsearch.searchbox.connectView
import com.algolia.instantsearch.searcher.connectFilterState
import com.algolia.instantsearch.searcher.hits.HitsSearcher
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import com.algolia.search.model.filter.Filter
import com.algolia.search.model.insights.UserToken
import com.algolia.search.model.search.Query
import com.samsantech.souschef.data.SearchRecipe
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel: ViewModel() {
    val lazyState = MutableStateFlow(LazyGridState())

    private val appID = ApplicationID("JLQPKQBVUP")
    private val apiKey = APIKey("26ef1633753e107ebeecd0d69264f86e")
    private val indexName = IndexName("souschef-recipes")

    private var searcher = HitsSearcher(
        ClientSearch(appID, apiKey),
        indexName,
        query = Query(
            personalizationImpact = 70
        )
    )

    private val filterState = FilterState()
    private var searchBoxConnector = SearchBoxConnector(searcher, searchMode = SearchMode.OnSubmit, searchOnQueryUpdate = false)
    private val searchBoxState = SearchBoxState()
    var hitsPaginator = Paginator(searcher) {
        it.deserialize(SearchRecipe.serializer())
    }
    val loadingState = LoadingState()
    private val loadingConnector = LoadingConnector(searcher)
    private val connections = ConnectionHandler(
        searchBoxConnector,
        loadingConnector
    )

    init {
        connections += searcher.connectFilterState(filterState)
        connections += searchBoxConnector.connectView(searchBoxState)
        connections += searchBoxConnector.connectPaginator(hitsPaginator)
        connections += loadingConnector.connectView(loadingState)

        val audienceId = FilterGroupID("audienceId", FilterOperator.Or)
        filterState.notify {
            add(
                audienceId,
                setOf(
                    Filter.Facet(Attribute("audience"), "Public"),
                    Filter.Facet(Attribute("isTikTok"), true)
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelSearch()
        connections.disconnect()
    }

    private fun cancelSearch() {
        searcher.cancel()
        searchBoxState.setText("", true)
    }

    fun updateUserToken(userId: String?) {
        searcher.query.userToken = if (userId.isNullOrEmpty()) null else UserToken(userId)
        searchBoxState.setText("", true)
    }
}