package com.nexatv.launcher.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexatv.launcher.app.AppContainer
import com.nexatv.launcher.domain.model.ContentItem
import com.nexatv.launcher.domain.model.ContinueWatchingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    data object NotFound : DetailsUiState
    data class Ready(
        val item: ContentItem,
        val progress: ContinueWatchingItem?,
        val inLibrary: Boolean,
    ) : DetailsUiState
}

class DetailsViewModel(
    private val container: AppContainer,
    private val contentId: String,
) : ViewModel() {

    private data class Loaded(val item: ContentItem?, val progress: ContinueWatchingItem?)

    private val loaded = MutableStateFlow<Loaded?>(null)

    init {
        viewModelScope.launch {
            val repository = container.contentRepository
            loaded.value = Loaded(repository.byId(contentId), repository.progressFor(contentId))
        }
    }

    val state: StateFlow<DetailsUiState> = combine(
        loaded,
        container.settingsRepository.settings.map { contentId in it.libraryIds },
    ) { loaded, inLibrary ->
        when {
            loaded == null -> DetailsUiState.Loading
            loaded.item == null -> DetailsUiState.NotFound
            else -> DetailsUiState.Ready(loaded.item, loaded.progress, inLibrary)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailsUiState.Loading)

    fun toggleLibrary() {
        viewModelScope.launch {
            val wasInLibrary = (state.value as? DetailsUiState.Ready)?.inLibrary == true
            container.settingsRepository.toggleLibrary(contentId)
            container.messages.post(if (wasInLibrary) "Removed from your Library" else "Added to your Library")
        }
    }
}
