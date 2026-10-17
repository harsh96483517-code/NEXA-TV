package com.nexatv.launcher.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.nexatv.launcher.domain.model.AccentTheme

object NexaPalette {
    val Background = Color(0xFF050812)
    val BackgroundRaised = Color(0xFF0A1022)

    val TextPrimary = Color(0xFFF4F7FF)
    val TextSecondary = Color(0xFFB9C3DC)
    val TextTertiary = Color(0xFF8E98B4)

    /** Frosted-glass surfaces are white at low alpha over the wallpaper. */
    val GlassFill = Color.White.copy(alpha = 0.075f)
    val GlassFillRaised = Color.White.copy(alpha = 0.12f)
    val GlassHighlight = Color.White.copy(alpha = 0.22f)
    val GlassBorder = Color.White.copy(alpha = 0.16f)

    val Scrim = Color(0xCC03060F)
    val Error = Color(0xFFFF7B8A)
    val Success = Color(0xFF5CE1A8)
}

/** Three-stop accent gradient used for focus borders, selected pills and progress bars. */
@Immutable
data class NexaAccent(val start: Color, val mid: Color, val end: Color) {
    val horizontal: Brush get() = Brush.horizontalGradient(listOf(start, mid, end))
    val diagonal: Brush get() = Brush.linearGradient(listOf(start, mid, end))
}

fun AccentTheme.colors(): NexaAccent = when (this) {
    AccentTheme.AURORA -> NexaAccent(Color(0xFF27D3FF), Color(0xFF3B7BFF), Color(0xFF9B6BFF))
    AccentTheme.OCEAN -> NexaAccent(Color(0xFF2CF0C8), Color(0xFF1FB4FF), Color(0xFF3B6BFF))
    AccentTheme.NEBULA -> NexaAccent(Color(0xFF5C8DFF), Color(0xFF8B5CF6), Color(0xFFE05CFF))
    AccentTheme.EMBER -> NexaAccent(Color(0xFFFFC24C), Color(0xFFFF7A45), Color(0xFFFF4C8B))
}

val LocalNexaAccent = staticCompositionLocalOf { AccentTheme.AURORA.colors() }

/** True when the user asked for fewer animations (also good for weak hardware). */
val LocalReduceEffects = staticCompositionLocalOf { false }
