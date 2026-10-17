package com.nexatv.launcher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nexatv.launcher.R
import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.domain.model.ContentItem
import com.nexatv.launcher.platform.AppIcon
import com.nexatv.launcher.platform.AppIconLoader
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/** Options for one app, opened with the Menu key or by long-pressing OK on an app tile. */
@Composable
fun AppOptionsDialog(
    app: AppEntry,
    iconLoader: AppIconLoader,
    onOpen: () -> Unit,
    onAppInfo: () -> Unit,
    onDismiss: () -> Unit,
) {
    val first = remember { FocusRequester() }

    GlassDialog(onDismiss = onDismiss) {
        // Requested from inside the dialog so the focus request targets the dialog's own window.
        RequestFocusOnEnter(first)
        GlassPanel(Modifier.width(360.dp)) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon by produceState<AppIcon?>(initialValue = iconLoader.cached(app), app.key) {
                        if (value == null) value = iconLoader.load(app)
                    }
                    AppIconImage(icon = icon, label = app.label, size = 40.dp)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(text = app.label, style = NexaType.Headline, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            text = app.packageName,
                            style = NexaType.Caption.copy(color = NexaPalette.TextTertiary),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                GlassActionRow(
                    title = "Open",
                    icon = R.drawable.ic_play,
                    onClick = onOpen,
                    modifier = Modifier.focusRequester(first),
                )
                GlassActionRow(
                    title = "App info",
                    subtitle = "Storage, permissions, uninstall",
                    icon = R.drawable.ic_info,
                    onClick = onAppInfo,
                )
            }
        }
    }
}

/**
 * Shown instead of faking playback: the bundled titles are sample data and NEXA TV cannot start
 * or resume content without a real content source.
 */
@Composable
fun SampleNoticeDialog(
    item: ContentItem,
    onOpenApps: () -> Unit,
    onDismiss: () -> Unit,
) {
    val first = remember { FocusRequester() }

    GlassDialog(onDismiss = onDismiss) {
        // Requested from inside the dialog so the focus request targets the dialog's own window.
        RequestFocusOnEnter(first)
        GlassPanel(Modifier.width(420.dp)) {
            Column(Modifier.padding(26.dp)) {
                Text(text = "Sample content", style = NexaType.Headline)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "“${item.title}” is placeholder content that ships with NEXA TV, and no " +
                        "content source is configured yet, so there is nothing to play. Your " +
                        "streaming apps are one step away on the Apps screen.",
                    style = NexaType.Body,
                )
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassButton(
                        text = "Open Apps",
                        icon = R.drawable.ic_apps,
                        primary = true,
                        onClick = onOpenApps,
                        modifier = Modifier.focusRequester(first),
                    )
                    GlassButton(text = "Close", onClick = onDismiss)
                }
            }
        }
    }
}
