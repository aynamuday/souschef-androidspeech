package com.samsantech.souschef.viewmodel

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import com.algolia.instantsearch.android.paging3.Paginator
import com.algolia.instantsearch.android.paging3.searchbox.connectPaginator
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.filter.state.FilterGroupID
import com.algolia.instantsearch.filter.state.FilterOperator
import com.algolia.instantsearch.filter.state.FilterState
import com.algolia.instantsearch.filter.state.add
import com.algolia.instantsearch.filter.state.remove
import com.algolia.instantsearch.loading.LoadingConnector
import com.algolia.instantsearch.loading.connectView
import com.algolia.instantsearch.searchbox.SearchBoxConnector
import com.algolia.instantsearch.searchbox.SearchMode
import com.algolia.instantsearch.searchbox.connectView
import com.algolia.instantsearch.searcher.connectFilterState
import com.algolia.instantsearch.searcher.hits.HitsSearcher
import com.algolia.instantsearch.searcher.hits.SearchForQuery
import com.algolia.instantsearch.filter.Filter
import com.algolia.instantsearch.compose.loading.LoadingState
import com.algolia.instantsearch.compose.searchbox.SearchBoxState
import com.algolia.instantsearch.searcher.updateSearchParamsObject
import com.algolia.search.helper.deserialize
import com.algolia.search.model.search.Query
import com.samsantech.souschef.BuildConfig
import com.samsantech.souschef.data.SearchRecipe
import com.samsantech.souschef.data.SharedViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow

class SearchRecipesViewModel: ViewModel() {
    private val sharedViewModel: SharedViewModel
        get() = SharedViewModelProvider.sharedViewModel
    val search = MutableStateFlow("")
    private var category: String = ""
    private val categoriesId = FilterGroupID("categoriesId", FilterOperator.Or)
    val hasSearched = MutableStateFlow(false)
    val gridState = MutableStateFlow(LazyGridState())

    private var searcher = HitsSearcher(
        BuildConfig.ALGOLIA_APP_ID, BuildConfig.ALGOLIA_API_KEY, BuildConfig.ALGOLIA_INDEX_NAME,
        triggerSearchFor = SearchForQuery.lengthAtLeast(1),
        isDisjunctiveFacetingEnabled = false,
        query = Query(
            personalizationImpact = 70,
            clickAnalytics = true
        )
    )

    private lateinit var filterState: FilterState
    private var searchBoxConnector = SearchBoxConnector(searcher, searchMode = SearchMode.OnSubmit, searchOnQueryUpdate = false)
    private var searchBoxState = SearchBoxState()
    lateinit var hitsPaginator: Paginator<SearchRecipe>
    var loadingState = LoadingState();

    private var loadingConnector = LoadingConnector(searcher)
    private lateinit var connections: ConnectionHandler

    fun updateUserToken(userId: String?) {
//        searcher.userToken = if (userId.isNullOrEmpty()) "" else userId
        searcher.updateSearchParamsObject {
            it.copy(
                userToken = if (userId.isNullOrEmpty()) "" else userId
            )
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

        val audienceIdFilter = mutableSetOf(Filter.Facet("audience", "Public"))
        if (userId != null) {
            audienceIdFilter.add(Filter.Facet("userId", userId))
        }
        filterState.notify {
            add(
                FilterGroupID("audienceId", FilterOperator.Or),
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

    fun cancelSearch() {
        searcher.cancel()
        searchBoxState.setText("", true)
    }

    fun search(text: String) {
        searchBoxState.setText(text, true)
    }

    fun searchCategory(category: String) {
        clearCategories()
        this.category = category

        filterState.notify {
            add(
                categoriesId,
                setOf(
                    Filter.Facet("categories", category)
                )
            )
        }

        searchBoxState.setText(" ", true)
        loadingState.setIsLoading(true)
    }

    fun clearCategories() {
        filterState.notify {
            remove(
                categoriesId,
                setOf(
                    Filter.Facet("categories", category)
                )
            )
        }

        category = ""
    }
}
