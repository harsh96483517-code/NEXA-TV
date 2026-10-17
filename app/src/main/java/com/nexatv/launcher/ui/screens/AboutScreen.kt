package com.nexatv.launcher.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.nexatv.launcher.BuildConfig
import com.nexatv.launcher.R
import com.nexatv.launcher.ui.components.RequestFocusOnEnter
import com.nexatv.launcher.ui.components.GlassButton
import com.nexatv.launcher.ui.components.GlassPanel
import com.nexatv.launcher.ui.components.NexaIcon
import com.nexatv.launcher.ui.components.NexaLogo
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/** App info: version and the default-Home-app flow (with honest limits). */
@Composable
fun AboutScreen(
    isDefaultHome: Boolean,
    sidebarEntry: FocusRequester,
    onSetDefaultHome: () -> Unit,
    onOpenHomeSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val initialFocus = remember { FocusRequester() }
    val leftToSidebar = remember(sidebarEntry) { Modifier.focusProperties { left = sidebarEntry } }
    // Re-request when the state flips: the focused button is replaced once Harsh Reality becomes the default.
    RequestFocusOnEnter(initialFocus, isDefaultHome)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 6.dp, end = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column {
            NexaLogo()
            Spacer(Modifier.height(6.dp))
            Text(text = "Your World On Screen", style = NexaType.Headline)
            Text(
                text = "Version ${BuildConfig.VERSION_NAME} (build ${BuildConfig.VERSION_CODE}) · " +
                    "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                style = NexaType.Caption.copy(color = NexaPalette.TextTertiary),
            )
        }

        GlassPanel(Modifier.fillMaxWidth(0.9f)) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NexaIcon(
                        id = if (isDefaultHome) R.drawable.ic_check else R.drawable.ic_home_app,
                        tint = if (isDefaultHome) NexaPalette.Success else NexaPalette.TextPrimary,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (isDefaultHome) "Harsh Reality is your default Home app" else "Harsh Reality is not your default Home app",
                        style = NexaType.Title,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Android decides which app is Home, and only you can choose it. Harsh Reality can " +
                        "ask, and Android shows its own confirmation. On some TVs that dialog is missing, " +
                        "in which case use the Android Home settings button, or Settings > Apps > " +
                        "Default apps. Harsh Reality cannot replace the system launcher on its own.",
                    style = NexaType.Body,
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!isDefaultHome) {
                        GlassButton(
                            text = "Set as default Home app",
                            icon = R.drawable.ic_home_app,
                            primary = true,
                            onClick = onSetDefaultHome,
                            modifier = Modifier.focusRequester(initialFocus).then(leftToSidebar),
                        )
                        GlassButton(text = "Android Home settings", onClick = onOpenHomeSettings)
                    } else {
                        GlassButton(
                            text = "Android Home settings",
                            onClick = onOpenHomeSettings,
                            modifier = Modifier.focusRequester(initialFocus).then(leftToSidebar),
                        )
                    }
                }
            }
        }

        GlassPanel(Modifier.fillMaxWidth(0.9f)) {
            Column(Modifier.padding(20.dp)) {
                Text(text = "What it does", style = NexaType.Title)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Harsh Reality shows the apps installed on this device and launches them. " +
                        "Settings holds the wallpaper, accent colour, clock format and the shortcuts to " +
                        "Android's own settings. It has no movies, shows or other content of its own.",
                    style = NexaType.Body,
                )
            }
        }
    }
}
