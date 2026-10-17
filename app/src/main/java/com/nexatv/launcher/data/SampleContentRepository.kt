package com.nexatv.launcher.data

import com.nexatv.launcher.domain.model.ArtMotif
import com.nexatv.launcher.domain.model.ArtSpec
import com.nexatv.launcher.domain.model.ContentItem
import com.nexatv.launcher.domain.model.ContentType
import com.nexatv.launcher.domain.model.ContinueWatchingItem
import com.nexatv.launcher.domain.repository.ContentRepository

/**
 * Bundled sample content. Every title here is invented and every image is generated procedurally,
 * so no third-party artwork is redistributed. Replace this class with a real source (a Google TV /
 * Watch Next integration, a server, ...) by implementing [ContentRepository].
 */
class SampleContentRepository : ContentRepository {

    private val solarReign = movie(
        id = "solar-reign", title = "Solar Reign", tagline = "Two suns. One throne.",
        description = "When a desert world's twin suns begin to fail, two exiled heirs must cross " +
            "a fractured empire to reunite the houses that once ruled it.",
        year = 2025, minutes = 158, rating = 8.7f, genres = listOf("Adventure", "Sci-Fi"),
        art = ArtSpec(0xFF0B0F2E, 0xFF7A3B1E, 0xFFFFB347, ArtMotif.PLANET, 3),
    )
    private val glassMeridian = movie(
        id = "glass-meridian", title = "Glass Meridian", tagline = "Every city keeps a secret.",
        description = "A night-shift cartographer discovers that the skyline she maps changes " +
            "whenever someone disappears.",
        year = 2024, minutes = 124, rating = 8.2f, genres = listOf("Thriller", "Mystery"),
        art = ArtSpec(0xFF080B24, 0xFF1E3F7A, 0xFF4CE0FF, ArtMotif.CITY, 7),
    )
    private val quietSummit = movie(
        id = "quiet-summit", title = "The Quiet Summit", tagline = "Some climbs change you.",
        description = "Three strangers share a single rope on an unmapped Himalayan ridge and " +
            "learn what they are willing to leave behind.",
        year = 2023, minutes = 112, rating = 7.9f, genres = listOf("Adventure", "Drama"),
        art = ArtSpec(0xFF0A1330, 0xFF32507A, 0xFFBFE6FF, ArtMotif.RIDGES, 12),
    )
    private val neonHarbor = show(
        id = "neon-harbor", title = "Neon Harbor", tagline = "The tide brings everything in.",
        description = "A harbour inspector inherits a ledger that links the city's brightest " +
            "towers to its darkest docks.",
        year = 2025, minutes = 48, rating = 8.4f, genres = listOf("Crime", "Drama"),
        art = ArtSpec(0xFF12062B, 0xFF5A1E7A, 0xFFFF5CC8, ArtMotif.CITY, 19),
    )

    private val ashfall = show(
        id = "ashfall-protocol", title = "Ashfall Protocol", tagline = "Rebuild or repeat.",
        description = "Survivors of a buried vault network return to a ruined surface with one " +
            "rule: leave nothing the way you found it.",
        year = 2024, minutes = 60, rating = 8.5f, genres = listOf("Sci-Fi", "Drama"),
        art = ArtSpec(0xFF10120A, 0xFF4A5A2A, 0xFFB8E060, ArtMotif.FOREST, 24),
    )
    private val wasteland = show(
        id = "wasteland-run", title = "Wasteland Run", tagline = "Outrun the horizon.",
        description = "A courier crew crosses a lawless salt flat with a cargo nobody will name.",
        year = 2024, minutes = 52, rating = 8.0f, genres = listOf("Action", "Adventure"),
        art = ArtSpec(0xFF1A0E08, 0xFF7A4A1E, 0xFFFFC266, ArtMotif.DESERT, 31),
    )
    private val blueLantern = show(
        id = "blue-lantern", title = "Blue Lantern", tagline = "Light the way home.",
        description = "A student with a borrowed lantern walks between worlds to find her " +
            "missing sister.",
        year = 2023, minutes = 24, rating = 8.6f, genres = listOf("Animation", "Fantasy"),
        art = ArtSpec(0xFF050E2E, 0xFF1A3C9A, 0xFF5CB8FF, ArtMotif.PLANET, 41),
    )
    private val frozenOrbit = movie(
        id = "frozen-orbit", title = "Frozen Orbit", tagline = "Silence is the last signal.",
        description = "A lone astronaut on an ice-bound relay station hears a transmission from " +
            "a crew that was never launched.",
        year = 2022, minutes = 173, rating = 8.3f, genres = listOf("Sci-Fi", "Drama"),
        art = ArtSpec(0xFF0A1424, 0xFF3A5A7A, 0xFFE0F4FF, ArtMotif.ICE, 47),
    )

    private val trendingItems = listOf(
        movie(
            id = "night-courier", title = "Night Courier", tagline = "Deliver. Don't ask.",
            description = "A bike messenger takes one last job across a city on lockdown.",
            year = 2025, minutes = 101, rating = 7.6f, genres = listOf("Action", "Thriller"),
            art = ArtSpec(0xFF08061E, 0xFF3A1E7A, 0xFFFF7A5C, ArtMotif.CITY, 52),
        ),
        show(
            id = "verdant", title = "Verdant", tagline = "Nature keeps score.",
            description = "Botanists in a sealed arboretum realise the plants are watching back.",
            year = 2025, minutes = 45, rating = 8.1f, genres = listOf("Mystery", "Sci-Fi"),
            art = ArtSpec(0xFF04140E, 0xFF1E5A3A, 0xFF7CF0A8, ArtMotif.FOREST, 58),
        ),
        movie(
            id = "paper-moons", title = "Paper Moons", tagline = "Wish carefully.",
            description = "Two siblings fold a paper moon that begins to answer their wishes.",
            year = 2024, minutes = 96, rating = 7.8f, genres = listOf("Family", "Fantasy"),
            art = ArtSpec(0xFF0C1030, 0xFF4A4A9A, 0xFFFFE08A, ArtMotif.PLANET, 63),
        ),
        show(
            id = "iron-tide", title = "Iron Tide", tagline = "The sea remembers.",
            description = "A salvage fleet raises a warship that was never meant to surface.",
            year = 2024, minutes = 55, rating = 8.2f, genres = listOf("Drama", "War"),
            art = ArtSpec(0xFF060E18, 0xFF1E4A5A, 0xFF6CD0E0, ArtMotif.RIDGES, 68),
        ),
        movie(
            id = "echo-valley", title = "Echo Valley", tagline = "Say it once.",
            description = "A sound engineer maps a canyon whose echoes arrive before the voice.",
            year = 2023, minutes = 108, rating = 7.7f, genres = listOf("Drama", "Mystery"),
            art = ArtSpec(0xFF140A18, 0xFF6A3A5A, 0xFFFFA8D8, ArtMotif.DESERT, 74),
        ),
        show(
            id = "starlight-run", title = "Starlight Run", tagline = "Race the dawn.",
            description = "Rival pilots chase a comet through a ring of derelict stations.",
            year = 2025, minutes = 42, rating = 8.0f, genres = listOf("Sci-Fi", "Action"),
            art = ArtSpec(0xFF070820, 0xFF2A2A8A, 0xFF9C8CFF, ArtMotif.PLANET, 79),
        ),
        movie(
            id = "crimson-atlas", title = "Crimson Atlas", tagline = "Every map has an edge.",
            description = "A cartographer's final atlas leads an expedition off the known world.",
            year = 2022, minutes = 131, rating = 7.9f, genres = listOf("Adventure", "History"),
            art = ArtSpec(0xFF1A0808, 0xFF7A2A2A, 0xFFFF9A7A, ArtMotif.RIDGES, 84),
        ),
        show(
            id = "hollow-peaks", title = "Hollow Peaks", tagline = "Winter has a pulse.",
            description = "A mountain rescue team investigates calls from a village buried by snow.",
            year = 2025, minutes = 50, rating = 8.3f, genres = listOf("Thriller", "Drama"),
            art = ArtSpec(0xFF0A1220, 0xFF3A5070, 0xFFD0E8FF, ArtMotif.ICE, 89),
        ),
    )

    private val featuredItems = listOf(solarReign, glassMeridian, quietSummit, neonHarbor)

    private val continueItems = listOf(
        ContinueWatchingItem(ashfall, "S1 E5", progress = 0.30f, remainingMinutes = 42),
        ContinueWatchingItem(wasteland, "S1 E2", progress = 0.31f, remainingMinutes = 36),
        ContinueWatchingItem(blueLantern, "S1 E12", progress = 0.75f, remainingMinutes = 18),
        ContinueWatchingItem(frozenOrbit, null, progress = 0.25f, remainingMinutes = 130),
    )

    private val allItems: List<ContentItem> =
        (featuredItems + continueItems.map { it.content } + trendingItems).distinctBy { it.id }

    override suspend fun featured(): List<ContentItem> = featuredItems

    override suspend fun continueWatching(): List<ContinueWatchingItem> = continueItems

    override suspend fun trending(): List<ContentItem> = trendingItems

    override suspend fun catalog(type: ContentType?): List<ContentItem> =
        if (type == null) allItems else allItems.filter { it.type == type }

    override suspend fun byId(id: String): ContentItem? = allItems.firstOrNull { it.id == id }

    override suspend fun progressFor(id: String): ContinueWatchingItem? =
        continueItems.firstOrNull { it.id == id }

    private fun movie(
        id: String, title: String, tagline: String, description: String, year: Int,
        minutes: Int, rating: Float, genres: List<String>, art: ArtSpec,
    ) = ContentItem(id, title, tagline, description, ContentType.MOVIE, year, minutes, rating, genres, art)

    private fun show(
        id: String, title: String, tagline: String, description: String, year: Int,
        minutes: Int, rating: Float, genres: List<String>, art: ArtSpec,
    ) = ContentItem(id, title, tagline, description, ContentType.SHOW, year, minutes, rating, genres, art)
}
