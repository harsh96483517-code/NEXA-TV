package com.nexatv.launcher.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexatv.launcher.app.AppContainer
import com.nexatv.launcher.domain.model.AccentTheme
import com.nexatv.launcher.domain.model.TimeFormat
import com.nexatv.launcher.domain.model.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** User preferences and the system shortcuts reachable from Quick Settings / About. */
class SettingsViewModel(private val container: AppContainer) : ViewModel() {

    /** `null` until DataStore has produced its first value, so the UI never flashes defaults. */
    val settings: StateFlow<UserSettings?> = container.settingsRepository.settings
        .map<UserSettings, UserSettings?> { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isDefaultHome: StateFlow<Boolean> = container.isDefaultHome

    fun setAccent(accent: AccentTheme) = runAsync { container.settingsRepository.setAccent(accent) }

    fun setSidebarCompact(compact: Boolean) = runAsync { container.settingsRepository.setSidebarCompact(compact) }

    fun setReduceEffects(reduce: Boolean) = runAsync { container.settingsRepository.setReduceEffects(reduce) }

    fun cycleTimeFormat() {
        val current = settings.value?.timeFormat ?: return
        val entries = TimeFormat.entries
        val next = entries[(current.ordinal + 1) % entries.size]
        runAsync {
            container.settingsRepository.setTimeFormat(next)
            container.messages.post("Time format: ${next.label}")
        }
    }

    fun refreshDefaultHome() = container.refreshDefaultHome()

    fun openManageApps() = container.appActions.openManageApps()

    fun openSystemSettings() = container.appActions.openSystemSettings()

    fun openHomeSettings() = container.appActions.openHomeSettings()

    private fun runAsync(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
