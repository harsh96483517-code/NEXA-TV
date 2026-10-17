package com.nexatv.launcher.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nexatv.launcher.R
import com.nexatv.launcher.domain.model.WallpaperChoice
import com.nexatv.launcher.ui.components.RequestFocusOnEnter
import com.nexatv.launcher.ui.components.ActionChip
import com.nexatv.launcher.ui.components.ArtCanvas
import com.nexatv.launcher.ui.components.GlassCard
import com.nexatv.launcher.ui.components.NexaIcon
import com.nexatv.launcher.ui.components.SectionHeader
import com.nexatv.launcher.ui.theme.LocalNexaAccent
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

private val TileWidth = 188.dp
private val TileHeight = 108.dp

/**
 * Wallpaper picker. Sources, all permitted for a normal app:
 *  - built-in procedural wallpapers,
 *  - images copied into Harsh Reality's own storage folder (no permission, works without a file picker),
 *  - any image chosen with Android's document picker, if the device has one.
 */
@Composable
fun WallpaperScreen(
    state: WallpaperUiState,
    sidebarEntry: FocusRequester,
    onSelect: (WallpaperChoice) -> Unit,
    onPickFromDevice: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val initialFocus = remember { FocusRequester() }
    val leftToSidebar = remember(sidebarEntry) { Modifier.focusProperties { left = sidebarEntry } }
    RequestFocusOnEnter(initialFocus)

    Column(modifier.fillMaxSize().padding(end = 8.dp)) {
        SectionHeader(title = "Wallpaper", caption = "Press OK to apply")
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = TileWidth),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(items = state.presets, key = { "preset-${it.id}" }) { preset ->
                val first = preset.id == state.presets.first().id
                WallpaperTile(
                    name = preset.name,
                    selected = state.current == WallpaperChoice.BuiltIn(preset.id),
                    onClick = { onSelect(WallpaperChoice.BuiltIn(preset.id)) },
                    modifier = if (first) Modifier.focusRequester(initialFocus).then(leftToSidebar) else Modifier,
                ) {
                    ArtCanvas(preset.art, Modifier.fillMaxSize())
                }
            }
            items(items = state.imported, key = { "file-${it.uri}" }) { file ->
                WallpaperTile(
                    name = file.name,
                    selected = state.current == WallpaperChoice.Image(file.uri),
                    onClick = { onSelect(WallpaperChoice.Image(file.uri)) },
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(Uri.parse(file.uri))
                            .size(376, 216)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            item(key = "pick-from-device") {
                GlassCard(
                    modifier = Modifier.size(TileWidth, TileHeight),
                    onClick = onPickFromDevice,
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        NexaIcon(R.drawable.ic_wallpaper, size = 28.dp)
                        Text(
                            text = "Choose from device",
                            style = NexaType.Label.copy(fontSize = 13.sp),
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }
            item(key = "folder-hint", span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text(
                        text = "No file picker on your TV? Copy JPG, PNG or WebP images into " +
                            "${state.folderHint} and refresh.",
                        style = NexaType.Caption.copy(color = NexaPalette.TextTertiary),
                        modifier = Modifier.weight(1f),
                    )
                    ActionChip(text = "Refresh", onClick = onRefresh, showArrow = false)
                }
            }
        }
    }
}

@Composable
private fun WallpaperTile(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    preview: @Composable BoxScope.() -> Unit,
) {
    val accent = LocalNexaAccent.current
    GlassCard(
        modifier = modifier.size(TileWidth, TileHeight),
        onClick = onClick,
        selected = selected,
        focusedScale = 1.06f,
    ) {
        preview()
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xB3000000)), startY = 40f)),
        )
        Text(
            text = name,
            style = NexaType.Label.copy(fontSize = 13.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 10.dp, end = 34.dp, bottom = 8.dp),
        )
        if (selected) {
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(20.dp)
                    .background(accent.horizontal, androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                NexaIcon(R.drawable.ic_check, size = 13.dp)
            }
        }
    }
}
