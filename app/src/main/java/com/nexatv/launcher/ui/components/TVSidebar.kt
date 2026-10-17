package com.nexatv.launcher.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexatv.launcher.R
import com.nexatv.launcher.navigation.Destination
import com.nexatv.launcher.ui.theme.LocalNexaAccent
import com.nexatv.launcher.ui.theme.LocalReduceEffects
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/** Shared widths: the shell reserves [Compact] in the layout and lets the sidebar grow over content. */
object SidebarDimens {
    val Compact = 68.dp
    val Expanded = 184.dp
}

/** Sidebar entries. [destination] is null for entries that open an overlay instead of a screen. */
enum class SidebarItem(val label: String, @DrawableRes val icon: Int, val destination: Destination?) {
    HOME("Apps", R.drawable.ic_apps, Destination.Home),
    SETTINGS("Settings", R.drawable.ic_settings, null),
}

/**
 * Left navigation rail. When [expanded] is false it collapses to icons; the shell expands it while
 * it holds focus, so the compact state costs nothing in usability.
 *
 * @param selectedRequester attached to the selected entry so content can send "Left" back to it.
 */
@Composable
fun TVSidebar(
    selected: SidebarItem,
    expanded: Boolean,
    onSelect: (SidebarItem) -> Unit,
    selectedRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val reduce = LocalReduceEffects.current
    val width by animateDpAsState(
        targetValue = if (expanded) SidebarDimens.Expanded else SidebarDimens.Compact,
        animationSpec = tween(if (reduce) 0 else 220),
        label = "sidebarWidth",
    )
    val labelAlpha by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(if (reduce) 0 else 180),
        label = "sidebarLabel",
    )

    Column(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .clipToBounds()
            .padding(start = 14.dp, end = 10.dp, top = 6.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SidebarItem.entries.forEach { item ->
            val isSelected = item == selected
            SidebarRow(
                item = item,
                selected = isSelected,
                labelAlpha = labelAlpha,
                onClick = { onSelect(item) },
                modifier = if (isSelected) Modifier.focusRequester(selectedRequester) else Modifier,
            )
        }
        Spacer(Modifier.weight(1f))
        if (labelAlpha > 0.01f) SidebarBrand(Modifier.alpha(labelAlpha))
    }
}

@Composable
private fun SidebarRow(
    item: SidebarItem,
    selected: Boolean,
    labelAlpha: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = LocalNexaAccent.current
    GlassCard(
        modifier = modifier.fillMaxWidth().height(42.dp),
        onClick = onClick,
        cornerRadius = 14.dp,
        focusedScale = 1.04f,
        selected = selected,
        tint = if (selected) accent.mid else Color.White,
        tintStrength = if (selected) 2.4f else 0.7f,
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NexaIcon(item.icon, size = 20.dp)
            if (labelAlpha > 0.01f) {
                Spacer(Modifier.width(12.dp))
                Text(
                    text = item.label,
                    style = NexaType.Label,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.alpha(labelAlpha),
                )
            }
        }
    }
}

/** Wavy glass brand block at the foot of the sidebar, as in the reference design. */
@Composable
private fun SidebarBrand(modifier: Modifier = Modifier) {
    val accent = LocalNexaAccent.current
    Box(modifier.fillMaxWidth().height(86.dp)) {
        Canvas(Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val wave = Path().apply {
                moveTo(0f, h * 0.55f)
                cubicTo(w * 0.25f, h * 0.15f, w * 0.55f, h * 0.95f, w, h * 0.35f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                wave,
                Brush.linearGradient(
                    listOf(accent.start.copy(alpha = 0.35f), accent.mid.copy(alpha = 0.30f), accent.end.copy(alpha = 0.45f)),
                ),
            )
        }
        Column(Modifier.align(Alignment.BottomStart).padding(start = 6.dp, bottom = 8.dp)) {
            Text(
                text = "HARSH REALITY",
                style = NexaType.Label.copy(fontSize = 12.sp, letterSpacing = 1.5.sp, color = NexaPalette.TextPrimary),
            )
            Text(
                text = "Your World On Screen",
                style = NexaType.Caption.copy(fontSize = 10.sp, lineHeight = 12.sp, color = NexaPalette.TextSecondary),
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

