package com.nexatv.launcher.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import com.nexatv.launcher.R
import com.nexatv.launcher.app.MessageBus
import com.nexatv.launcher.domain.model.UiMessage
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

/** Provided once at the root so dialogs (separate windows) can show toasts too. */
val LocalMessageBus = staticCompositionLocalOf<MessageBus?> { null }

private const val TOAST_DURATION_MS = 3_800L

/**
 * Transient glass toast, bottom-centre. It is never focusable, so it can not trap the D-pad,
 * and a newer message replaces the current one immediately.
 */
@Composable
fun ToastHost(modifier: Modifier = Modifier) {
    val bus = LocalMessageBus.current ?: return
    var current by remember { mutableStateOf<UiMessage?>(null) }

    LaunchedEffect(bus) {
        bus.messages.collectLatest { message ->
            current = message
            delay(TOAST_DURATION_MS)
            current = null
        }
    }

    Box(modifier.fillMaxWidth().padding(bottom = 28.dp), contentAlignment = Alignment.BottomCenter) {
        Crossfade(targetState = current, animationSpec = tween(220), label = "toast") { message ->
            if (message != null) {
                GlassPanel(cornerRadius = 999.dp, modifier = Modifier.widthIn(max = 560.dp)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NexaIcon(
                            id = if (message.isError) R.drawable.ic_info else R.drawable.ic_check,
                            size = 18.dp,
                            tint = if (message.isError) NexaPalette.Error else NexaPalette.Success,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(text = message.text, style = NexaType.Label, maxLines = 2)
                    }
                }
            }
        }
    }
}
