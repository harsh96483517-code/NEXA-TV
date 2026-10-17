package com.nexatv.launcher.domain.model

/** A launchable application discovered through PackageManager. */
data class AppEntry(
    val packageName: String,
    val activityName: String,
    val label: String,
    /** True when the app exposes a leanback (TV) launcher activity. */
    val isTv: Boolean,
) {
    /** Stable identity for lazy-list keys and icon caching. */
    val key: String get() = "$packageName/$activityName"
}
