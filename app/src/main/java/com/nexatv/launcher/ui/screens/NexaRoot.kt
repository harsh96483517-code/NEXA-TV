package com.nexatv.launcher.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexatv.launcher.app.AppContainer
import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.domain.model.UserSettings
import com.nexatv.launcher.navigation.Destination
import com.nexatv.launcher.navigation.NexaNavigator
import com.nexatv.launcher.platform.NetworkStatus
import com.nexatv.launcher.ui.components.AppOptionsDialog
import com.nexatv.launcher.ui.components.LocalMessageBus
import com.nexatv.launcher.ui.components.QuickSettingsPanel
import com.nexatv.launcher.ui.components.SidebarDimens
import com.nexatv.launcher.ui.components.SidebarItem
import com.nexatv.launcher.ui.components.TVSidebar
import com.nexatv.launcher.ui.components.ToastHost
import com.nexatv.launcher.ui.components.TopNavBar
import com.nexatv.launcher.ui.components.WallpaperBackground
import com.nexatv.launcher.ui.theme.LocalReduceEffects
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaTheme

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer was not provided")
}

/**
 * Everything the screens need from the shell. Screens never touch the navigator or the activity
 * directly; they call these.
 */
class ShellCallbacks(
    val navigator: NexaNavigator,
    /** Attached to the selected sidebar entry; "Left" from content jumps here. */
    val sidebarEntry: FocusRequester,
    val onAppOptions: (AppEntry) -> Unit,
    val onPickWallpaper: () -> Unit,
    val onSetDefaultHome: () -> Unit,
)

/** Entry point of the UI: loads settings, applies the theme and hosts the shell. */
@Composable
fun NexaRoot(
    container: AppContainer,
    onRequestDefaultHome: () -> Unit,
    onPickWallpaper: () -> Unit,
) {
    val factory = remember(container) { NexaViewModelFactory(container) }
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()

    // Wait for the first DataStore read so the UI never flashes default colors or wallpaper.
    val loaded = settings
    if (loaded == null) {
        Box(Modifier.fillMaxSize().background(NexaPalette.Background))
        return
    }

    NexaTheme(accent = loaded.accent, reduceEffects = loaded.reduceEffects) {
        CompositionLocalProvider(
            LocalAppContainer provides container,
            LocalMessageBus provides container.messages,
        ) {
            NexaShell(
                container = container,
                factory = factory,
                settings = loaded,
                settingsViewModel = settingsViewModel,
                onRequestDefaultHome = onRequestDefaultHome,
                onPickWallpaper = onPickWallpaper,
            )
        }
    }
}

@Composable
private fun NexaShell(
    container: AppContainer,
    factory: NexaViewModelFactory,
    settings: UserSettings,
    settingsViewModel: SettingsViewModel,
    onRequestDefaultHome: () -> Unit,
    onPickWallpaper: () -> Unit,
) {
    val navigator = remember { NexaNavigator() }
    val sidebarEntry = remember { FocusRequester() }
    var sidebarFocused by remember { mutableStateOf(false) }
    var optionsApp by remember { mutableStateOf<AppEntry?>(null) }
    val reduce = LocalReduceEffects.current

    // The system Home button, pressed while we are already in front, means "go to the Home screen".
    LaunchedEffect(container) {
        container.homePressed.collect { navigator.goHome() }
    }
    // Coming back from Android's settings / role dialog: re-check whether we are the default Home.
    LifecycleResumeEffect(container) {
        container.refreshDefaultHome()
        onPauseOrDispose { }
    }
    // A launcher never exits on Back: close overlays / pop screens, otherwise stay on Home.
    BackHandler { navigator.back() }

    val networkFlow = remember(container) { container.networkMonitor.status() }
    val network by networkFlow.collectAsStateWithLifecycle(initialValue = NetworkStatus.OFFLINE)

    val callbacks = remember(navigator, sidebarEntry, onPickWallpaper, onRequestDefaultHome) {
        ShellCallbacks(
            navigator = navigator,
            sidebarEntry = sidebarEntry,
            onAppOptions = { optionsApp = it },
            onPickWallpaper = onPickWallpaper,
            onSetDefaultHome = onRequestDefaultHome,
        )
    }

    val selectedSidebar = SidebarItem.entries.firstOrNull { it.destination == navigator.currentRoot }
        ?: SidebarItem.HOME
    val sidebarExpanded = !settings.sidebarCompact || sidebarFocused
    val backdropAlpha by animateFloatAsState(
        targetValue = if (settings.sidebarCompact && sidebarFocused) 1f else 0f,
        animationSpec = tween(if (reduce) 0 else 200),
        label = "sidebarBackdrop",
    )

    Box(Modifier.fillMaxSize()) {
        WallpaperBackground(settings.wallpaper)

        Column(Modifier.fillMaxSize()) {
            TopNavBar(network = network, timeFormat = settings.timeFormat)
            Box(Modifier.weight(1f).fillMaxSize()) {
                // The layout always reserves the sidebar's *compact* width; when it expands it
                // simply grows over the content, so screens never reflow while you navigate.
                Row(Modifier.fillMaxSize()) {
                    Spacer(
                        Modifier.width(
                            if (settings.sidebarCompact) SidebarDimens.Compact else SidebarDimens.Expanded,
                        ),
                    )
                    Box(Modifier.weight(1f).fillMaxHeight().padding(start = 4.dp, end = 12.dp)) {
                        ScreenHost(
                            destination = navigator.current,
                            factory = factory,
                            settings = settings,
                            settingsViewModel = settingsViewModel,
                            callbacks = callbacks,
                        )
                    }
                }
                Box(
                    Modifier
                        .align(Alignment.TopStart)
                        .fillMaxHeight()
                        .width(SidebarDimens.Expanded + 40.dp)
                        .graphicsLayer { alpha = backdropAlpha }
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xF2050812), Color(0xE6050812), Color.Transparent),
                            ),
                        ),
                )
                TVSidebar(
                    selected = selectedSidebar,
                    expanded = sidebarExpanded,
                    onSelect = { item ->
                        val destination = item.destination
                        if (destination != null) navigator.openRoot(destination) else navigator.openQuickSettings()
                    },
                    selectedRequester = sidebarEntry,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .onFocusChanged { sidebarFocused = it.hasFocus },
                )
            }
        }

        ToastHost(Modifier.align(Alignment.BottomCenter))
    }

    if (navigator.quickSettingsOpen) {
        QuickSettingsPanel(
            timeFormat = settings.timeFormat,
            onDismiss = navigator::closeQuickSettings,
            onWallpaper = { navigator.push(Destination.Wallpaper) },
            onTheme = { navigator.push(Destination.Theme) },
            onCycleTimeFormat = settingsViewModel::cycleTimeFormat,
            onManageApps = settingsViewModel::openManageApps,
            onSystemSettings = settingsViewModel::openSystemSettings,
            onAbout = { navigator.push(Destination.About) },
        )
    }

    val appForOptions = optionsApp
    if (appForOptions != null) {
        AppOptionsDialog(
            app = appForOptions,
            iconLoader = container.iconLoader,
            onOpen = {
                optionsApp = null
                container.appActions.launch(appForOptions)
            },
            onAppInfo = {
                optionsApp = null
                container.appActions.openAppInfo(appForOptions)
            },
            onDismiss = { optionsApp = null },
        )
    }
}

@Composable
private fun ScreenHost(
    destination: Destination,
    factory: NexaViewModelFactory,
    settings: UserSettings,
    settingsViewModel: SettingsViewModel,
    callbacks: ShellCallbacks,
) {
    // A plain switch: no cross-fade, so only one screen is ever composed at a time.
    Routes(destination, factory, settings, settingsViewModel, callbacks)
}
