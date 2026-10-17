package com.nexatv.launcher.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.nexatv.launcher.platform.ClockFormatter
import com.nexatv.launcher.platform.NetworkStatus
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType
import kotlinx.coroutines.delay

/** Logo on the left, network status and the clock on the right. Nothing in it takes focus. */
@Composable
fun TopNavBar(
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
        Spacer(Modifier.weight(1f))
        NetworkIndicator(network)
        Spacer(Modifier.width(16.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(text = timeText, style = NexaType.Title.copy(fontSize = 17.sp, lineHeight = 20.sp))
            Text(text = dateText, style = NexaType.Caption.copy(fontSize = 11.sp, lineHeight = 14.sp))
        }
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
