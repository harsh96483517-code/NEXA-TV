package com.nexatv.launcher.ui.components

import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nexatv.launcher.domain.model.WallpaperCatalog
import com.nexatv.launcher.domain.model.WallpaperChoice
import com.nexatv.launcher.ui.theme.LocalReduceEffects
import com.nexatv.launcher.ui.theme.NexaPalette

/**
 * Full-screen wallpaper with a soft cross-fade between choices and a very slow "breathing" zoom
 * (the subtle parallax). A dark scrim keeps text legible on any wallpaper, including user images.
 */
@Composable
fun WallpaperBackground(choice: WallpaperChoice, modifier: Modifier = Modifier) {
    val reduce = LocalReduceEffects.current
    Box(modifier.fillMaxSize().background(NexaPalette.Background).clipToBounds()) {
        Crossfade(
            targetState = choice,
            animationSpec = tween(durationMillis = if (reduce) 0 else 900),
            label = "wallpaper",
        ) { current ->
            WallpaperLayer(choice = current, animate = !reduce)
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x66030610), Color(0x2E030610), Color(0xB0030610)),
                    ),
                ),
        )
    }
}

@Composable
private fun WallpaperLayer(choice: WallpaperChoice, animate: Boolean) {
    val zoom: State<Float> = if (animate) {
        rememberInfiniteTransition(label = "wallpaperDrift").animateFloat(
            initialValue = 1.02f,
            targetValue = 1.07f,
            animationSpec = infiniteRepeatable(tween(32_000, easing = LinearEasing), RepeatMode.Reverse),
            label = "zoom",
        )
    } else {
        remember { mutableStateOf(1.02f) }
    }
    val layerModifier = Modifier
        .fillMaxSize()
        .graphicsLayer {
            // Read inside the layer block so the drift only invalidates drawing, not composition.
            val scale = zoom.value
            scaleX = scale
            scaleY = scale
        }

    when (choice) {
        is WallpaperChoice.BuiltIn -> ArtCanvas(WallpaperCatalog.byId(choice.id).art, layerModifier)
        is WallpaperChoice.Image -> ImageWallpaper(choice.uri, layerModifier)
    }
}

@Composable
private fun ImageWallpaper(uri: String, modifier: Modifier) {
    // If the file was deleted or the picker permission was revoked, fall back to the default art.
    var failed by remember(uri) { mutableStateOf(false) }
    if (failed) {
        ArtCanvas(WallpaperCatalog.byId(WallpaperCatalog.DEFAULT_ID).art, modifier)
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Uri.parse(uri))
                .size(1920, 1080)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier,
            onError = { failed = true },
        )
    }
}
