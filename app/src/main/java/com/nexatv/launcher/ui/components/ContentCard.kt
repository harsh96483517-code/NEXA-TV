package com.nexatv.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexatv.launcher.domain.model.ContentItem
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/** Artwork card for Trending Now and the catalog grids. OK opens the details screen. */
@Composable
fun ContentCard(
    item: ContentItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 156.dp,
    height: Dp = 96.dp,
) {
    GlassCard(
        modifier = modifier.size(width, height),
        onClick = onClick,
        cornerRadius = 14.dp,
        focusedScale = 1.07f,
    ) {
        Box(Modifier.fillMaxSize().artBackground(item.art, stars = false))
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xB3000000)),
                        startY = 40f,
                    ),
                ),
        )
        if (item.isSample) {
            SampleBadge(Modifier.align(Alignment.TopEnd).padding(6.dp))
        }
        Column(Modifier.align(Alignment.BottomStart).padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(
                text = item.title,
                style = NexaType.Label.copy(fontSize = 13.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${item.year} · ${item.genres.firstOrNull().orEmpty()}",
                style = NexaType.Caption.copy(fontSize = 10.sp, color = NexaPalette.TextSecondary),
                maxLines = 1,
            )
        }
    }
}
