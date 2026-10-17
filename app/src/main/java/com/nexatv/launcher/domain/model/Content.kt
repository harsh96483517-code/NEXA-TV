package com.nexatv.launcher.domain.model

enum class ContentType { MOVIE, SHOW }

/** Visual motif used by the procedural artwork renderer. */
enum class ArtMotif { PLANET, RIDGES, CITY, FOREST, ICE, DESERT }

/**
 * Description of original, procedurally generated artwork. Colors are ARGB longs so the domain
 * layer stays free of Compose types.
 */
data class ArtSpec(
    val top: Long,
    val bottom: Long,
    val accent: Long,
    val motif: ArtMotif,
    val seed: Int,
)

data class ContentItem(
    val id: String,
    val title: String,
    val tagline: String,
    val description: String,
    val type: ContentType,
    val year: Int,
    val durationMinutes: Int,
    val rating: Float,
    val genres: List<String>,
    val art: ArtSpec,
    /** False only once a real content source is configured. Everything shipped today is sample data. */
    val isSample: Boolean = true,
)

/**
 * Resume information for an item. Only ever produced by a configured content source: NEXA TV cannot
 * read playback progress from other apps, so the bundled values are labelled as sample data.
 */
data class ContinueWatchingItem(
    val content: ContentItem,
    val episodeLabel: String?,
    val progress: Float,
    val remainingMinutes: Int,
) {
    val id: String get() = content.id
}
