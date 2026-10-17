package com.nexatv.launcher.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexatv.launcher.app.AppContainer
import com.nexatv.launcher.domain.model.AppEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** All installed launchable apps (without Harsh Reality itself). `null` = still loading. */
class AppsViewModel(private val container: AppContainer) : ViewModel() {

    val apps: StateFlow<List<AppEntry>?> = container.installedApps
        .map { installed -> installed?.filter { it.packageName != container.ownPackage } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun launch(app: AppEntry) = container.appActions.launch(app)

    fun openAppInfo(app: AppEntry) = container.appActions.openAppInfo(app)
}
