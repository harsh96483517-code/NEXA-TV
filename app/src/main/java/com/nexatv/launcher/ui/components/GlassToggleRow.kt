package com.nexatv.launcher.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexatv.launcher.ui.theme.LocalNexaAccent
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType

/** A focusable settings row with a trailing on/off switch. OK toggles it. */
@Composable
fun GlassToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onCheckedChange(!checked) },
        cornerRadius = 14.dp,
        focusedScale = 1.02f,
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(text = title, style = NexaType.Title.copy(fontSize = 16.sp))
                Text(text = subtitle, style = NexaType.Caption)
            }
            Spacer(Modifier.width(16.dp))
            SwitchGraphic(checked)
        }
    }
}

/** A settings row that shows a value and runs an action (used by Quick Settings and the lists). */
@Composable
fun GlassActionRow(
    title: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    value: String? = null,
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        cornerRadius = 14.dp,
        focusedScale = 1.02f,
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NexaIcon(icon, size = 22.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(text = title, style = NexaType.Label.copy(fontSize = 15.sp))
                if (subtitle != null) Text(text = subtitle, style = NexaType.Caption)
            }
            if (value != null) {
                Spacer(Modifier.width(10.dp))
                Text(text = value, style = NexaType.Caption.copy(color = LocalNexaAccent.current.start))
            }
        }
    }
}

@Composable
private fun SwitchGraphic(checked: Boolean) {
    val accent = LocalNexaAccent.current
    val knobOffset by animateDpAsState(if (checked) 22.dp else 2.dp, tween(160), label = "switchKnob")
    Box(
        modifier = Modifier
            .width(46.dp)
            .height(26.dp)
            .clip(RoundedCornerShape(50))
            .background(if (checked) accent.horizontal else androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.2f))),
    ) {
        Box(
            Modifier
                .offset(x = knobOffset, y = 2.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(NexaPalette.TextPrimary),
        )
    }
}
