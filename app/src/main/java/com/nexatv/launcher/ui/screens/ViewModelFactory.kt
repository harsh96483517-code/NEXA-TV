package com.nexatv.launcher.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nexatv.launcher.app.AppContainer

/** Creates ViewModels from the hand-rolled [AppContainer]; [argument] feeds parameterised ones. */
class NexaViewModelFactory(
    private val container: AppContainer,
    private val argument: Any? = null,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(container)
            modelClass.isAssignableFrom(AppsViewModel::class.java) -> AppsViewModel(container)
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(container)
            modelClass.isAssignableFrom(WallpaperViewModel::class.java) -> WallpaperViewModel(container)
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(container)
            modelClass.isAssignableFrom(CatalogViewModel::class.java) ->
                CatalogViewModel(container, argument as CatalogMode)
            modelClass.isAssignableFrom(DetailsViewModel::class.java) ->
                DetailsViewModel(container, argument as String)
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
        return viewModel as T
    }
}
