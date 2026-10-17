package com.nexatv.launcher.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexatv.launcher.R
import com.nexatv.launcher.domain.model.ContentItem
import com.nexatv.launcher.domain.usecase.DurationFormatter
import com.nexatv.launcher.ui.theme.LocalNexaAccent
import com.nexatv.launcher.ui.theme.LocalReduceEffects
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaShapes
import com.nexatv.launcher.ui.theme.NexaType
import kotlinx.coroutines.delay

private const val AUTO_ADVANCE_MS = 9_000L

/**
 * Large cinematic banner. Artwork and text cross-fade between items; the two buttons stay put (so
 * focus never jumps during a transition). The carousel indicators are focusable: moving focus onto
 * a dot selects that item. The banner auto-advances only while nothing inside it has focus and
 * "Reduce effects" is off.
 *
 * @param items must not be empty
 * @param watchModifier applied to the Watch Now button (initial focus, "Left leads to sidebar").
 */
@Composable
fun FeaturedHero(
    items: List<ContentItem>,
    onWatch: (ContentItem) -> Unit,
    onInfo: (ContentItem) -> Unit,
    modifier: Modifier = Modifier,
    watchModifier: Modifier = Modifier,
) {
    require(items.isNotEmpty()) { "FeaturedHero needs at least one item" }
    val reduce = LocalReduceEffects.current
    var index by rememberSaveable { mutableIntStateOf(0) }
    var hasFocus by remember { mutableStateOf(false) }
    val safeIndex = index.coerceIn(0, items.lastIndex)
    val current = items[safeIndex]
    val fade = if (reduce) 0 else 700

    LaunchedEffect(items.size, hasFocus, reduce) {
        if (items.size < 2 || hasFocus || reduce) return@LaunchedEffect
        while (true) {
            delay(AUTO_ADVANCE_MS)
            index = (index + 1) % items.size
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(184.dp)
            .clip(NexaShapes.Hero)
            .border(1.dp, NexaPalette.GlassBorder, NexaShapes.Hero)
            .onFocusChanged { hasFocus = it.hasFocus },
    ) {
        Crossfade(targetState = current, animationSpec = tween(fade), label = "heroArt") { item ->
            Box(Modifier.fillMaxSize()) {
                ArtCanvas(item.art, Modifier.fillMaxSize())
                // Left-to-right scrim keeps the text readable on any artwork.
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                0.0f to Color(0xE6030610),
                                0.55f to Color(0x99030610),
                                1.0f to Color.Transparent,
                            ),
                        ),
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 28.dp, end = 28.dp)
                .fillMaxWidth(0.62f),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "FEATURED", style = NexaType.Overline.copy(color = NexaPalette.TextPrimary))
                if (current.isSample) {
                    Spacer(Modifier.width(8.dp))
                    SampleBadge()
                }
            }
            Crossfade(targetState = current, animationSpec = tween(if (reduce) 0 else 500), label = "heroText") { item ->
                Column {
                    Text(
                        text = item.title,
                        style = NexaType.Display,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = item.tagline,
                        style = NexaType.Body.copy(fontSize = 15.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(6.dp))
                    ContentMetaRow(item)
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassButton(
                    text = "Watch Now",
                    icon = R.drawable.ic_play,
                    primary = true,
                    onClick = { onWatch(current) },
                    modifier = watchModifier,
                )
                GlassButton(
                    text = "More Info",
                    icon = R.drawable.ic_info,
                    onClick = { onInfo(current) },
                )
            }
        }

        if (items.size > 1) {
            Row(
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 14.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEachIndexed { i, _ ->
                    HeroDot(
                        selected = i == safeIndex,
                        onFocused = { index = i },
                        onClick = { index = i },
                    )
                }
            }
        }
    }
}

/** year • duration • rating ★ • genres, shared by the hero and the details screen. */
@Composable
fun ContentMetaRow(item: ContentItem) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val meta = NexaType.Caption.copy(fontSize = 13.sp, color = NexaPalette.TextSecondary)
        Text("${item.year}", style = meta)
        MetaDot()
        Text(DurationFormatter.format(item.durationMinutes), style = meta)
        MetaDot()
        Text(String.format(java.util.Locale.US, "%.1f", item.rating), style = meta)
        Spacer(Modifier.width(3.dp))
        NexaIcon(R.drawable.ic_star, size = 13.dp, tint = Color(0xFFFFC857))
        MetaDot()
        Text(item.genres.joinToString(" / "), style = meta, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun MetaDot() {
    Text(text = "  •  ", style = NexaType.Caption.copy(color = NexaPalette.TextTertiary))
}

@Composable
private fun HeroDot(selected: Boolean, onFocused: () -> Unit, onClick: () -> Unit) {
    val accent = LocalNexaAccent.current
    var focused by remember { mutableStateOf(false) }
    val dotWidth by animateDpAsState(
        targetValue = if (selected) 22.dp else 8.dp,
        animationSpec = tween(200),
        label = "heroDotWidth",
    )
    Box(
        modifier = Modifier
            .padding(horizontal = 1.dp)
            .clip(CircleShape)
            // (a 0.dp border would still draw a hairline, so only add it while focused)
            .then(if (focused) Modifier.border(1.5.dp, Color.White, CircleShape) else Modifier)
            .onFocusChanged {
                focused = it.isFocused
                if (it.isFocused) onFocused()
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 4.dp, vertical = 6.dp),
    ) {
        Box(
            Modifier
                .height(8.dp)
                .width(dotWidth)
                .clip(CircleShape)
                .background(if (selected) accent.horizontal else Brush.linearGradient(listOf(Color(0x66FFFFFF), Color(0x66FFFFFF)))),
        )
    }
}
