package com.nexatv.launcher.navigation

/** Every screen in the launcher. Root destinations are reachable from the sidebar / top bar. */
sealed interface Destination {
    data object Home : Destination
    data object Search : Destination
    data object Movies : Destination
    data object TvShows : Destination
    data object Apps : Destination
    data object Library : Destination
    data object Wallpaper : Destination
    data object Theme : Destination
    data object About : Destination
    data class Details(val contentId: String) : Destination

    val isRoot: Boolean
        get() = when (this) {
            Home, Search, Movies, TvShows, Apps, Library -> true
            else -> false
        }
}
