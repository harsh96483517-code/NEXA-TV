package com.nexatv.launcher.domain.model

/** Visual motif used by the procedural wallpaper renderer. */
enum class ArtMotif { PLANET, RIDGES, ICE, DESERT }

/**
 * Description of an original, procedurally generated wallpaper. Colors are ARGB longs so the domain
 * layer stays free of Compose types.
 */
data class ArtSpec(
    val top: Long,
    val bottom: Long,
    val accent: Long,
    val motif: ArtMotif,
    val seed: Int,
)
