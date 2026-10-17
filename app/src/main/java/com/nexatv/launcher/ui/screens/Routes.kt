package com.nexatv.launcher.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
    val navigator = callbacks.navigator

    when (destination) {
        Destination.Home -> {
            val viewModel: HomeViewModel = viewModel(factory = factory)
            val state by viewModel.state.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                iconLoader = container.iconLoader,
                sidebarEntry = callbacks.sidebarEntry,
                onWatch = callbacks.onPlay,
                onInfo = { navigator.push(Destination.Details(it.id)) },
                onOpenContent = { navigator.push(Destination.Details(it)) },
                onAppClick = container.appActions::launch,
                onAppOptions = callbacks.onAppOptions,
                onSeeAllApps = { navigator.openRoot(Destination.Apps) },
                onBrowseMovies = { navigator.openRoot(Destination.Movies) },
                onRetry = viewModel::retry,
            )
        }

        Destination.Apps -> {
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

        Destination.Search -> {
            val viewModel: SearchViewModel = viewModel(factory = factory)
            val query by viewModel.query.collectAsStateWithLifecycle()
            val results by viewModel.results.collectAsStateWithLifecycle()
            SearchScreen(
                query = query,
                results = results,
                iconLoader = container.iconLoader,
                sidebarEntry = callbacks.sidebarEntry,
                onQueryChange = viewModel::onQueryChange,
                onAppClick = viewModel::launch,
                onAppOptions = callbacks.onAppOptions,
                onOpenContent = { navigator.push(Destination.Details(it)) },
            )
        }

        Destination.Movies -> CatalogRoute(CatalogMode.MOVIES, callbacks)
        Destination.TvShows -> CatalogRoute(CatalogMode.SHOWS, callbacks)
        Destination.Library -> CatalogRoute(CatalogMode.LIBRARY, callbacks)

        is Destination.Details -> {
            val detailsFactory = remember(destination.contentId) {
                NexaViewModelFactory(container, destination.contentId)
            }
            val viewModel: DetailsViewModel = viewModel(
                key = "details-${destination.contentId}",
                factory = detailsFactory,
            )
            val state by viewModel.state.collectAsStateWithLifecycle()
            DetailsScreen(
                state = state,
                sidebarEntry = callbacks.sidebarEntry,
                onPlay = callbacks.onPlay,
                onToggleLibrary = viewModel::toggleLibrary,
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

@Composable
private fun CatalogRoute(mode: CatalogMode, callbacks: ShellCallbacks) {
    val container = LocalAppContainer.current
    val catalogFactory = remember(mode) { NexaViewModelFactory(container, mode) }
    val viewModel: CatalogViewModel = viewModel(key = "catalog-${mode.name}", factory = catalogFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    CatalogScreen(
        mode = mode,
        state = state,
        sidebarEntry = callbacks.sidebarEntry,
        onOpenContent = { callbacks.navigator.push(Destination.Details(it)) },
        onBrowseMovies = { callbacks.navigator.openRoot(Destination.Movies) },
        modifier = Modifier,
    )
}
