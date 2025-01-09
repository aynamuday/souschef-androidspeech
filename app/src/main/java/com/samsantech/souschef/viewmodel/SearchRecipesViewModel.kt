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
import com.algolia.instantsearch.filter.state.remove
import com.algolia.instantsearch.loading.LoadingConnector
import com.algolia.instantsearch.loading.connectView
import com.algolia.instantsearch.searchbox.SearchBoxConnector
import com.algolia.instantsearch.searchbox.connectView
import com.algolia.instantsearch.searcher.connectFilterState
import com.algolia.instantsearch.searcher.hits.HitsSearcher
import com.algolia.instantsearch.searcher.hits.SearchForQuery
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import com.algolia.search.model.filter.Filter
import com.samsantech.souschef.data.SearchRecipe

class SearchRecipesViewModel: ViewModel() {
    private var category: String = ""
    private val categoriesId = FilterGroupID("categoriesId", FilterOperator.Or)

    private var searcher = HitsSearcher(
        applicationID = ApplicationID("VP98Z77775"),
        apiKey = APIKey("f5fad5c803ab4df0ea8c02f45496c71c"),
        indexName = IndexName("souschef-samsantech"),
        triggerSearchFor = SearchForQuery.lengthAtLeast(1),
        isDisjunctiveFacetingEnabled = false,
    )
    private val filterState = FilterState()

    private var searchBoxConnector = SearchBoxConnector(searcher)
    val searchBoxState = SearchBoxState()
    var hitsPaginator = Paginator(searcher) {
        if (category != "") {
            filterState.notify {
                remove(
                    categoriesId,
                    setOf(
                        Filter.Facet(Attribute("categories"), category)
                    )
                )
            }

            category = ""
        }

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
                    Filter.Facet(Attribute("audience"), "Public")
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelSearch()
    }

    fun cancelSearch() {
        searcher.cancel()
    }

    fun searchCategory(category: String) {
        this.category = category

        filterState.notify {
            add(
                categoriesId,
                setOf(
                    Filter.Facet(Attribute("categories"), category)
                )
            )
        }

        searchBoxState.setText(" ")
        loadingState.setIsLoading(true)
    }
}
