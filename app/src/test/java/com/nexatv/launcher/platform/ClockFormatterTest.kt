package com.nexatv.launcher.platform

import com.nexatv.launcher.domain.model.TimeFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class ClockFormatterTest {

    private val utc = TimeZone.getTimeZone("UTC")
    private val kolkata = TimeZone.getTimeZone("Asia/Kolkata")

    // 2026-10-15T14:05:00Z
    private val instant = 1_792_073_100_000L

    @Test
    fun twentyFourHourFormat() {
        val text = ClockFormatter.timeText(instant, TimeFormat.H24, systemIs24Hour = false, Locale.US, utc)
        assertEquals("14:05", text)
    }

    @Test
    fun twelveHourFormatContainsMeridiem() {
        val text = ClockFormatter.timeText(instant, TimeFormat.H12, systemIs24Hour = true, Locale.US, utc)
        assertTrue(text, text.startsWith("2:05"))
        assertTrue(text, text.uppercase().endsWith("PM"))
    }

    @Test
    fun systemFormatFollowsSystemFlag() {
        assertEquals(
            "14:05",
            ClockFormatter.timeText(instant, TimeFormat.SYSTEM, systemIs24Hour = true, Locale.US, utc),
        )
        assertTrue(
            ClockFormatter.timeText(instant, TimeFormat.SYSTEM, systemIs24Hour = false, Locale.US, utc)
                .startsWith("2:05"),
        )
    }

    @Test
    fun timeZoneIsRespected() {
        // Asia/Kolkata is UTC+05:30.
        val text = ClockFormatter.timeText(instant, TimeFormat.H24, systemIs24Hour = true, Locale.US, kolkata)
        assertEquals("19:35", text)
    }

    @Test
    fun dateTextIsShortAndLocalised() {
        assertEquals("Thu, 15 Oct", ClockFormatter.dateText(instant, Locale.US, utc))
    }

    @Test
    fun millisUntilNextMinuteIsWithinOneMinute() {
        assertEquals(60_000L, ClockFormatter.millisUntilNextMinute(0L))
        assertEquals(1L, ClockFormatter.millisUntilNextMinute(59_999L))
        assertEquals(30_000L, ClockFormatter.millisUntilNextMinute(instant + 30_000L))
        for (now in listOf(1L, 12_345L, instant, instant + 59_999L)) {
            val wait = ClockFormatter.millisUntilNextMinute(now)
            assertTrue(wait in 1L..60_000L)
            assertEquals(0L, (now + wait) % 60_000L)
        }
    }
}
