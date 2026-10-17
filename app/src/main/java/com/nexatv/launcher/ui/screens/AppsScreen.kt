package com.nexatv.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.platform.AppIconLoader
import com.nexatv.launcher.ui.components.RequestFocusOnEnter
import com.nexatv.launcher.ui.components.EmptyState
import com.nexatv.launcher.ui.components.FocusableAppTile
import com.nexatv.launcher.ui.components.LoadingState
import com.nexatv.launcher.ui.components.SectionHeader
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/** Home: every launchable app on the device. OK launches, Menu / long-press OK opens options. */
@Composable
fun AppsScreen(
    apps: List<AppEntry>?,
    iconLoader: AppIconLoader,
    sidebarEntry: FocusRequester,
    onAppClick: (AppEntry) -> Unit,
    onAppOptions: (AppEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        apps == null -> LoadingState(modifier, message = "Finding your apps…")
        apps.isEmpty() -> EmptyState(
            title = "No apps found",
            message = "Harsh Reality could not find any launchable apps on this device.",
            modifier = modifier,
        )
        else -> {
            val initialFocus = remember { FocusRequester() }
            val leftToSidebar = remember(sidebarEntry) { Modifier.focusProperties { left = sidebarEntry } }
            RequestFocusOnEnter(initialFocus)

            Column(modifier.fillMaxSize().padding(end = 8.dp)) {
                SectionHeader(title = "Apps", caption = "${apps.size} installed")
                Text(
                    text = "Press OK to open · hold OK or press Menu for options",
                    style = NexaType.Caption.copy(color = NexaPalette.TextTertiary),
                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 112.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    itemsIndexed(items = apps, key = { _, app -> app.key }) { index, app ->
                        FocusableAppTile(
                            app = app,
                            iconLoader = iconLoader,
                            onClick = { onAppClick(app) },
                            onSecondaryAction = { onAppOptions(app) },
                            width = 108.dp,
                            modifier = if (index == 0) {
                                Modifier.focusRequester(initialFocus).then(leftToSidebar)
                            } else {
                                Modifier
                            },
                        )
                    }
                }
            }
        }
    }
}
