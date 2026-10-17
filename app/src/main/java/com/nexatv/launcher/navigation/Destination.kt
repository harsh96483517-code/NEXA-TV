package com.nexatv.launcher.navigation

/** Every screen in the launcher. Home is the Apps grid; the rest are opened from Settings. */
sealed interface Destination {
    data object Home : Destination
    data object Wallpaper : Destination
    data object Theme : Destination
    data object About : Destination

    val isRoot: Boolean
        get() = this == Home
}
