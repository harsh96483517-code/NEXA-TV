package com.nexatv.launcher.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Minimal back stack. Root destinations (sidebar / top bar) always sit directly above Home so that
 * Back from any root screen returns to Home; detail screens push on top of whatever is below.
 */
class NexaNavigator {

    private val stack = mutableStateListOf<Destination>(Destination.Home)

    /** Quick-settings overlay. Back closes it before anything else. */
    var quickSettingsOpen by mutableStateOf(false)
        private set

    val current: Destination get() = stack.last()

    /** The root destination the current screen belongs to (used to highlight nav items). */
    val currentRoot: Destination
        get() = stack.lastOrNull { it.isRoot } ?: Destination.Home

    val canGoBack: Boolean get() = quickSettingsOpen || stack.size > 1

    fun openRoot(destination: Destination) {
        require(destination.isRoot) { "$destination is not a root destination" }
        quickSettingsOpen = false
        if (destination == current) return
        stack.clear()
        stack.add(Destination.Home)
        if (destination != Destination.Home) stack.add(destination)
    }

    fun push(destination: Destination) {
        quickSettingsOpen = false
        if (destination == current) return
        stack.add(destination)
    }

    fun openQuickSettings() {
        quickSettingsOpen = true
    }

    fun closeQuickSettings() {
        quickSettingsOpen = false
    }

    /** Returns true if something was closed or popped. */
    fun back(): Boolean {
        if (quickSettingsOpen) {
            quickSettingsOpen = false
            return true
        }
        if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
            return true
        }
        return false
    }

    /** Home button pressed: close overlays and return to the Home screen. */
    fun goHome() {
        quickSettingsOpen = false
        stack.clear()
        stack.add(Destination.Home)
    }
}
