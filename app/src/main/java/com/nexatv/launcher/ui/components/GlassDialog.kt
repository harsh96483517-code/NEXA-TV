package com.nexatv.launcher.ui.components

import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.foundation.shape.RoundedCornerShape
import com.nexatv.launcher.ui.theme.LocalReduceEffects
import com.nexatv.launcher.ui.theme.NexaPalette

/**
 * Full-screen overlay window with a dimming scrim. Compose's Dialog gives us the behaviour we
 * want for TV for free: focus is trapped inside the window and Back dismisses it.
 *
 * On Android 12+ devices that support cross-window blur, the content behind the dialog is blurred
 * (real backdrop blur, applied by the system compositor). Elsewhere the scrim alone is used, so it
 * degrades gracefully on low-end TV hardware. "Reduce effects" turns the blur off.
 */
@Composable
fun GlassDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    val reduce = LocalReduceEffects.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        if (!reduce) BlurBehindWindow(radiusPx = 40)
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

@Composable
private fun BlurBehindWindow(radiusPx: Int) {
    val view = LocalView.current
    SideEffect {
        val window = (view.parent as? DialogWindowProvider)?.window ?: return@SideEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            val attributes = window.attributes
            attributes.blurBehindRadius = radiusPx
            window.attributes = attributes
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
