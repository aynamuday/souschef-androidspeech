package com.samsantech.souschef.viewmodel

import androidx.lifecycle.ViewModel
import com.algolia.client.model.search.OptionalFilters
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
import com.algolia.instantsearch.searcher.updateSearchParamsObject
import com.algolia.search.model.search.Query
import com.samsantech.souschef.BuildConfig
import com.samsantech.souschef.data.SearchRecipe
import com.samsantech.souschef.data.SharedViewModelProvider

class HomeViewModel: ViewModel() {
    private val sharedViewModel: SharedViewModel
        get() = SharedViewModelProvider.sharedViewModel

    val optionalFilters = mutableListOf<OptionalFilters>()
    private var searcher = HitsSearcher(
        BuildConfig.ALGOLIA_APP_ID, BuildConfig.ALGOLIA_API_KEY, BuildConfig.ALGOLIA_INDEX_NAME,
        query = Query(
            personalizationImpact = 70,
            optionalFilters = OptionalFilters.of(optionalFilters),
            clickAnalytics = true
        )
    )

    private lateinit var filterState: FilterState
    private var searchBoxState = SearchBoxState()
    lateinit var hitsPaginator: Paginator<SearchRecipe>
    var loadingState = LoadingState()
    private var loadingConnector = LoadingConnector(searcher)
    private lateinit var connections: ConnectionHandler

//    init {
//        updateUserToken(null)
//    }

    fun updateUserToken(userId: String? = null, categories: List<String>? = null) {
        searcher.updateSearchParamsObject {
            it.copy(
                userToken = if (userId.isNullOrEmpty()) "" else userId
            )
        }

        if (userId != null) {
            optionalFilters.add(OptionalFilters.of("seenBy:-$userId"))

            // uses user preferences as optional filters, ONLY IF there's not enough events sent
            if (!categories.isNullOrEmpty()) {
                categories.forEach {
                    optionalFilters.add(OptionalFilters.of("categories:$it"))
                }
            }
        }



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

        // search response
        searcher.response.subscribe { response ->
            sharedViewModel.setSearchQueryId(response?.queryID)
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
}