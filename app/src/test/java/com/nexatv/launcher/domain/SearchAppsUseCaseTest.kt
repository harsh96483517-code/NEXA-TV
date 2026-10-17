package com.nexatv.launcher.domain

import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.domain.usecase.SearchAppsUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchAppsUseCaseTest {

    private val search = SearchAppsUseCase()

    private fun app(label: String, pkg: String = "com.example.${label.lowercase().filter { it.isLetter() }}") =
        AppEntry(packageName = pkg, activityName = "$pkg.Main", label = label, isTv = true)

    private val apps = listOf(
        app("YouTube"),
        app("YouTube Kids"),
        app("Netflix"),
        app("Prime Video"),
        app("Spotify"),
        app("My Files", pkg = "com.vendor.files"),
        app("Photos", pkg = "com.vendor.netgallery"),
    )

    @Test
    fun blankQueryReturnsNothing() {
        assertTrue(search(apps, "").isEmpty())
        assertTrue(search(apps, "   ").isEmpty())
    }

    @Test
    fun matchingIsCaseInsensitiveAndTrimmed() {
        assertEquals(listOf("Netflix"), search(apps, "  NETFLIX ").map { it.label })
    }

    @Test
    fun prefixMatchesComeFirstAndTiesAreAlphabetical() {
        assertEquals(listOf("YouTube", "YouTube Kids"), search(apps, "you").map { it.label })
    }

    @Test
    fun wordPrefixBeatsPlainContains() {
        // "video" starts a word in "Prime Video"; "files" only appears inside the package of nothing else.
        val result = search(apps, "vid").map { it.label }
        assertEquals(listOf("Prime Video"), result)
    }

    @Test
    fun labelMatchBeatsPackageMatch() {
        // "net" is a label prefix for Netflix and only a package substring for Photos.
        assertEquals(listOf("Netflix", "Photos"), search(apps, "net").map { it.label })
    }

    @Test
    fun unknownQueryReturnsNothing() {
        assertTrue(search(apps, "zzzz").isEmpty())
    }
}
