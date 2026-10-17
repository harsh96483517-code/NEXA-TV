package com.nexatv.launcher.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexatv.launcher.app.AppContainer
import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.domain.model.ContentItem
import com.nexatv.launcher.domain.model.ContinueWatchingItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val contentLoading: Boolean = true,
    val appsLoading: Boolean = true,
    val error: String? = null,
    val featured: List<ContentItem> = emptyList(),
    val continueWatching: List<ContinueWatchingItem> = emptyList(),
    val trending: List<ContentItem> = emptyList(),
    val appRow: List<AppEntry> = emptyList(),
) {
    val loading: Boolean get() = contentLoading || appsLoading
}

/** Business logic for the Home screen; the composables only render [state]. */
class HomeViewModel(private val container: AppContainer) : ViewModel() {

    private val mutableState = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = mutableState.asStateFlow()

    private var contentJob: Job? = null

    init {
        loadContent()
        observeApps()
    }

    fun retry() = loadContent()

    private fun loadContent() {
        contentJob?.cancel()
        mutableState.update { it.copy(contentLoading = true, error = null) }
        contentJob = viewModelScope.launch {
            try {
                val repository = container.contentRepository
                val featured = repository.featured()
                val continueWatching = repository.continueWatching()
                val trending = repository.trending()
                mutableState.update {
                    it.copy(
                        contentLoading = false,
                        featured = featured,
                        continueWatching = continueWatching,
                        trending = trending,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutableState.update {
                    it.copy(contentLoading = false, error = e.message ?: "The content source did not respond.")
                }
            }
        }
    }

    private fun observeApps() {
        viewModelScope.launch {
            container.installedApps.collect { installed ->
                mutableState.update {
                    if (installed == null) {
                        it.copy(appsLoading = true)
                    } else {
                        it.copy(appsLoading = false, appRow = container.buildAppRow(installed))
                    }
                }
            }
        }
    }
}
