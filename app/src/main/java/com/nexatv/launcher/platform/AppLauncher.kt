package com.nexatv.launcher.platform

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.nexatv.launcher.domain.model.AppEntry

sealed interface LaunchResult {
    data object Success : LaunchResult

    /** Nothing on the device can handle the intent (app uninstalled, no such settings screen...). */
    data object NotAvailable : LaunchResult

    data class Failed(val reason: String) : LaunchResult
}

/**
 * Starts apps and system screens. Every call returns a [LaunchResult] instead of throwing, so the
 * UI can show a friendly message when an app was removed or a settings screen does not exist on
 * this TV build.
 */
class AppLauncher(private val context: Context) {

    fun launch(app: AppEntry): LaunchResult {
        val category = if (app.isTv) Intent.CATEGORY_LEANBACK_LAUNCHER else Intent.CATEGORY_LAUNCHER
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(category)
            .setComponent(ComponentName(app.packageName, app.activityName))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        return start(intent)
    }

    fun openAppInfo(packageName: String): LaunchResult = start(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)),
    )

    /** Android's own "Apps" management screen, falling back to the main Settings screen. */
    fun openManageApps(): LaunchResult = startFirst(
        Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS),
        Intent(Settings.ACTION_SETTINGS),
    )

    fun openSystemSettings(): LaunchResult = start(Intent(Settings.ACTION_SETTINGS))

    /** Settings screen where the default Home app is chosen (not present on every TV build). */
    fun openHomeSettings(): LaunchResult = startFirst(
        Intent(Settings.ACTION_HOME_SETTINGS),
        Intent(Settings.ACTION_SETTINGS),
    )

    private fun startFirst(vararg intents: Intent): LaunchResult {
        var last: LaunchResult = LaunchResult.NotAvailable
        for (intent in intents) {
            last = start(intent)
            if (last is LaunchResult.Success) return last
        }
        return last
    }

    private fun start(intent: Intent): LaunchResult = try {
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        LaunchResult.Success
    } catch (e: ActivityNotFoundException) {
        LaunchResult.NotAvailable
    } catch (e: SecurityException) {
        LaunchResult.Failed("Android refused to open it")
    } catch (e: RuntimeException) {
        LaunchResult.Failed(e.message ?: "Unknown error")
    }
}
