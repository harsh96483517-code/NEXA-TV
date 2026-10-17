package com.nexatv.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexatv.launcher.R
import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.platform.AppIcon
import com.nexatv.launcher.platform.AppIconLoader
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/**
 * Glass tile for one installed app. The glass is tinted with the icon's average color (as in the
 * reference design), enlarges slightly on focus and shows the luminous accent border.
 * OK launches the app; Menu / long-press OK opens the options dialog.
 */
@Composable
fun FocusableAppTile(
    app: AppEntry,
    iconLoader: AppIconLoader,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSecondaryAction: (() -> Unit)? = null,
    width: Dp = 104.dp,
    height: Dp = 80.dp,
) {
    val icon by produceState<AppIcon?>(initialValue = iconLoader.cached(app), app.key) {
        if (value == null) value = iconLoader.load(app)
    }
    val tint = remember(icon) { icon?.let { Color(it.dominantColor) } ?: Color.White }

    GlassCard(
        modifier = modifier.size(width, height),
        onClick = onClick,
        onSecondaryAction = onSecondaryAction,
        cornerRadius = 14.dp,
        focusedScale = 1.08f,
        tint = tint,
        tintStrength = if (icon != null) 2.2f else 1f,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 6.dp),
        ) {
            AppIconImage(icon = icon, label = app.label, size = 38.dp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = app.label,
                style = NexaType.Label.copy(fontSize = 12.sp, lineHeight = 15.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** The trailing "All Apps" tile of the apps row. */
@Composable
fun AllAppsTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 104.dp,
    height: Dp = 80.dp,
) {
    GlassCard(
        modifier = modifier.size(width, height),
        onClick = onClick,
        cornerRadius = 14.dp,
        focusedScale = 1.08f,
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NexaIcon(R.drawable.ic_apps, size = 32.dp)
            Spacer(Modifier.height(6.dp))
            Text(text = "All Apps", style = NexaType.Label.copy(fontSize = 12.sp))
        }
    }
}

/** App icon, or a lettered placeholder while it loads (or if it cannot be loaded at all). */
@Composable
fun AppIconImage(icon: AppIcon?, label: String, size: Dp, modifier: Modifier = Modifier) {
    if (icon != null) {
        val bitmap = remember(icon) { icon.bitmap.asImageBitmap() }
        Image(bitmap = bitmap, contentDescription = null, modifier = modifier.size(size))
    } else {
        Box(
            modifier = modifier
                .size(size)
                .background(NexaPalette.GlassFillRaised, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label.take(1).uppercase(),
                style = NexaType.Title.copy(fontSize = (size.value * 0.45f).sp),
            )
        }
    }
}
