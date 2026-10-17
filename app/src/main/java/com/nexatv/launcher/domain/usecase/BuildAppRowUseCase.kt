package com.nexatv.launcher.domain.usecase

import com.nexatv.launcher.domain.model.AppEntry
import java.util.Locale

/**
 * Best-effort list of well-known streaming/media apps, in the order they should appear in the
 * "Your Apps" row. Matching is by package name first, then by an exact (case-insensitive) label.
 * Apps that are not installed are simply skipped: nothing is shown for them.
 */
object KnownApps {
    data class Brand(
        val name: String,
        val packages: List<String>,
        val exactLabels: List<String> = listOf(name),
    )

    val ordered: List<Brand> = listOf(
        Brand("YouTube", listOf("com.google.android.youtube.tv", "com.google.android.youtube")),
        Brand("Netflix", listOf("com.netflix.ninja", "com.netflix.mediaclient")),
        Brand(
            "Prime Video",
            listOf("com.amazon.amazonvideo.livingroom", "com.amazon.avod.thirdpartyclient"),
        ),
        Brand("Disney+", listOf("com.disney.disneyplus")),
        Brand("Spotify", listOf("com.spotify.tv.android", "com.spotify.music")),
        Brand(
            "JioHotstar",
            listOf("in.startv.hotstar.dplus.tv", "in.startv.hotstar", "in.startv.hotstar.dplus"),
            exactLabels = listOf("JioHotstar", "Disney+ Hotstar", "Hotstar"),
        ),
        Brand(
            "YouTube Kids",
            listOf("com.google.android.youtube.tvkids", "com.google.android.apps.youtube.kids"),
        ),
    )
}

/**
 * Builds the "Your Apps" home row: installed well-known apps first (in [KnownApps] order), then
 * other installed apps (TV apps first, then alphabetical) until [maxApps] is reached.
 * NEXA TV itself is never listed.
 */
class BuildAppRowUseCase(
    private val ownPackage: String,
    private val maxApps: Int = DEFAULT_MAX_APPS,
) {

    operator fun invoke(installed: List<AppEntry>): List<AppEntry> {
        val candidates = installed.filter { it.packageName != ownPackage }
        val picked = LinkedHashMap<String, AppEntry>()
        // Every package of a matched brand is "claimed", so e.g. the phone build of YouTube is not
        // listed a second time as filler after its TV build was picked.
        val claimedPackages = HashSet<String>()

        for (brand in KnownApps.ordered) {
            val match = findBrandMatch(brand, candidates) ?: continue
            picked.putIfAbsent(match.key, match)
            claimedPackages += brand.packages
        }

        val others = candidates
            .filter { it.key !in picked && it.packageName !in claimedPackages }
            .sortedWith(
                compareByDescending<AppEntry> { it.isTv }
                    .thenBy { it.label.lowercase(Locale.ROOT) },
            )
        for (app in others) {
            if (picked.size >= maxApps) break
            picked[app.key] = app
        }
        return picked.values.take(maxApps)
    }

    private fun findBrandMatch(brand: KnownApps.Brand, apps: List<AppEntry>): AppEntry? {
        // Earlier packages in the list are preferred (TV builds come before phone builds).
        for (pkg in brand.packages) {
            apps.firstOrNull { it.packageName == pkg }?.let { return it }
        }
        return apps.firstOrNull { app ->
            brand.exactLabels.any { it.equals(app.label, ignoreCase = true) }
        }
    }

    companion object {
        const val DEFAULT_MAX_APPS = 10
    }
}
