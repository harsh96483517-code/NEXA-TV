package com.nexatv.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nexatv.launcher.R
import com.nexatv.launcher.domain.model.ContentItem
import com.nexatv.launcher.domain.model.ContentType
import com.nexatv.launcher.domain.model.ContinueWatchingItem
import com.nexatv.launcher.domain.usecase.DurationFormatter
import com.nexatv.launcher.ui.components.RequestFocusOnEnter
import com.nexatv.launcher.ui.components.ArtCanvas
import com.nexatv.launcher.ui.components.ContentMetaRow
import com.nexatv.launcher.ui.components.EmptyState
import com.nexatv.launcher.ui.components.GlassButton
import com.nexatv.launcher.ui.components.LoadingState
import com.nexatv.launcher.ui.components.SampleBadge
import com.nexatv.launcher.ui.theme.LocalNexaAccent
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaShapes
import com.nexatv.launcher.ui.theme.NexaType

/**
 * Details for one title: artwork, metadata, description, progress (when a content source provides
 * it) and the Resume / Watch Now and Library actions.
 */
@Composable
fun DetailsScreen(
    state: DetailsUiState,
    sidebarEntry: FocusRequester,
    onPlay: (ContentItem) -> Unit,
    onToggleLibrary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        DetailsUiState.Loading -> LoadingState(modifier)
        DetailsUiState.NotFound -> EmptyState(
            title = "Title not found",
            message = "This title is no longer available. Press Back to return.",
            modifier = modifier,
        )
        is DetailsUiState.Ready -> DetailsContent(state, sidebarEntry, onPlay, onToggleLibrary, modifier)
    }
}

@Composable
private fun DetailsContent(
    state: DetailsUiState.Ready,
    sidebarEntry: FocusRequester,
    onPlay: (ContentItem) -> Unit,
    onToggleLibrary: () -> Unit,
    modifier: Modifier,
) {
    val item = state.item
    val initialFocus = remember { FocusRequester() }
    val leftToSidebar = remember(sidebarEntry) { Modifier.focusProperties { left = sidebarEntry } }
    RequestFocusOnEnter(initialFocus)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(end = 8.dp, top = 4.dp, bottom = 12.dp)
            .clip(NexaShapes.Hero)
            .border(1.dp, NexaPalette.GlassBorder, NexaShapes.Hero),
    ) {
        ArtCanvas(item.art, Modifier.fillMaxSize())
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0.0f to Color(0xF0030610),
                        0.6f to Color(0xA0030610),
                        1.0f to Color(0x20030610),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .fillMaxWidth(0.62f)
                .padding(start = 32.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (item.type == ContentType.SHOW) "TV SHOW" else "MOVIE",
                    style = NexaType.Overline.copy(color = NexaPalette.TextPrimary),
                )
                if (item.isSample) {
                    Spacer(Modifier.width(8.dp))
                    SampleBadge()
                }
            }
            Text(text = item.title, style = NexaType.Display, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(text = item.tagline, style = NexaType.Body.copy(color = NexaPalette.TextPrimary))
            Spacer(Modifier.height(8.dp))
            ContentMetaRow(item)
            Spacer(Modifier.height(10.dp))
            Text(
                text = item.description,
                style = NexaType.Body,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            state.progress?.let { progress ->
                Spacer(Modifier.height(12.dp))
                ProgressSummary(progress)
            }
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassButton(
                    text = if (state.progress != null) "Resume" else "Watch Now",
                    icon = R.drawable.ic_play,
                    primary = true,
                    onClick = { onPlay(item) },
                    modifier = Modifier.focusRequester(initialFocus).then(leftToSidebar),
                )
                GlassButton(
                    text = if (state.inLibrary) "In Library" else "Add to Library",
                    icon = if (state.inLibrary) R.drawable.ic_check else R.drawable.ic_library_add,
                    onClick = onToggleLibrary,
                )
            }
            if (item.isSample) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Sample content: no content source is configured, so nothing can be played yet.",
                    style = NexaType.Caption.copy(color = NexaPalette.TextTertiary),
                )
            }
        }
    }
}

@Composable
private fun ProgressSummary(progress: ContinueWatchingItem) {
    val accent = LocalNexaAccent.current
    val label = listOfNotNull(progress.episodeLabel, "${DurationFormatter.format(progress.remainingMinutes)} left")
        .joinToString(" · ")
    Column {
        Text(text = label, style = NexaType.Caption.copy(color = NexaPalette.TextPrimary))
        Spacer(Modifier.height(5.dp))
        Box(
            Modifier
                .width(240.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.22f)),
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.progress.coerceIn(0f, 1f))
                    .background(accent.horizontal),
            )
        }
    }
}
