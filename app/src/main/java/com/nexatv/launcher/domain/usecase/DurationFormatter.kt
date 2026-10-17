package com.nexatv.launcher.domain.usecase

/** Formats minute counts for the UI: 42 -> "42m", 130 -> "2h 10m", 120 -> "2h". */
object DurationFormatter {
    fun format(minutes: Int): String {
        val total = minutes.coerceAtLeast(0)
        val hours = total / 60
        val rest = total % 60
        return when {
            hours == 0 -> "${rest}m"
            rest == 0 -> "${hours}h"
            else -> "${hours}h ${rest}m"
        }
    }
}
