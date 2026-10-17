package com.nexatv.launcher.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nexatv.launcher.R
import com.nexatv.launcher.ui.theme.LocalNexaAccent
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/**
 * Pill-shaped glass button. [primary] fills it with the accent gradient (Watch Now / Resume);
 * secondary buttons stay frosted glass. Focus shows the luminous border, glow and scale-up.
 */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    primary: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
) {
    val accent = LocalNexaAccent.current
    GlassCard(
        modifier = modifier,
        onClick = onClick,
        cornerRadius = 999.dp,
        focusedScale = 1.06f,
        selected = primary,
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .then(if (primary) Modifier.background(accent.horizontal) else Modifier)
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (icon != null) {
                NexaIcon(icon, size = 18.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text(text = text, style = NexaType.Label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

/** Small pill used for "See All", tabs-in-content and similar low-emphasis actions. */
@Composable
fun ActionChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showArrow: Boolean = true,
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick,
        cornerRadius = 999.dp,
        focusedScale = 1.08f,
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = text, style = NexaType.Caption.copy(color = NexaPalette.TextPrimary))
            if (showArrow) {
                Spacer(Modifier.width(6.dp))
                NexaIcon(R.drawable.ic_arrow_forward, size = 12.dp, tint = Color.White)
            }
        }
    }
}
