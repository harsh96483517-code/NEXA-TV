package com.nexatv.launcher.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexatv.launcher.ui.theme.LocalNexaAccent
import com.nexatv.launcher.ui.theme.LocalReduceEffects
import com.nexatv.launcher.ui.theme.NexaPalette

/** Tracks whether the current OK-button press already fired the long-press action. */
private class LongPressTracker {
    var fired = false
}

/**
 * The building block of the whole UI: a translucent frosted-glass surface with a thin border,
 * a luminous accent border + soft glow and a small scale-up while focused.
 *
 * - With [onClick] it is focusable and activates on OK / Enter / touch.
 * - [onSecondaryAction] runs on the Menu key, on a long-press of OK, or on touch long-press.
 * - Without [onClick] it is a plain, non-focusable glass panel.
 *
 * Callers can attach `Modifier.focusRequester(...)` / `Modifier.focusProperties { ... }` through
 * [modifier]; they apply to the focusable surface because [modifier] is first in the chain.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onSecondaryAction: (() -> Unit)? = null,
    cornerRadius: Dp = 16.dp,
    focusedScale: Float = 1.05f,
    selected: Boolean = false,
    tint: Color = Color.White,
    tintStrength: Float = 1f,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.(focused: Boolean) -> Unit,
) {
    val accent = LocalNexaAccent.current
    val reduce = LocalReduceEffects.current
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }

    var focused by remember { mutableStateOf(false) }
    val glow by animateFloatAsState(
        targetValue = if (focused) 1f else 0f,
        animationSpec = tween(if (reduce) 0 else 180),
        label = "glassGlow",
    )
    val scale by animateFloatAsState(
        targetValue = if (focused) focusedScale else 1f,
        animationSpec = tween(if (reduce) 0 else 160),
        label = "glassScale",
    )
    val tracker = remember { LongPressTracker() }
    val interaction = remember { MutableInteractionSource() }

    val interactiveModifier = if (onClick != null) {
        Modifier
            .onFocusChanged { focused = it.isFocused }
            .onPreviewKeyEvent { event ->
                if (onSecondaryAction == null) return@onPreviewKeyEvent false
                val isOk = event.key == Key.DirectionCenter || event.key == Key.Enter
                when {
                    event.key == Key.Menu && event.type == KeyEventType.KeyDown -> {
                        onSecondaryAction()
                        true
                    }
                    isOk && event.type == KeyEventType.KeyDown && event.repeatCount == 1 -> {
                        tracker.fired = true
                        onSecondaryAction()
                        true
                    }
                    // Swallow the key-up that ends a long press so it does not also "click".
                    isOk && event.type == KeyEventType.KeyUp && tracker.fired -> {
                        tracker.fired = false
                        true
                    }
                    else -> false
                }
            }
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
                onLongClick = onSecondaryAction,
                onClick = onClick,
            )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                // Soft ambient glow: a few widening, fading strokes behind the card.
                if (glow > 0.01f) {
                    val radius = cornerRadius.toPx()
                    for (i in 1..3) {
                        val spread = i * 3.dp.toPx()
                        drawRoundRect(
                            color = accent.mid.copy(alpha = 0.22f * glow / i),
                            topLeft = Offset(-spread, -spread),
                            size = Size(size.width + spread * 2, size.height + spread * 2),
                            cornerRadius = CornerRadius(radius + spread),
                            style = Stroke(width = 3.dp.toPx()),
                        )
                    }
                }
            }
            .clip(shape)
            .drawBehind {
                val emphasis = maxOf(glow, if (selected) 0.65f else 0f)
                val top = ((0.13f + 0.09f * emphasis) * tintStrength).coerceAtMost(0.95f)
                val bottom = ((0.045f + 0.07f * emphasis) * tintStrength).coerceAtMost(0.9f)
                drawRect(
                    Brush.verticalGradient(listOf(tint.copy(alpha = top), tint.copy(alpha = bottom))),
                )
            }
            .border(
                width = if (focused) 2.dp else 1.dp,
                brush = when {
                    focused -> accent.diagonal
                    selected -> SolidColor(accent.mid.copy(alpha = 0.7f))
                    else -> SolidColor(NexaPalette.GlassBorder)
                },
                shape = shape,
            )
            .then(interactiveModifier),
        contentAlignment = contentAlignment,
    ) {
        content(focused)
    }
}
