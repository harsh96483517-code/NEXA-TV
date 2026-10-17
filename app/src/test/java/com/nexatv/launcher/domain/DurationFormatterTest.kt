package com.nexatv.launcher.domain

import com.nexatv.launcher.domain.usecase.DurationFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class DurationFormatterTest {

    @Test
    fun minutesOnly() {
        assertEquals("0m", DurationFormatter.format(0))
        assertEquals("42m", DurationFormatter.format(42))
        assertEquals("59m", DurationFormatter.format(59))
    }

    @Test
    fun wholeHours() {
        assertEquals("1h", DurationFormatter.format(60))
        assertEquals("2h", DurationFormatter.format(120))
    }

    @Test
    fun hoursAndMinutes() {
        assertEquals("2h 10m", DurationFormatter.format(130))
        assertEquals("1h 1m", DurationFormatter.format(61))
    }

    @Test
    fun negativeValuesAreClampedToZero() {
        assertEquals("0m", DurationFormatter.format(-15))
    }
}
