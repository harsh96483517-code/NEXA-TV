package com.nexatv.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.domain.model.ContentItem
import com.nexatv.launcher.platform.AppIconLoader
import com.nexatv.launcher.ui.components.RequestFocusOnEnter
import com.nexatv.launcher.ui.components.AppCarousel
import com.nexatv.launcher.ui.components.ContentCard
import com.nexatv.launcher.ui.components.ContinueWatchingCard
import com.nexatv.launcher.ui.components.ErrorState
import com.nexatv.launcher.ui.components.FeaturedHero
import com.nexatv.launcher.ui.components.LoadingState
import com.nexatv.launcher.ui.components.SectionHeader

/**
 * The home screen: featured hero, Your Apps, Continue Watching and Trending Now.
 *
 * Focus plan: the whole column scrolls vertically, every row is a LazyRow whose focused item is
 * scrolled into view automatically (so focus never disappears), pressing Left on the first item of
 * any row returns to the selected sidebar entry, and the hero's Watch Now button takes initial focus.
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    iconLoader: AppIconLoader,
    sidebarEntry: FocusRequester,
    onWatch: (ContentItem) -> Unit,
    onInfo: (ContentItem) -> Unit,
    onOpenContent: (String) -> Unit,
    onAppClick: (AppEntry) -> Unit,
    onAppOptions: (AppEntry) -> Unit,
    onSeeAllApps: () -> Unit,
    onBrowseMovies: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.loading -> LoadingState(modifier)
        state.error != null && state.featured.isEmpty() -> ErrorState(
            message = state.error,
            onRetry = onRetry,
            modifier = modifier,
        )
        else -> HomeContent(
            state = state,
            iconLoader = iconLoader,
            sidebarEntry = sidebarEntry,
            onWatch = onWatch,
            onInfo = onInfo,
            onOpenContent = onOpenContent,
            onAppClick = onAppClick,
            onAppOptions = onAppOptions,
            onSeeAllApps = onSeeAllApps,
            onBrowseMovies = onBrowseMovies,
            modifier = modifier,
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    iconLoader: AppIconLoader,
    sidebarEntry: FocusRequester,
    onWatch: (ContentItem) -> Unit,
    onInfo: (ContentItem) -> Unit,
    onOpenContent: (String) -> Unit,
    onAppClick: (AppEntry) -> Unit,
    onAppOptions: (AppEntry) -> Unit,
    onSeeAllApps: () -> Unit,
    onBrowseMovies: () -> Unit,
    modifier: Modifier,
) {
    val initialFocus = remember { FocusRequester() }
    val leftToSidebar = remember(sidebarEntry) { Modifier.focusProperties { left = sidebarEntry } }
    val hasHero = state.featured.isNotEmpty()

    RequestFocusOnEnter(initialFocus)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 4.dp, end = 4.dp),
    ) {
        if (hasHero) {
            FeaturedHero(
                items = state.featured,
                onWatch = onWatch,
                onInfo = onInfo,
                watchModifier = Modifier.focusRequester(initialFocus).then(leftToSidebar),
                modifier = Modifier.padding(horizontal = 4.dp),
            )
            Spacer(Modifier.height(10.dp))
        }

        AppCarousel(
            apps = state.appRow,
            iconLoader = iconLoader,
            onAppClick = onAppClick,
            onAppOptions = onAppOptions,
            onSeeAll = onSeeAllApps,
            firstItemModifier = (if (hasHero) Modifier else Modifier.focusRequester(initialFocus)).then(leftToSidebar),
        )

        if (state.continueWatching.isNotEmpty()) {
            SectionHeader(
                title = "Continue Watching",
                caption = if (state.continueWatching.any { it.content.isSample }) "Sample content" else null,
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                itemsIndexed(state.continueWatching, key = { _, item -> item.id }) { index, item ->
                    ContinueWatchingCard(
                        item = item,
                        onClick = { onOpenContent(item.id) },
                        modifier = if (index == 0) leftToSidebar else Modifier,
                    )
                }
            }
        }

        if (state.trending.isNotEmpty()) {
            SectionHeader(
                title = "Trending Now",
                caption = if (state.trending.any { it.isSample }) "Sample content" else null,
                actionLabel = "Browse Movies",
                onAction = onBrowseMovies,
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                itemsIndexed(state.trending, key = { _, item -> item.id }) { index, item ->
                    ContentCard(
                        item = item,
                        onClick = { onOpenContent(item.id) },
                        modifier = if (index == 0) leftToSidebar else Modifier,
                    )
                }
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}
