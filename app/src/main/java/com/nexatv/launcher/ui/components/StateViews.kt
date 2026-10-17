package com.nexatv.launcher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nexatv.launcher.R
import com.nexatv.launcher.ui.theme.LocalNexaAccent
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/** Calm loading state: a thin accent spinner and a short message. */
@Composable
fun LoadingState(modifier: Modifier = Modifier, message: String = "Loading…") {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            CircularProgressIndicator(
                modifier = Modifier.size(34.dp),
                color = LocalNexaAccent.current.start,
                trackColor = NexaPalette.GlassFill,
                strokeWidth = 3.dp,
            )
            Text(text = message, style = NexaType.Body)
        }
    }
}

/**
 * Empty state with an optional action. When there is an action it takes focus automatically, so
 * the D-pad is never stranded on an empty screen.
 */
@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    StateLayout(
        modifier = modifier,
        title = title,
        message = message,
        actionLabel = actionLabel,
        onAction = onAction,
    )
}

/** Error state with a retry action that takes focus automatically. */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Something went wrong",
) {
    StateLayout(
        modifier = modifier,
        title = title,
        message = message,
        actionLabel = "Try again",
        onAction = onRetry,
        icon = R.drawable.ic_info,
    )
}

@Composable
private fun StateLayout(
    modifier: Modifier,
    title: String,
    message: String,
    actionLabel: String?,
    onAction: (() -> Unit)?,
    icon: Int = R.drawable.ic_apps,
) {
    val requester = remember { FocusRequester() }
    if (actionLabel != null) RequestFocusOnEnter(requester, actionLabel)
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GlassCard(cornerRadius = 22.dp) {
            Column(
                modifier = Modifier.padding(horizontal = 36.dp, vertical = 28.dp).widthIn(max = 380.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                NexaIcon(icon, size = 34.dp, tint = NexaPalette.TextSecondary)
                Spacer(Modifier.height(12.dp))
                Text(text = title, style = NexaType.Headline, textAlign = TextAlign.Center)
                Spacer(Modifier.height(6.dp))
                Text(text = message, style = NexaType.Body, textAlign = TextAlign.Center)
                if (actionLabel != null && onAction != null) {
                    Spacer(Modifier.height(18.dp))
                    GlassButton(
                        text = actionLabel,
                        onClick = onAction,
                        primary = true,
                        modifier = Modifier.focusRequester(requester),
                    )
                }
            }
        }
    }
}
