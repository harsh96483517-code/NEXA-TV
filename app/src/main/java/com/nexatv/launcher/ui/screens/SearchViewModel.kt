package com.nexatv.launcher.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexatv.launcher.app.AppContainer
import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.domain.model.ContentItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchResults(
    val query: String = "",
    val apps: List<AppEntry> = emptyList(),
    val content: List<ContentItem> = emptyList(),
) {
    val isBlank: Boolean get() = query.isBlank()
    val isEmpty: Boolean get() = apps.isEmpty() && content.isEmpty()
}

/** Searches installed apps and the (sample) content catalog as the user types. */
class SearchViewModel(private val container: AppContainer) : ViewModel() {

    private val mutableQuery = MutableStateFlow("")
    val query: StateFlow<String> = mutableQuery.asStateFlow()

    private val catalog = MutableStateFlow<List<ContentItem>>(emptyList())

    init {
        viewModelScope.launch { catalog.value = container.contentRepository.catalog(null) }
    }

    val results: StateFlow<SearchResults> = combine(
        mutableQuery,
        container.installedApps,
        catalog,
    ) { query, installed, items ->
        SearchResults(
            query = query,
            apps = container.searchApps(
                apps = installed.orEmpty().filter { it.packageName != container.ownPackage },
                query = query,
            ),
            content = container.searchContent(items, query),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchResults())

    fun onQueryChange(value: String) {
        mutableQuery.value = value
    }

    fun launch(app: AppEntry) = container.appActions.launch(app)
}
