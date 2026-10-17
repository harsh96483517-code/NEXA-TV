package com.nexatv.launcher.domain

import com.nexatv.launcher.domain.model.WallpaperCatalog
import com.nexatv.launcher.domain.model.WallpaperChoice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WallpaperChoiceTest {

    @Test
    fun builtInRoundTrips() {
        val choice = WallpaperChoice.BuiltIn("alpine")
        assertEquals(choice, WallpaperChoice.parse(choice.serialize()))
    }

    @Test
    fun imageUriRoundTripsEvenWhenItContainsColons() {
        val choice = WallpaperChoice.Image("content://com.android.providers.media.documents/document/image:42")
        assertEquals(choice, WallpaperChoice.parse(choice.serialize()))
    }

    @Test
    fun missingOrCorruptValuesFallBackToDefault() {
        assertEquals(WallpaperChoice.Default, WallpaperChoice.parse(null))
        assertEquals(WallpaperChoice.Default, WallpaperChoice.parse(""))
        assertEquals(WallpaperChoice.Default, WallpaperChoice.parse("garbage"))
        assertEquals(WallpaperChoice.Default, WallpaperChoice.parse("uri:"))
    }

    @Test
    fun defaultPresetExistsInCatalog() {
        assertTrue(WallpaperCatalog.presets.any { it.id == WallpaperCatalog.DEFAULT_ID })
    }

    @Test
    fun unknownPresetIdFallsBackToFirstPreset() {
        assertEquals(WallpaperCatalog.presets.first(), WallpaperCatalog.byId("does-not-exist"))
    }

    @Test
    fun presetIdsAreUnique() {
        val ids = WallpaperCatalog.presets.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }
}
