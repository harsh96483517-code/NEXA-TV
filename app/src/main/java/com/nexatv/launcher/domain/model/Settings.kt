package com.nexatv.launcher.domain.model

enum class TimeFormat(val label: String) {
    SYSTEM("Follow system"),
    H12("12-hour"),
    H24("24-hour"),
}

/** Gradient accent families. Colors are resolved in the UI theme layer. */
enum class AccentTheme(val label: String) {
    AURORA("Aurora"),
    OCEAN("Ocean"),
    NEBULA("Nebula"),
    EMBER("Ember"),
}

/** Built-in wallpapers are drawn procedurally, so they cost no storage and no network. */
data class WallpaperPreset(
    val id: String,
    val name: String,
    val art: ArtSpec,
)

/** Either a built-in preset or an image the user picked / copied onto the device. */
sealed interface WallpaperChoice {
    data class BuiltIn(val id: String) : WallpaperChoice
    data class Image(val uri: String) : WallpaperChoice

    /** Serialised form stored in DataStore. */
    fun serialize(): String = when (this) {
        is BuiltIn -> "builtin:$id"
        is Image -> "uri:$uri"
    }

    companion object {
        val Default: WallpaperChoice = BuiltIn(WallpaperCatalog.DEFAULT_ID)

        fun parse(raw: String?): WallpaperChoice = when {
            raw == null -> Default
            raw.startsWith("builtin:") -> BuiltIn(raw.removePrefix("builtin:"))
            raw.startsWith("uri:") && raw.length > 4 -> Image(raw.removePrefix("uri:"))
            else -> Default
        }
    }
}

object WallpaperCatalog {
    const val DEFAULT_ID = "orbit"

    val presets: List<WallpaperPreset> = listOf(
        WallpaperPreset(
            id = "orbit",
            name = "Orbit Rise",
            art = ArtSpec(0xFF050A1F, 0xFF3B1E5E, 0xFFFFB347, ArtMotif.PLANET, seed = 11),
        ),
        WallpaperPreset(
            id = "alpine",
            name = "Alpine Night",
            art = ArtSpec(0xFF060B1E, 0xFF14305A, 0xFF7FD6FF, ArtMotif.RIDGES, seed = 23),
        ),
        WallpaperPreset(
            id = "nebula",
            name = "Deep Nebula",
            art = ArtSpec(0xFF0A0420, 0xFF3A1070, 0xFFB46CFF, ArtMotif.ICE, seed = 37),
        ),
        WallpaperPreset(
            id = "ember",
            name = "Ember Dunes",
            art = ArtSpec(0xFF140716, 0xFF6A2036, 0xFFFF8A4C, ArtMotif.DESERT, seed = 53),
        ),
    )

    fun byId(id: String): WallpaperPreset =
        presets.firstOrNull { it.id == id } ?: presets.first()
}

data class UserSettings(
    val wallpaper: WallpaperChoice = WallpaperChoice.Default,
    val accent: AccentTheme = AccentTheme.AURORA,
    val timeFormat: TimeFormat = TimeFormat.SYSTEM,
    /** When true the sidebar collapses to icons and expands only while it has focus. */
    val sidebarCompact: Boolean = false,
    /** Turns off focus and screen animations. On by default so older TVs stay smooth. */
    val reduceEffects: Boolean = true,
)
