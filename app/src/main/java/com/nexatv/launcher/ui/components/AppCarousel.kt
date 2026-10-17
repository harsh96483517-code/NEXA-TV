package com.nexatv.launcher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.platform.AppIconLoader

/**
 * The "Your Apps" row. Items use stable keys, and the row has vertical/horizontal content padding
 * so the focus glow and scale-up of the tiles are never clipped by the lazy list bounds.
 *
 * @param firstItemModifier applied to the first tile only (initial focus / "Left leads to sidebar").
 */
@Composable
fun AppCarousel(
    apps: List<AppEntry>,
    iconLoader: AppIconLoader,
    onAppClick: (AppEntry) -> Unit,
    onAppOptions: (AppEntry) -> Unit,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
    firstItemModifier: Modifier = Modifier,
) {
    Column(modifier) {
        SectionHeader(title = "Your Apps", actionLabel = "See All", onAction = onSeeAll)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(items = apps, key = { _, app -> app.key }) { index, app ->
                FocusableAppTile(
                    app = app,
                    iconLoader = iconLoader,
                    onClick = { onAppClick(app) },
                    onSecondaryAction = { onAppOptions(app) },
                    modifier = if (index == 0) firstItemModifier else Modifier,
                )
            }
            item(key = "all-apps") {
                AllAppsTile(
                    onClick = onSeeAll,
                    modifier = if (apps.isEmpty()) firstItemModifier else Modifier,
                )
            }
        }
    }
}
