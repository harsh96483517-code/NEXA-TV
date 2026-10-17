package com.nexatv.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.nexatv.launcher.ui.components.ContentCard
import com.nexatv.launcher.ui.components.EmptyState
import com.nexatv.launcher.ui.components.FocusableAppTile
import com.nexatv.launcher.ui.components.GlassSearchField
import com.nexatv.launcher.ui.components.SectionHeader
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/** Search over installed apps (real) and the sample catalog. Focus starts in the text field. */
@Composable
fun SearchScreen(
    query: String,
    results: SearchResults,
    iconLoader: AppIconLoader,
    sidebarEntry: FocusRequester,
    onQueryChange: (String) -> Unit,
    onAppClick: (AppEntry) -> Unit,
    onAppOptions: (AppEntry) -> Unit,
    onOpenContent: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fieldFocus = remember { FocusRequester() }
    RequestFocusOnEnter(fieldFocus)

    Column(modifier.fillMaxSize().padding(end = 8.dp, start = 4.dp, top = 6.dp)) {
        GlassSearchField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .focusRequester(fieldFocus)
                .focusProperties { left = sidebarEntry },
        )
        Spacer(Modifier.height(10.dp))

        when {
            results.isBlank -> Text(
                text = "Type to search your apps and titles. Press Back to leave search.",
                style = NexaType.Body.copy(color = NexaPalette.TextTertiary),
                modifier = Modifier.padding(start = 8.dp, top = 8.dp),
            )
            results.isEmpty -> EmptyState(
                title = "No results",
                message = "Nothing matches “${results.query.trim()}”. Check the spelling or try a shorter search.",
            )
            else -> {
                if (results.apps.isNotEmpty()) {
                    SectionHeader(title = "Apps", caption = "${results.apps.size}")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(items = results.apps, key = { it.key }) { app ->
                            FocusableAppTile(
                                app = app,
                                iconLoader = iconLoader,
                                onClick = { onAppClick(app) },
                                onSecondaryAction = { onAppOptions(app) },
                            )
                        }
                    }
                }
                if (results.content.isNotEmpty()) {
                    SectionHeader(title = "Titles", caption = "Sample content")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(items = results.content, key = { it.id }) { item ->
                            ContentCard(item = item, onClick = { onOpenContent(item.id) })
                        }
                    }
                }
            }
        }
    }
}
