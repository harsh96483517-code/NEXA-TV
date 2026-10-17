package com.nexatv.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/** Section title with an optional caption (e.g. "Sample content") and an optional action chip. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    caption: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = title, style = NexaType.Headline.copy(fontSize = 19.sp))
            if (caption != null) {
                Text(
                    text = "  ·  $caption",
                    style = NexaType.Caption.copy(color = NexaPalette.TextTertiary),
                )
            }
        }
        if (actionLabel != null && onAction != null) {
            ActionChip(text = actionLabel, onClick = onAction)
        }
    }
}

/** Tiny "SAMPLE" badge shown on artwork so placeholder data is never mistaken for real data. */
@Composable
fun SampleBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0x99000000), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = "SAMPLE",
            style = NexaType.Overline.copy(fontSize = 8.sp, color = NexaPalette.TextPrimary),
        )
    }
}
