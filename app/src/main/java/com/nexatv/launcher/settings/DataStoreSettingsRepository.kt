package com.nexatv.launcher.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nexatv.launcher.domain.model.AccentTheme
import com.nexatv.launcher.domain.model.TimeFormat
import com.nexatv.launcher.domain.model.UserSettings
import com.nexatv.launcher.domain.model.WallpaperChoice
import com.nexatv.launcher.domain.repository.SettingsRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private val Context.nexaDataStore: DataStore<Preferences> by preferencesDataStore(name = "nexa_settings")

private object Keys {
    val WALLPAPER = stringPreferencesKey("wallpaper")
    val ACCENT = stringPreferencesKey("accent")
    val TIME_FORMAT = stringPreferencesKey("time_format")
    val SIDEBAR_COMPACT = booleanPreferencesKey("sidebar_compact")
    val REDUCE_EFFECTS = booleanPreferencesKey("reduce_effects")
}

/** Persists interface settings locally with Jetpack DataStore (Preferences). */
class DataStoreSettingsRepository(context: Context) : SettingsRepository {

    private val store: DataStore<Preferences> = context.applicationContext.nexaDataStore

    override val settings: Flow<UserSettings> = store.data
        // A corrupt or unreadable file falls back to defaults instead of crashing the launcher.
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { it.toSettings() }
        .distinctUntilChanged()

    override suspend fun setWallpaper(choice: WallpaperChoice) {
        store.edit { it[Keys.WALLPAPER] = choice.serialize() }
    }

    override suspend fun setAccent(accent: AccentTheme) {
        store.edit { it[Keys.ACCENT] = accent.name }
    }

    override suspend fun setTimeFormat(format: TimeFormat) {
        store.edit { it[Keys.TIME_FORMAT] = format.name }
    }

    override suspend fun setSidebarCompact(compact: Boolean) {
        store.edit { it[Keys.SIDEBAR_COMPACT] = compact }
    }

    override suspend fun setReduceEffects(reduce: Boolean) {
        store.edit { it[Keys.REDUCE_EFFECTS] = reduce }
    }

    private fun Preferences.toSettings() = UserSettings(
        wallpaper = WallpaperChoice.parse(this[Keys.WALLPAPER]),
        accent = parseEnum(this[Keys.ACCENT], AccentTheme.AURORA),
        timeFormat = parseEnum(this[Keys.TIME_FORMAT], TimeFormat.SYSTEM),
        sidebarCompact = this[Keys.SIDEBAR_COMPACT] ?: false,
        reduceEffects = this[Keys.REDUCE_EFFECTS] ?: true,
    )

    private inline fun <reified T : Enum<T>> parseEnum(raw: String?, default: T): T =
        raw?.let { name -> enumValues<T>().firstOrNull { it.name == name } } ?: default
}
