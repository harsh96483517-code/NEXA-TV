package com.nexatv.launcher.domain.repository

import com.nexatv.launcher.domain.model.AccentTheme
import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.domain.model.ContentItem
import com.nexatv.launcher.domain.model.ContentType
import com.nexatv.launcher.domain.model.ContinueWatchingItem
import com.nexatv.launcher.domain.model.TimeFormat
import com.nexatv.launcher.domain.model.UserSettings
import com.nexatv.launcher.domain.model.WallpaperChoice
import kotlinx.coroutines.flow.Flow

interface AppsRepository {
    /** Emits the launchable apps now, and again whenever packages are installed/removed/updated. */
    fun observeApps(): Flow<List<AppEntry>>
}

interface ContentRepository {
    suspend fun featured(): List<ContentItem>
    suspend fun continueWatching(): List<ContinueWatchingItem>
    suspend fun trending(): List<ContentItem>
    suspend fun catalog(type: ContentType?): List<ContentItem>
    suspend fun byId(id: String): ContentItem?
    suspend fun progressFor(id: String): ContinueWatchingItem?
}

interface SettingsRepository {
    val settings: Flow<UserSettings>
    suspend fun setWallpaper(choice: WallpaperChoice)
    suspend fun setAccent(accent: AccentTheme)
    suspend fun setTimeFormat(format: TimeFormat)
    suspend fun setSidebarCompact(compact: Boolean)
    suspend fun setReduceEffects(reduce: Boolean)
    suspend fun toggleLibrary(contentId: String)
}
