package com.nexatv.launcher.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import com.nexatv.launcher.ui.theme.NexaPalette

/**
 * Full-screen wallpaper. It is completely static (no drifting, no cross-fade) and lives in its own
 * graphics layer, so it is drawn once and never repainted while focus moves over the apps. A dark
 * scrim keeps text legible on any wallpaper, including user images.
 */
@Composable
fun WallpaperBackground(choice: WallpaperChoice, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(NexaPalette.Background).clipToBounds()) {
        val layerModifier = Modifier.fillMaxSize().graphicsLayer()
        when (choice) {
            is WallpaperChoice.BuiltIn -> ArtCanvas(WallpaperCatalog.byId(choice.id).art, layerModifier)
            is WallpaperChoice.Image -> ImageWallpaper(choice.uri, layerModifier)
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
private fun ImageWallpaper(uri: String, modifier: Modifier) {
    // If the file was deleted or the picker permission was revoked, fall back to the default art.
    var failed by remember(uri) { mutableStateOf(false) }
    if (failed) {
        ArtCanvas(WallpaperCatalog.byId(WallpaperCatalog.DEFAULT_ID).art, modifier)
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Uri.parse(uri))
                // 720p is plenty for a wallpaper under a dark scrim and keeps memory use low.
                .size(1280, 720)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier,
            onError = { failed = true },
        )
    }
}
