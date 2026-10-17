package com.nexatv.launcher.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexatv.launcher.R
import com.nexatv.launcher.domain.model.TimeFormat
import com.nexatv.launcher.navigation.Destination
import com.nexatv.launcher.platform.ClockFormatter
import com.nexatv.launcher.platform.NetworkStatus
import com.nexatv.launcher.ui.theme.LocalNexaAccent
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType
import kotlinx.coroutines.delay

private data class NavTabSpec(val label: String, val destination: Destination)

private val NavTabs = listOf(
    NavTabSpec("Home", Destination.Home),
    NavTabSpec("Movies", Destination.Movies),
    NavTabSpec("TV Shows", Destination.TvShows),
    NavTabSpec("Apps", Destination.Apps),
    NavTabSpec("Library", Destination.Library),
)

/** Logo, section tabs, network status, settings shortcut and the clock. */
@Composable
fun TopNavBar(
    currentRoot: Destination,
    onNavigate: (Destination) -> Unit,
    onOpenSettings: () -> Unit,
    network: NetworkStatus,
    timeFormat: TimeFormat,
    modifier: Modifier = Modifier,
) {
    val now by rememberNowMillis()
    val context = LocalContext.current
    val timeText = ClockFormatter.timeText(now, timeFormat, DateFormat.is24HourFormat(context))
    val dateText = ClockFormatter.dateText(now)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexaLogo()
        Spacer(Modifier.width(28.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            NavTabs.forEach { tab ->
                NavTab(
                    label = tab.label,
                    selected = tab.destination == currentRoot,
                    onClick = { onNavigate(tab.destination) },
                )
            }
        }
        Spacer(Modifier.weight(1f))
        NetworkIndicator(network)
        Spacer(Modifier.width(14.dp))
        GlassCard(
            onClick = onOpenSettings,
            cornerRadius = 999.dp,
            focusedScale = 1.12f,
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(36.dp),
        ) {
            NexaIcon(R.drawable.ic_settings, size = 20.dp)
        }
        Spacer(Modifier.width(16.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(text = timeText, style = NexaType.Title.copy(fontSize = 17.sp, lineHeight = 20.sp))
            Text(text = dateText, style = NexaType.Caption.copy(fontSize = 11.sp, lineHeight = 14.sp))
        }
    }
}

@Composable
private fun NavTab(label: String, selected: Boolean, onClick: () -> Unit) {
    val accent = LocalNexaAccent.current
    GlassCard(
        onClick = onClick,
        cornerRadius = 999.dp,
        focusedScale = 1.07f,
        selected = selected,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = NexaType.Label,
            modifier = Modifier
                .then(if (selected) Modifier.background(accent.horizontal) else Modifier)
                .padding(horizontal = 16.dp, vertical = 7.dp),
        )
    }
}

@Composable
private fun NetworkIndicator(status: NetworkStatus) {
    val (icon, tint) = when (status) {
        NetworkStatus.WIFI -> R.drawable.ic_wifi to NexaPalette.TextPrimary
        NetworkStatus.ETHERNET, NetworkStatus.OTHER -> R.drawable.ic_ethernet to NexaPalette.TextPrimary
        NetworkStatus.OFFLINE -> R.drawable.ic_wifi to NexaPalette.TextTertiary
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        NexaIcon(icon, size = 20.dp, tint = tint)
        if (status == NetworkStatus.OFFLINE) {
            Spacer(Modifier.width(6.dp))
            Text(text = "Offline", style = NexaType.Caption.copy(color = NexaPalette.Error))
        }
    }
}

/** Current time in millis, refreshed exactly when the wall clock rolls over to a new minute. */
@Composable
private fun rememberNowMillis(): State<Long> = produceState(initialValue = System.currentTimeMillis()) {
    while (true) {
        delay(ClockFormatter.millisUntilNextMinute(System.currentTimeMillis()))
        value = System.currentTimeMillis()
    }
}
