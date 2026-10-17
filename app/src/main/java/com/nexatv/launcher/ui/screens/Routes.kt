package com.nexatv.launcher.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexatv.launcher.domain.model.UserSettings
import com.nexatv.launcher.domain.model.WallpaperChoice
import com.nexatv.launcher.navigation.Destination

/**
 * Connects each [Destination] to its ViewModel and screen composable. The screens themselves are
 * stateless: they render what they are given and report what the user did.
 */
@Composable
fun Routes(
    destination: Destination,
    factory: NexaViewModelFactory,
    settings: UserSettings,
    settingsViewModel: SettingsViewModel,
    callbacks: ShellCallbacks,
) {
    val container = LocalAppContainer.current

    when (destination) {
        Destination.Home -> {
            val viewModel: AppsViewModel = viewModel(factory = factory)
            val apps by viewModel.apps.collectAsStateWithLifecycle()
            AppsScreen(
                apps = apps,
                iconLoader = container.iconLoader,
                sidebarEntry = callbacks.sidebarEntry,
                onAppClick = viewModel::launch,
                onAppOptions = callbacks.onAppOptions,
            )
        }

        Destination.Wallpaper -> {
            val viewModel: WallpaperViewModel = viewModel(factory = factory)
            val state by viewModel.state.collectAsStateWithLifecycle()
            WallpaperScreen(
                state = state,
                sidebarEntry = callbacks.sidebarEntry,
                onSelect = { choice: WallpaperChoice -> viewModel.select(choice) },
                onPickFromDevice = callbacks.onPickWallpaper,
                onRefresh = viewModel::refresh,
            )
        }

        Destination.Theme -> ThemeScreen(
            settings = settings,
            sidebarEntry = callbacks.sidebarEntry,
            onAccent = settingsViewModel::setAccent,
            onSidebarCompact = settingsViewModel::setSidebarCompact,
            onReduceEffects = settingsViewModel::setReduceEffects,
            onCycleTimeFormat = settingsViewModel::cycleTimeFormat,
        )

        Destination.About -> {
            val isDefaultHome by settingsViewModel.isDefaultHome.collectAsStateWithLifecycle()
            AboutScreen(
                isDefaultHome = isDefaultHome,
                sidebarEntry = callbacks.sidebarEntry,
                onSetDefaultHome = callbacks.onSetDefaultHome,
                onOpenHomeSettings = settingsViewModel::openHomeSettings,
            )
        }
    }
}
