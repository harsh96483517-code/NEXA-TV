package com.nexatv.launcher.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexatv.launcher.app.AppContainer
import com.nexatv.launcher.domain.model.ContentItem
import com.nexatv.launcher.domain.model.ContentType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class CatalogMode(val title: String) {
    MOVIES("Movies"),
    SHOWS("TV Shows"),
    LIBRARY("Library"),
}

/** `items == null` means loading. */
data class CatalogUiState(
    val items: List<ContentItem>? = null,
)

/** Movies, TV Shows and the user's Library (items they saved from the details screen). */
class CatalogViewModel(
    container: AppContainer,
    private val mode: CatalogMode,
) : ViewModel() {

    private val catalog = flow {
        emit(
            when (mode) {
                CatalogMode.MOVIES -> container.contentRepository.catalog(ContentType.MOVIE)
                CatalogMode.SHOWS -> container.contentRepository.catalog(ContentType.SHOW)
                CatalogMode.LIBRARY -> container.contentRepository.catalog(null)
            },
        )
    }

    val state: StateFlow<CatalogUiState> = combine(
        catalog,
        container.settingsRepository.settings.map { it.libraryIds },
    ) { items, libraryIds ->
        CatalogUiState(
            items = if (mode == CatalogMode.LIBRARY) items.filter { it.id in libraryIds } else items,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogUiState())
}
