package com.nexatv.launcher.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nexatv.launcher.app.AppContainer

/** Creates ViewModels from the hand-rolled [AppContainer]. */
class NexaViewModelFactory(
    private val container: AppContainer,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = when {
            modelClass.isAssignableFrom(AppsViewModel::class.java) -> AppsViewModel(container)
            modelClass.isAssignableFrom(WallpaperViewModel::class.java) -> WallpaperViewModel(container)
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(container)
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
        return viewModel as T
    }
}
