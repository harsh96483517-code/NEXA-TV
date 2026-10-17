package com.nexatv.launcher.domain.repository

import com.nexatv.launcher.domain.model.AccentTheme
import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.domain.model.TimeFormat
import com.nexatv.launcher.domain.model.UserSettings
import com.nexatv.launcher.domain.model.WallpaperChoice
import kotlinx.coroutines.flow.Flow

interface AppsRepository {
    /** Emits the launchable apps now, and again whenever packages are installed/removed/updated. */
    fun observeApps(): Flow<List<AppEntry>>
}

interface SettingsRepository {
    val settings: Flow<UserSettings>
    suspend fun setWallpaper(choice: WallpaperChoice)
    suspend fun setAccent(accent: AccentTheme)
    suspend fun setTimeFormat(format: TimeFormat)
    suspend fun setSidebarCompact(compact: Boolean)
    suspend fun setReduceEffects(reduce: Boolean)
}
