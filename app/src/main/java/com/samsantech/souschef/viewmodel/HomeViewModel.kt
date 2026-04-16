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
import com.algolia.search.helper.deserialize
import com.algolia.instantsearch.filter.Filter
import com.algolia.search.model.search.Query
import com.samsantech.souschef.BuildConfig
import com.samsantech.souschef.data.SearchRecipe

class HomeViewModel: ViewModel() {
    private var searcher = HitsSearcher(
        BuildConfig.ALGOLIA_APP_ID, BuildConfig.ALGOLIA_API_KEY, BuildConfig.ALGOLIA_INDEX_NAME,
        query = Query(
            personalizationImpact = 70
        )
    )

    private lateinit var filterState: FilterState
    private var searchBoxState = SearchBoxState()
    lateinit var hitsPaginator: Paginator<SearchRecipe>
    var loadingState = LoadingState()
    private var loadingConnector = LoadingConnector(searcher)
    private lateinit var connections: ConnectionHandler

    init {
        updateUserToken(null)
    }

    fun updateUserToken(userId: String?, categories: List<String>? = null) {
        searcher.userToken = if (userId.isNullOrEmpty()) "" else userId
//        val optionalFilters = mutableListOf<List<String>>()
        if (userId != null) {
//            optionalFilters.add(listOf("seenBy:-$userId"))

            if (!categories.isNullOrEmpty()) {
                val categoriesFilter = mutableListOf<String>()
                categories.forEach {
                    categoriesFilter.add("categories:$it")
                }
//                optionalFilters.add(categoriesFilter)
            }
        }
//        if (optionalFilters.isNotEmpty()) {
//            searcher.query.optionalFilters = optionalFilters
//        }

        filterState = FilterState()
        val searchBoxConnector = SearchBoxConnector(searcher, searchMode = SearchMode.OnSubmit, searchOnQueryUpdate = false)
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

        val audienceIdFilter = mutableSetOf(Filter.Facet("audience", "Public"))
        if (userId != null) {
            audienceIdFilter.add(Filter.Facet("userId", userId, isNegated = true))
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