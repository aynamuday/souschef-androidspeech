package com.samsantech.souschef.viewmodel

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
import com.algolia.search.model.response.ResponseSearch
import com.algolia.search.model.search.Query
import com.samsantech.souschef.data.SearchRecipe

class HomeViewModel: ViewModel() {
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

    private lateinit var filterState: FilterState
    private lateinit var searchBoxConnector: SearchBoxConnector<ResponseSearch>
    private var searchBoxState = SearchBoxState()
    lateinit var hitsPaginator: Paginator<SearchRecipe>
    var loadingState = LoadingState()
    private var loadingConnector = LoadingConnector(searcher)
    private lateinit var connections: ConnectionHandler

    init {
        updateUserToken(null)
    }

    fun updateUserToken(userId: String?) {
        searcher.query.userToken = if (userId.isNullOrEmpty()) null else UserToken(userId)
        if (userId != null) {
            searcher.query.optionalFilters = listOf(listOf("seenBy:-$userId"))
        }

        filterState = FilterState()
        searchBoxConnector = SearchBoxConnector(searcher, searchMode = SearchMode.OnSubmit, searchOnQueryUpdate = false)
        hitsPaginator = Paginator(searcher) {
            it.deserialize(SearchRecipe.serializer())
        }
        loadingConnector = LoadingConnector(searcher)
        connections = ConnectionHandler(
            searchBoxConnector,
            loadingConnector
        )

        connections += searcher.connectFilterState(filterState)
        connections += searchBoxConnector.connectView(searchBoxState)
        connections += searchBoxConnector.connectPaginator(hitsPaginator)
        connections += loadingConnector.connectView(loadingState)

        val audienceIdFilter = mutableSetOf(Filter.Facet(Attribute("audience"), "Public"))
        if (userId != null) {
            audienceIdFilter.add(Filter.Facet(Attribute("userId"), userId, isNegated = true))
        }
        filterState.notify {
            add(
                FilterGroupID("audienceId", FilterOperator.And),
                audienceIdFilter
            )
        }

        searchBoxState.setText("", true)
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
}