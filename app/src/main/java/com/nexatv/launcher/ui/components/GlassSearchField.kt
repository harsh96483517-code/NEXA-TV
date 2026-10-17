package com.nexatv.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.nexatv.launcher.R
import com.nexatv.launcher.ui.theme.LocalNexaAccent
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaShapes
import com.nexatv.launcher.ui.theme.NexaType

/**
 * Glass search box. It works with the on-screen / voice keyboard of a TV and with a hardware
 * keyboard (handy in the emulator). Focus is shown with the accent border like every other control.
 */
@Composable
fun GlassSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search apps and titles",
) {
    val accent = LocalNexaAccent.current
    val focusManager = LocalFocusManager.current
    var focused by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(NexaShapes.Pill)
            .background(if (focused) NexaPalette.GlassFillRaised else NexaPalette.GlassFill)
            .border(
                width = if (focused) 2.dp else 1.dp,
                brush = if (focused) accent.diagonal else SolidColor(NexaPalette.GlassBorder),
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NexaIcon(R.drawable.ic_search, size = 22.dp, tint = NexaPalette.TextSecondary)
        Spacer(Modifier.width(14.dp))
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(text = placeholder, style = NexaType.Title.copy(color = NexaPalette.TextTertiary, fontWeight = FontWeight.Normal))
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = NexaType.Title.copy(fontWeight = FontWeight.Normal),
                cursorBrush = SolidColor(accent.start),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = modifier
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused }
                    // A text field would otherwise swallow the D-pad and trap focus. Up / Down always
                    // leave it; Left leaves it while it is empty (so the sidebar stays reachable).
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                        when (event.key) {
                            Key.DirectionDown -> focusManager.moveFocus(FocusDirection.Down)
                            Key.DirectionUp -> focusManager.moveFocus(FocusDirection.Up)
                            Key.DirectionLeft ->
                                value.isEmpty() && focusManager.moveFocus(FocusDirection.Left)
                            else -> false
                        }
                    },
            )
        }
    }
}
