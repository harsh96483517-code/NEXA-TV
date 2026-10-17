package com.nexatv.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexatv.launcher.R
import com.nexatv.launcher.domain.model.ContinueWatchingItem
import com.nexatv.launcher.domain.usecase.DurationFormatter
import com.nexatv.launcher.ui.theme.LocalNexaAccent
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/**
 * Resume card: artwork, play disc, title, episode + time left and a progress bar. Progress values
 * come from the configured content source; the bundled data is sample data and badged as such.
 * OK opens the details screen, which holds the Resume and library actions.
 */
@Composable
fun ContinueWatchingCard(
    item: ContinueWatchingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 236.dp,
    height: Dp = 112.dp,
) {
    val accent = LocalNexaAccent.current
    val content = item.content
    val episode = item.episodeLabel?.let { "$it · " }.orEmpty()

    GlassCard(
        modifier = modifier.size(width, height),
        onClick = onClick,
        cornerRadius = 16.dp,
        focusedScale = 1.05f,
    ) {
        Box(Modifier.fillMaxSize().artBackground(content.art, stars = false))
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC000000)), startY = 30f)),
        )
        if (content.isSample) {
            SampleBadge(Modifier.align(Alignment.TopEnd).padding(8.dp))
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 10.dp, end = 10.dp, bottom = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0x66000000))
                    .border(1.dp, Color.White.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                NexaIcon(R.drawable.ic_play, size = 16.dp)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = content.title,
                    style = NexaType.Label.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "$episode${DurationFormatter.format(item.remainingMinutes)} left",
                    style = NexaType.Caption.copy(fontSize = 11.sp, color = NexaPalette.TextSecondary),
                    maxLines = 1,
                )
            }
        }
        // Progress bar along the bottom edge.
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 6.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.22f)),
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(item.progress.coerceIn(0f, 1f))
                    .background(accent.horizontal),
            )
        }
    }
}
