package com.nexatv.launcher.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import com.nexatv.launcher.domain.model.ArtMotif
import com.nexatv.launcher.domain.model.ArtSpec
import kotlin.math.min
import kotlin.random.Random

/**
 * Original, procedurally generated artwork (sky gradient, glowing sun/planet, layered terrain).
 * It stands in for poster and wallpaper images, so the project ships no third-party artwork and no
 * binary assets. Everything is computed once per size inside drawWithCache.
 */
@Composable
fun ArtCanvas(spec: ArtSpec, modifier: Modifier = Modifier, stars: Boolean = true) {
    Box(modifier.artBackground(spec, stars))
}

private class TerrainLayer(val path: Path, val color: Color)

fun Modifier.artBackground(spec: ArtSpec, stars: Boolean = true): Modifier = drawWithCache {
    val w = size.width
    val h = size.height
    // Nothing to draw (and the terrain builders would never terminate on a zero-height canvas).
    if (w < 1f || h < 1f) return@drawWithCache onDrawBehind { }
    val rnd = Random(spec.seed)

    val top = Color(spec.top)
    val bottom = Color(spec.bottom)
    val accent = Color(spec.accent)

    val sky = Brush.verticalGradient(listOf(top, bottom), startY = 0f, endY = h)

    val sunCenter = Offset(w * (0.5f + rnd.nextFloat() * 0.3f), h * (0.26f + rnd.nextFloat() * 0.16f))
    val sunRadius = min(w, h) * if (spec.motif == ArtMotif.ICE) 0.2f else 0.15f
    val sunGlow = Brush.radialGradient(
        colors = listOf(accent.copy(alpha = 0.85f), accent.copy(alpha = 0.28f), Color.Transparent),
        center = sunCenter,
        radius = sunRadius * 2.6f,
    )

    val starPoints = if (stars) {
        List(if (w * h > 160_000f) 70 else 28) {
            Triple(
                Offset(rnd.nextFloat() * w, rnd.nextFloat() * h * 0.6f),
                (0.5f + rnd.nextFloat() * 1.4f),
                0.25f + rnd.nextFloat() * 0.6f,
            )
        }
    } else {
        emptyList()
    }

    // A large planet limb for the PLANET motif, otherwise nothing.
    val planetCenter = Offset(w * 0.8f, -h * 0.05f)
    val planetRadius = h * 0.78f
    val planetBrush = Brush.radialGradient(
        colors = listOf(top.copy(alpha = 0f), accent.copy(alpha = 0.10f), accent.copy(alpha = 0.55f)),
        center = planetCenter,
        radius = planetRadius,
    )

    val layers = buildLayers(spec.motif, rnd, w, h, top, bottom, accent)
    val vignette = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f)),
        startY = h * 0.55f,
        endY = h,
    )

    onDrawBehind {
        drawRect(sky, size = Size(w, h))
        for ((point, radius, alpha) in starPoints) {
            drawCircle(Color.White.copy(alpha = alpha), radius = radius, center = point)
        }
        if (spec.motif == ArtMotif.PLANET) {
            drawCircle(planetBrush, radius = planetRadius, center = planetCenter)
            drawCircle(
                accent.copy(alpha = 0.45f),
                radius = planetRadius,
                center = planetCenter,
                style = Stroke(width = 2f),
            )
        }
        drawCircle(sunGlow, radius = sunRadius * 2.6f, center = sunCenter)
        drawCircle(accent.copy(alpha = 0.92f), radius = sunRadius * 0.5f, center = sunCenter)
        for (layer in layers) drawPath(layer.path, layer.color)
        drawRect(vignette, size = Size(w, h))
    }
}

private fun buildLayers(
    motif: ArtMotif,
    rnd: Random,
    w: Float,
    h: Float,
    top: Color,
    bottom: Color,
    accent: Color,
): List<TerrainLayer> {
    fun shade(t: Float) = lerp(bottom, Color.Black, t)
    return when (motif) {
        ArtMotif.RIDGES, ArtMotif.PLANET -> listOf(
            TerrainLayer(ridgePath(rnd, w, h, h * 0.66f, h * 0.10f, 14), shade(0.30f)),
            TerrainLayer(ridgePath(rnd, w, h, h * 0.76f, h * 0.09f, 12), shade(0.50f)),
            TerrainLayer(ridgePath(rnd, w, h, h * 0.88f, h * 0.06f, 10), shade(0.72f)),
        )
        ArtMotif.ICE -> listOf(
            TerrainLayer(ridgePath(rnd, w, h, h * 0.62f, h * 0.14f, 16), lerp(accent, bottom, 0.82f)),
            TerrainLayer(ridgePath(rnd, w, h, h * 0.76f, h * 0.10f, 12), lerp(accent, bottom, 0.9f)),
            TerrainLayer(ridgePath(rnd, w, h, h * 0.9f, h * 0.05f, 10), shade(0.6f)),
        )
        ArtMotif.CITY -> listOf(
            TerrainLayer(skylinePath(rnd, w, h, h * 0.84f, h * 0.38f), shade(0.42f)),
            TerrainLayer(skylinePath(rnd, w, h, h * 0.95f, h * 0.34f), shade(0.70f)),
        )
        ArtMotif.FOREST -> listOf(
            TerrainLayer(treesPath(rnd, w, h, h * 0.78f, h * 0.30f), shade(0.45f)),
            TerrainLayer(treesPath(rnd, w, h, h * 0.92f, h * 0.34f), shade(0.72f)),
        )
        ArtMotif.DESERT -> listOf(
            TerrainLayer(dunePath(rnd, w, h, h * 0.70f, h * 0.10f), lerp(accent, bottom, 0.78f)),
            TerrainLayer(dunePath(rnd, w, h, h * 0.82f, h * 0.10f), shade(0.45f)),
            TerrainLayer(dunePath(rnd, w, h, h * 0.94f, h * 0.07f), shade(0.72f)),
        )
    }
}

private fun ridgePath(rnd: Random, w: Float, h: Float, baseY: Float, amp: Float, steps: Int): Path {
    val path = Path()
    val dx = w / steps
    var y = baseY
    path.moveTo(0f, h)
    path.lineTo(0f, y)
    for (i in 1..steps) {
        val target = baseY + (rnd.nextFloat() - 0.5f) * 2f * amp
        y = (y * 0.35f + target * 0.65f).coerceIn(0f, h)
        path.lineTo(dx * i, y)
    }
    path.lineTo(w, h)
    path.close()
    return path
}

private fun dunePath(rnd: Random, w: Float, h: Float, baseY: Float, amp: Float): Path {
    val path = Path()
    path.moveTo(0f, h)
    path.lineTo(0f, baseY)
    var x = 0f
    while (x < w) {
        val segment = w * (0.25f + rnd.nextFloat() * 0.2f)
        val nextX = min(w, x + segment)
        path.cubicTo(
            x + segment * 0.35f, baseY - amp * (0.4f + rnd.nextFloat()),
            x + segment * 0.65f, baseY + amp * (rnd.nextFloat() - 0.3f),
            nextX, baseY + (rnd.nextFloat() - 0.5f) * amp * 0.6f,
        )
        x = nextX
    }
    path.lineTo(w, h)
    path.close()
    return path
}

private fun skylinePath(rnd: Random, w: Float, h: Float, baseY: Float, maxHeight: Float): Path {
    val path = Path()
    var x = 0f
    while (x < w) {
        val buildingWidth = w * (0.03f + rnd.nextFloat() * 0.05f)
        val buildingHeight = maxHeight * (0.25f + rnd.nextFloat() * 0.75f)
        path.addRect(Rect(x, baseY - buildingHeight, min(w, x + buildingWidth), h))
        x += buildingWidth * (0.9f + rnd.nextFloat() * 0.3f)
    }
    return path
}

private fun treesPath(rnd: Random, w: Float, h: Float, baseY: Float, treeHeight: Float): Path {
    val path = Path()
    var x = -treeHeight * 0.2f
    while (x < w) {
        val th = treeHeight * (0.6f + rnd.nextFloat() * 0.6f)
        val tw = th * 0.45f
        path.moveTo(x, baseY)
        path.lineTo(x + tw / 2f, baseY - th)
        path.lineTo(x + tw, baseY)
        path.close()
        x += tw * 0.7f
    }
    path.addRect(Rect(0f, baseY - 1f, w, h))
    return path
}
