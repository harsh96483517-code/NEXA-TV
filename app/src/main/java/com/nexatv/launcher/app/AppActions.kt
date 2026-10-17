package com.nexatv.launcher.app

import com.nexatv.launcher.domain.model.AppEntry
import com.nexatv.launcher.platform.AppLauncher
import com.nexatv.launcher.platform.LaunchResult

/**
 * Single place that turns [LaunchResult]s into friendly messages, so a removed app, a missing
 * settings screen or a refused intent never crashes the launcher or fails silently.
 */
class AppActions(
    private val launcher: AppLauncher,
    private val messages: MessageBus,
) {

    fun launch(app: AppEntry) {
        report(launcher.launch(app)) { reason ->
            when (reason) {
                null -> "${app.label} isn't installed anymore."
                else -> "Couldn't open ${app.label}: $reason"
            }
        }
    }

    fun openAppInfo(app: AppEntry) {
        report(launcher.openAppInfo(app.packageName)) { reason ->
            "Couldn't open the app info screen for ${app.label}" + (reason?.let { ": $it" } ?: ".")
        }
    }

    fun openManageApps() {
        report(launcher.openManageApps()) { _ ->
            "This device has no Android app-management screen we can open."
        }
    }

    fun openSystemSettings() {
        report(launcher.openSystemSettings()) { _ ->
            "This device has no Android settings screen we can open."
        }
    }

    fun openHomeSettings() {
        report(launcher.openHomeSettings()) { _ ->
            "Open Android Settings > Apps > Default apps to choose the Home app."
        }
    }

    /** [errorText] receives the failure reason, or null when nothing could handle the request. */
    private fun report(result: LaunchResult, errorText: (reason: String?) -> String) {
        when (result) {
            LaunchResult.Success -> Unit
            LaunchResult.NotAvailable -> messages.post(errorText(null), isError = true)
            is LaunchResult.Failed -> messages.post(errorText(result.reason), isError = true)
        }
    }
}
