package com.nexatv.launcher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexatv.launcher.ui.theme.LocalNexaAccent
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/** Play-mark glyph with the accent gradient, drawn in code so it follows the selected theme. */
@Composable
fun NexaMark(modifier: Modifier = Modifier) {
    val accent = LocalNexaAccent.current
    Canvas(modifier.size(30.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.10f)
            lineTo(w * 0.18f, h * 0.90f)
            lineTo(w * 0.88f, h * 0.50f)
            close()
        }
        drawPath(path, Brush.linearGradient(listOf(accent.start, accent.mid, accent.end)))
        drawPath(
            path,
            color = NexaPalette.TextPrimary.copy(alpha = 0.35f),
            style = Stroke(width = 1.2f, join = StrokeJoin.Round),
        )
    }
}

/** The Harsh Reality wordmark: mark + letter-spaced name (two words, the second in the accent colour). */
@Composable
fun NexaLogo(modifier: Modifier = Modifier) {
    val accent = LocalNexaAccent.current
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        NexaMark()
        Spacer(Modifier.width(10.dp))
        Text(
            text = "HARSH",
            style = NexaType.Title.copy(fontSize = 16.sp, letterSpacing = 2.5.sp, fontWeight = FontWeight.Medium),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "REALITY",
            style = NexaType.Title.copy(
                fontSize = 16.sp,
                letterSpacing = 2.5.sp,
                fontWeight = FontWeight.Medium,
                color = accent.start,
            ),
        )
    }
}
