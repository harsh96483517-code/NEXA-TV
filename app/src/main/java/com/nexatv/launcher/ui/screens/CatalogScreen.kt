package com.nexatv.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.nexatv.launcher.domain.model.ContentItem
import com.nexatv.launcher.ui.components.RequestFocusOnEnter
import com.nexatv.launcher.ui.components.ContentCard
import com.nexatv.launcher.ui.components.EmptyState
import com.nexatv.launcher.ui.components.LoadingState
import com.nexatv.launcher.ui.components.SectionHeader

/** Movies, TV Shows and Library share one grid screen. */
@Composable
fun CatalogScreen(
    mode: CatalogMode,
    state: CatalogUiState,
    sidebarEntry: FocusRequester,
    onOpenContent: (String) -> Unit,
    onBrowseMovies: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = state.items
    when {
        items == null -> LoadingState(modifier)
        items.isEmpty() -> EmptyState(
            title = if (mode == CatalogMode.LIBRARY) "Your Library is empty" else "Nothing here yet",
            message = if (mode == CatalogMode.LIBRARY) {
                "Open any title and choose “Add to Library” to keep it here."
            } else {
                "No titles are available from the configured content source."
            },
            actionLabel = if (mode == CatalogMode.LIBRARY) "Browse Movies" else null,
            onAction = if (mode == CatalogMode.LIBRARY) onBrowseMovies else null,
            modifier = modifier,
        )
        else -> CatalogGrid(mode, items, sidebarEntry, onOpenContent, modifier)
    }
}

@Composable
private fun CatalogGrid(
    mode: CatalogMode,
    items: List<ContentItem>,
    sidebarEntry: FocusRequester,
    onOpenContent: (String) -> Unit,
    modifier: Modifier,
) {
    val initialFocus = remember { FocusRequester() }
    val leftToSidebar = remember(sidebarEntry) { Modifier.focusProperties { left = sidebarEntry } }
    RequestFocusOnEnter(initialFocus)

    Column(modifier.fillMaxSize().padding(end = 8.dp)) {
        SectionHeader(
            title = mode.title,
            caption = if (items.any { it.isSample }) "Sample content" else null,
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 156.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            itemsIndexed(items = items, key = { _, item -> item.id }) { index, item ->
                ContentCard(
                    item = item,
                    onClick = { onOpenContent(item.id) },
                    modifier = if (index == 0) Modifier.focusRequester(initialFocus).then(leftToSidebar) else Modifier,
                )
            }
        }
    }
}
