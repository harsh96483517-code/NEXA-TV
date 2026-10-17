package com.nexatv.launcher.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Type scale designed for a 960 x 540 dp canvas (see [NexaTheme], which scales the whole UI to the
 * real screen). Sizes are intentionally large so everything is readable from the couch.
 */
object NexaType {
    val Display = TextStyle(
        color = NexaPalette.TextPrimary, fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold,
    )
    val Headline = TextStyle(
        color = NexaPalette.TextPrimary, fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold,
    )
    val Title = TextStyle(
        color = NexaPalette.TextPrimary, fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold,
    )
    val Body = TextStyle(
        color = NexaPalette.TextSecondary, fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal,
    )
    val Label = TextStyle(
        color = NexaPalette.TextPrimary, fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium,
    )
    val Caption = TextStyle(
        color = NexaPalette.TextSecondary, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Normal,
    )
    val Overline = TextStyle(
        color = NexaPalette.TextSecondary, fontSize = 11.sp, lineHeight = 14.sp,
        fontWeight = FontWeight.SemiBold, letterSpacing = 1.6.sp,
    )
}

val NexaMaterialTypography = Typography(
    displayLarge = NexaType.Display,
    headlineMedium = NexaType.Headline,
    titleMedium = NexaType.Title,
    bodyMedium = NexaType.Body,
    labelLarge = NexaType.Label,
    bodySmall = NexaType.Caption,
)

object NexaShapes {
    val Card = RoundedCornerShape(16.dp)
    val Tile = RoundedCornerShape(14.dp)
    val Panel = RoundedCornerShape(22.dp)
    val Pill = RoundedCornerShape(50)
}
