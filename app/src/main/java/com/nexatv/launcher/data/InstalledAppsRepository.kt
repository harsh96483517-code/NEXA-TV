package com.nexatv.launcher.data

import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.domain.repository.AppsRepository
import com.nexatv.launcher.platform.InstalledAppsSource
import com.nexatv.launcher.platform.PackageChanges
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/** Discovers launchable apps via PackageManager and refreshes when packages change. */
class InstalledAppsRepository(
    private val source: InstalledAppsSource,
    private val packageChanges: PackageChanges,
) : AppsRepository {

    override fun observeApps(): Flow<List<AppEntry>> =
        packageChanges.events()
            .onStart { emit(Unit) }
            .conflate() // bursts of install/update broadcasts collapse into a single reload
            .map { source.load() }
            .flowOn(Dispatchers.IO)
}
