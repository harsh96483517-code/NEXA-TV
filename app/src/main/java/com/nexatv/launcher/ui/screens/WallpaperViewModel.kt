package com.nexatv.launcher.ui.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexatv.launcher.app.AppContainer
import com.nexatv.launcher.domain.model.WallpaperCatalog
import com.nexatv.launcher.domain.model.WallpaperChoice
import com.nexatv.launcher.domain.model.WallpaperPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ImportedWallpaper(val name: String, val uri: String)

data class WallpaperUiState(
    val current: WallpaperChoice = WallpaperChoice.Default,
    val presets: List<WallpaperPreset> = WallpaperCatalog.presets,
    val imported: List<ImportedWallpaper> = emptyList(),
    val folderHint: String = "",
)

/**
 * Wallpaper options: the built-in procedural presets, images the user dropped into Harsh Reality's own
 * storage folder, and (through the activity's document picker) any image the user chooses.
 */
class WallpaperViewModel(private val container: AppContainer) : ViewModel() {

    private val imported = MutableStateFlow<List<ImportedWallpaper>>(emptyList())
    private val folderHint = MutableStateFlow("")

    val state: StateFlow<WallpaperUiState> = combine(
        container.settingsRepository.settings.map { it.wallpaper },
        imported,
        folderHint,
    ) { current, files, hint ->
        WallpaperUiState(current = current, imported = files, folderHint = hint)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WallpaperUiState())

    init {
        refresh()
    }

    /** Re-scans the app folder (disk access happens off the main thread). */
    fun refresh() {
        viewModelScope.launch {
            val (files, hint) = withContext(Dispatchers.IO) {
                val found = container.wallpaperSource.listImported()
                    .map { ImportedWallpaper(it.name, Uri.fromFile(it).toString()) }
                found to container.wallpaperSource.folderHint()
            }
            imported.value = files
            folderHint.value = hint
        }
    }

    fun select(choice: WallpaperChoice) {
        viewModelScope.launch {
            container.settingsRepository.setWallpaper(choice)
            container.messages.post("Wallpaper updated")
        }
    }
}
