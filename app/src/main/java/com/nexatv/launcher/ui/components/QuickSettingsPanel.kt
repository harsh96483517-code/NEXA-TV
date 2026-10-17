package com.nexatv.launcher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.nexatv.launcher.R
import com.nexatv.launcher.domain.model.TimeFormat
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/**
 * Right-hand Quick Settings panel with the five requested shortcuts plus a shortcut to Android's
 * own settings. Things Android does not let a normal app change (system settings, other apps) are
 * delegated to the matching system screen, and the footnote says so.
 */
@Composable
fun QuickSettingsPanel(
    timeFormat: TimeFormat,
    onDismiss: () -> Unit,
    onWallpaper: () -> Unit,
    onTheme: () -> Unit,
    onCycleTimeFormat: () -> Unit,
    onManageApps: () -> Unit,
    onSystemSettings: () -> Unit,
    onAbout: () -> Unit,
) {
    val firstItem = remember { FocusRequester() }

    GlassDialog(onDismiss = onDismiss, alignment = Alignment.CenterEnd) {
        // Requested from inside the dialog so the focus request targets the dialog's own window.
        RequestFocusOnEnter(firstItem)
        GlassPanel(
            modifier = Modifier
                .padding(vertical = 18.dp, horizontal = 20.dp)
                .width(330.dp)
                .fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = "Quick Settings", style = NexaType.Headline)
                Text(text = "Press Back to close", style = NexaType.Caption.copy(color = NexaPalette.TextTertiary))
                Spacer(Modifier.height(6.dp))

                GlassActionRow(
                    title = "Wallpaper",
                    subtitle = "Built-in or your own image",
                    icon = R.drawable.ic_wallpaper,
                    onClick = onWallpaper,
                    modifier = Modifier.focusRequester(firstItem),
                )
                GlassActionRow(
                    title = "Theme",
                    subtitle = "Accent, sidebar, effects",
                    icon = R.drawable.ic_palette,
                    onClick = onTheme,
                )
                GlassActionRow(
                    title = "Time format",
                    icon = R.drawable.ic_clock,
                    value = timeFormat.label,
                    onClick = onCycleTimeFormat,
                )
                GlassActionRow(
                    title = "App management",
                    subtitle = "Opens Android's app settings",
                    icon = R.drawable.ic_apps,
                    onClick = onManageApps,
                )
                GlassActionRow(
                    title = "System settings",
                    subtitle = "Network, display, sound…",
                    icon = R.drawable.ic_settings,
                    onClick = onSystemSettings,
                )
                GlassActionRow(
                    title = "About Harsh Reality",
                    subtitle = "Version and default Home app",
                    icon = R.drawable.ic_info,
                    onClick = onAbout,
                )

                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Android does not let apps change network, display or other system " +
                        "settings directly, so those shortcuts open the matching Android screen.",
                    style = NexaType.Caption.copy(color = NexaPalette.TextTertiary),
                )
            }
        }
    }
}
