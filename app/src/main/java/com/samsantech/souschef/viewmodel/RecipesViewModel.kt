package com.samsantech.souschef.viewmodel

import androidx.lifecycle.ViewModel
import com.algolia.instantsearch.android.paging3.Paginator
import com.algolia.instantsearch.android.paging3.searchbox.connectPaginator
import com.algolia.instantsearch.compose.loading.LoadingState
import com.algolia.instantsearch.compose.searchbox.SearchBoxState
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.loading.LoadingConnector
import com.algolia.instantsearch.loading.connectView
import com.algolia.instantsearch.searchbox.SearchBoxConnector
import com.algolia.instantsearch.searchbox.connectView
import com.algolia.instantsearch.searcher.hits.HitsSearcher
import com.algolia.instantsearch.searcher.hits.SearchForQuery
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.Query
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.data.SearchRecipe
import kotlinx.coroutines.flow.MutableStateFlow

class RecipesViewModel: ViewModel() {
    val displayRecipe = MutableStateFlow(Recipe())

    private val query: Query = Query(
        query = "ayna",
        filters = "audience:\"Public\""
    )

    private val searcher = HitsSearcher(
        applicationID = ApplicationID("VP98Z77775"),
        apiKey = APIKey("f5fad5c803ab4df0ea8c02f45496c71c"),
        indexName = IndexName("souschef-samsantech"),
        triggerSearchFor = SearchForQuery.lengthAtLeast(1),
        query = query,
        isDisjunctiveFacetingEnabled = false
    )

    private val searchBoxConnector = SearchBoxConnector(searcher)
    val searchBoxState = SearchBoxState()
    val hitsPaginator = Paginator(searcher) {
        println(it)
        it.deserialize(SearchRecipe.serializer())
    }
    val loadingState = LoadingState()
    private val loadingConnector = LoadingConnector(searcher)

//    private val filterState = FilterState()
//    private val audience = Attribute("audience")
//    private val filterListConnector = FilterListConnector.Facet(
//        filters = listOf(Filter.Facet(audience, "Only me")),
//        filterState = filterState,
//        selectionMode = SelectionMode.Single
//    )
//    val filterListState = FilterListState<Filter.Facet>()

    private val connections = ConnectionHandler(searchBoxConnector, loadingConnector)
    // , searcher.connectFilterState(filterState)
    init {
        connections += searchBoxConnector.connectView(searchBoxState)
        connections += searchBoxConnector.connectPaginator(hitsPaginator)
        connections += loadingConnector.connectView(loadingState)
//        connections += filterListConnector.connectView(filterListState)
    }

    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
    }
}