package com.nexatv.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexatv.launcher.R
import com.nexatv.launcher.domain.model.AccentTheme
import com.nexatv.launcher.domain.model.UserSettings
import com.nexatv.launcher.ui.components.RequestFocusOnEnter
import com.nexatv.launcher.ui.components.GlassActionRow
import com.nexatv.launcher.ui.components.GlassCard
import com.nexatv.launcher.ui.components.GlassToggleRow
import com.nexatv.launcher.ui.components.NexaIcon
import com.nexatv.launcher.ui.components.SectionHeader
import com.nexatv.launcher.ui.theme.NexaPalette
import com.nexatv.launcher.ui.theme.NexaType
import com.nexatv.launcher.ui.theme.colors

/** Accent color, sidebar behaviour, effects and time format. Changes apply instantly and persist. */
@Composable
fun ThemeScreen(
    settings: UserSettings,
    sidebarEntry: FocusRequester,
    onAccent: (AccentTheme) -> Unit,
    onSidebarCompact: (Boolean) -> Unit,
    onReduceEffects: (Boolean) -> Unit,
    onCycleTimeFormat: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val initialFocus = remember { FocusRequester() }
    val leftToSidebar = remember(sidebarEntry) { Modifier.focusProperties { left = sidebarEntry } }
    RequestFocusOnEnter(initialFocus)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(end = 8.dp, bottom = 24.dp),
    ) {
        SectionHeader(title = "Theme")
        Text(text = "Accent", style = NexaType.Label.copy(color = NexaPalette.TextSecondary))
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            AccentTheme.entries.forEachIndexed { index, accent ->
                AccentCard(
                    accent = accent,
                    selected = accent == settings.accent,
                    onClick = { onAccent(accent) },
                    modifier = if (index == 0) Modifier.focusRequester(initialFocus).then(leftToSidebar) else Modifier,
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Column(
            modifier = Modifier.fillMaxWidth(0.82f).padding(horizontal = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            GlassToggleRow(
                title = "Compact sidebar",
                subtitle = "Show icons only; the sidebar opens while it has focus",
                checked = settings.sidebarCompact,
                onCheckedChange = onSidebarCompact,
                modifier = leftToSidebar,
            )
            GlassToggleRow(
                title = "Reduce effects",
                subtitle = "No window blur, drifting wallpaper or auto-rotating banner. Best for older TVs",
                checked = settings.reduceEffects,
                onCheckedChange = onReduceEffects,
                modifier = leftToSidebar,
            )
            GlassActionRow(
                title = "Time format",
                icon = R.drawable.ic_clock,
                value = settings.timeFormat.label,
                subtitle = "Press OK to switch: follow system, 12-hour, 24-hour",
                onClick = onCycleTimeFormat,
                modifier = leftToSidebar,
            )
        }
    }
}

@Composable
private fun AccentCard(
    accent: AccentTheme,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = accent.colors()
    GlassCard(
        modifier = modifier.size(width = 150.dp, height = 92.dp),
        onClick = onClick,
        selected = selected,
        focusedScale = 1.06f,
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .background(colors.horizontal, androidx.compose.foundation.shape.RoundedCornerShape(50)),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = accent.label, style = NexaType.Label.copy(fontSize = 14.sp), modifier = Modifier.weight(1f))
                if (selected) {
                    Box(
                        Modifier.size(18.dp).background(colors.horizontal, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        NexaIcon(R.drawable.ic_check, size = 12.dp)
                    }
                }
            }
        }
    }
}
