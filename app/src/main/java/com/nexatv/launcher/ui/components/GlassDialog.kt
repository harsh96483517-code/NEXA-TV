package com.nexatv.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.shape.RoundedCornerShape
import com.nexatv.launcher.ui.theme.NexaPalette

/**
 * Full-screen overlay window with a dimming scrim. Compose's Dialog gives us the behaviour we
 * want for TV for free: focus is trapped inside the window and Back dismisses it. There is no
 * backdrop blur on purpose: it is expensive on TV GPUs and the scrim reads just as well.
 */
@Composable
fun GlassDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NexaPalette.Scrim.copy(alpha = 0.5f))
                // Touch only: a non-focusable tap target so the scrim never steals D-pad focus.
                .pointerInput(Unit) { detectTapGestures { onDismiss() } },
            contentAlignment = alignment,
        ) {
            content()
            ToastHost(Modifier.align(Alignment.BottomCenter))
        }
    }
}

/** Dark, mostly opaque glass used for overlays where text must stay readable over any wallpaper. */
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(listOf(Color(0xEB101A38), Color(0xEB0A1126))),
            )
            .border(1.dp, NexaPalette.GlassBorder, shape),
        content = content,
    )
}
