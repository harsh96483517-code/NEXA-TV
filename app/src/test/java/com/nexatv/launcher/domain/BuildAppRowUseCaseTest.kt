package com.nexatv.launcher.domain

import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.domain.usecase.BuildAppRowUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildAppRowUseCaseTest {

    private val own = "com.nexatv.launcher"

    private fun app(pkg: String, label: String, tv: Boolean = true) =
        AppEntry(packageName = pkg, activityName = "$pkg.Main", label = label, isTv = tv)

    @Test
    fun knownAppsComeFirstInBrandOrder() {
        val installed = listOf(
            app("com.zzz.tool", "Zeta Tool"),
            app("com.spotify.tv.android", "Spotify"),
            app("com.netflix.ninja", "Netflix"),
            app("com.google.android.youtube.tv", "YouTube"),
        )
        val row = BuildAppRowUseCase(own)(installed)
        assertEquals(
            listOf("YouTube", "Netflix", "Spotify", "Zeta Tool"),
            row.map { it.label },
        )
    }

    @Test
    fun ownPackageIsNeverListed() {
        val installed = listOf(app(own, "NEXA TV"), app("com.other", "Other"))
        val row = BuildAppRowUseCase(own)(installed)
        assertEquals(listOf("Other"), row.map { it.label })
    }

    @Test
    fun missingBrandsAreSkippedNotFaked() {
        val row = BuildAppRowUseCase(own)(listOf(app("com.other", "Other")))
        assertEquals(1, row.size)
        assertFalse(row.any { it.label == "Netflix" })
    }

    @Test
    fun tvBuildWinsAndPhoneBuildIsNotListedAgain() {
        val installed = listOf(
            app("com.google.android.youtube", "YouTube", tv = false),
            app("com.google.android.youtube.tv", "YouTube"),
        )
        val row = BuildAppRowUseCase(own)(installed)
        assertEquals(listOf("com.google.android.youtube.tv"), row.map { it.packageName })
    }

    @Test
    fun brandIsMatchedByExactLabelWhenPackageIsUnknown() {
        val installed = listOf(app("in.example.streaming", "Hotstar"))
        val row = BuildAppRowUseCase(own)(installed)
        assertEquals(listOf("Hotstar"), row.map { it.label })
    }

    @Test
    fun fillersPreferTvAppsThenAlphabetical() {
        val installed = listOf(
            app("com.b", "Beta", tv = false),
            app("com.a", "Alpha", tv = false),
            app("com.d", "Delta", tv = true),
            app("com.c", "Charlie", tv = true),
        )
        val row = BuildAppRowUseCase(own)(installed)
        assertEquals(listOf("Charlie", "Delta", "Alpha", "Beta"), row.map { it.label })
    }

    @Test
    fun rowIsCappedAtMaxApps() {
        val installed = (1..30).map { app("com.filler.$it", "App %02d".format(it)) }
        val row = BuildAppRowUseCase(own, maxApps = 6)(installed)
        assertEquals(6, row.size)
        assertTrue(row.map { it.label } == row.map { it.label }.sorted())
    }
}
