package com.nexatv.launcher.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.nexatv.launcher.domain.model.AccentTheme

/** The UI is laid out on a 960 dp wide canvas, which is what a 1080p TV reports at xhdpi. */
private const val DESIGN_WIDTH_DP = 960f

@Composable
fun NexaTheme(
    accent: AccentTheme,
    reduceEffects: Boolean,
    content: @Composable () -> Unit,
) {
    // Adaptive layout for different TV resolutions: instead of hand-tuning every dimension per
    // screen size, scale the density so the layout always sees (about) a 960 dp wide screen.
    // TVs that already report 960 dp are untouched; 720p / 4K / odd-density panels get a uniform scale.
    val baseDensity = LocalDensity.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val density = remember(baseDensity, screenWidthDp) {
        val factor = (screenWidthDp / DESIGN_WIDTH_DP).coerceIn(0.75f, 2.25f)
        Density(density = baseDensity.density * factor, fontScale = baseDensity.fontScale)
    }

    val accentColors = remember(accent) { accent.colors() }
    val colorScheme = darkColorScheme(
        primary = accentColors.mid,
        secondary = accentColors.start,
        tertiary = accentColors.end,
        background = NexaPalette.Background,
        surface = NexaPalette.BackgroundRaised,
        onPrimary = NexaPalette.TextPrimary,
        onBackground = NexaPalette.TextPrimary,
        onSurface = NexaPalette.TextPrimary,
        error = NexaPalette.Error,
    )

    CompositionLocalProvider(
        LocalDensity provides density,
        LocalNexaAccent provides accentColors,
        LocalReduceEffects provides reduceEffects,
        LocalContentColor provides NexaPalette.TextPrimary,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NexaMaterialTypography,
            content = content,
        )
    }
}
