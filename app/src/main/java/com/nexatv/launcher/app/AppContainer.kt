package com.nexatv.launcher.app

import android.content.Context
import com.nexatv.launcher.data.InstalledAppsRepository
import com.nexatv.launcher.data.SampleContentRepository
import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.domain.repository.AppsRepository
import com.nexatv.launcher.domain.repository.ContentRepository
import com.nexatv.launcher.domain.repository.SettingsRepository
import com.nexatv.launcher.domain.usecase.BuildAppRowUseCase
import com.nexatv.launcher.domain.usecase.SearchAppsUseCase
import com.nexatv.launcher.domain.usecase.SearchContentUseCase
import com.nexatv.launcher.platform.AppIconLoader
import com.nexatv.launcher.platform.AppLauncher
import com.nexatv.launcher.platform.DefaultHomeController
import com.nexatv.launcher.platform.InstalledAppsSource
import com.nexatv.launcher.platform.LocalWallpaperSource
import com.nexatv.launcher.platform.NetworkMonitor
import com.nexatv.launcher.platform.PackageChanges
import com.nexatv.launcher.settings.DataStoreSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Hand-rolled dependency container (no DI framework needed for an app this size). Created once by
 * [NexaApplication]; ViewModels receive it through a factory.
 */
class AppContainer(context: Context) {

    private val appContext: Context = context.applicationContext

    /** NEXA TV's own package; it is excluded from app lists and searches. */
    val ownPackage: String = appContext.packageName

    val messages = MessageBus()

    val settingsRepository: SettingsRepository = DataStoreSettingsRepository(appContext)
    val contentRepository: ContentRepository = SampleContentRepository()
    val appsRepository: AppsRepository =
        InstalledAppsRepository(InstalledAppsSource(appContext), PackageChanges(appContext))

    val appLauncher = AppLauncher(appContext)
    val appActions = AppActions(appLauncher, messages)
    val iconLoader = AppIconLoader(appContext)
    val networkMonitor = NetworkMonitor(appContext)
    val wallpaperSource = LocalWallpaperSource(appContext)
    val defaultHomeController = DefaultHomeController(appContext)

    val buildAppRow = BuildAppRowUseCase(ownPackage = ownPackage)
    val searchApps = SearchAppsUseCase()
    val searchContent = SearchContentUseCase()

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Installed launchable apps, shared by every screen. `null` means "still loading". It keeps
     * itself current through package-change broadcasts while any screen is observing it.
     */
    val installedApps: StateFlow<List<AppEntry>?> = appsRepository.observeApps()
        .map<List<AppEntry>, List<AppEntry>?> { it }
        .catch {
            messages.post("Couldn't read the list of installed apps.", isError = true)
            emit(emptyList())
        }
        .stateIn(appScope, SharingStarted.WhileSubscribed(5_000), null)

    private val mutableIsDefaultHome = MutableStateFlow(false)
    val isDefaultHome: StateFlow<Boolean> = mutableIsDefaultHome.asStateFlow()

    private val mutableHomePressed = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** Emits when the system Home button is pressed while NEXA TV is already in front. */
    val homePressed: SharedFlow<Unit> = mutableHomePressed.asSharedFlow()

    fun refreshDefaultHome() {
        mutableIsDefaultHome.value = runCatching { defaultHomeController.isDefaultHome() }.getOrDefault(false)
    }

    fun notifyHomePressed() {
        mutableHomePressed.tryEmit(Unit)
    }
}
