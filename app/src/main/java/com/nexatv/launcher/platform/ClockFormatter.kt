package com.nexatv.launcher.platform

import com.nexatv.launcher.domain.model.TimeFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Pure formatting helpers for the top-bar clock (kept free of Android types so they are unit-testable). */
object ClockFormatter {

    fun timeText(
        millis: Long,
        format: TimeFormat,
        systemIs24Hour: Boolean,
        locale: Locale = Locale.getDefault(),
        zone: TimeZone = TimeZone.getDefault(),
    ): String {
        val use24Hour = when (format) {
            TimeFormat.SYSTEM -> systemIs24Hour
            TimeFormat.H12 -> false
            TimeFormat.H24 -> true
        }
        val pattern = if (use24Hour) "HH:mm" else "h:mm a"
        return SimpleDateFormat(pattern, locale).apply { timeZone = zone }.format(Date(millis))
    }

    fun dateText(
        millis: Long,
        locale: Locale = Locale.getDefault(),
        zone: TimeZone = TimeZone.getDefault(),
    ): String = SimpleDateFormat("EEE, d MMM", locale).apply { timeZone = zone }.format(Date(millis))

    /** Milliseconds until the wall clock next ticks over to a new minute (always 1..60000). */
    fun millisUntilNextMinute(now: Long): Long = 60_000L - (now % 60_000L)
}
