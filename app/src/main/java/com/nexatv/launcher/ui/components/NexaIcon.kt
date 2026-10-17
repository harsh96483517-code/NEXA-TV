package com.nexatv.launcher.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import com.nexatv.launcher.ui.theme.NexaPalette

/** Decorative vector icon: a text label always sits next to it, so it has no content description. */
@Composable
fun NexaIcon(
    @DrawableRes id: Int,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    tint: Color = NexaPalette.TextPrimary,
) {
    Icon(
        painter = painterResource(id),
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(size),
    )
}
