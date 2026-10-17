package com.nexatv.launcher.platform

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import com.nexatv.launcher.domain.model.AppEntry
import java.util.Locale

/**
 * Lists launchable apps through the documented PackageManager query APIs. Visibility is granted by
 * the `<queries>` block in the manifest, so no QUERY_ALL_PACKAGES permission is needed.
 *
 * Call from a background dispatcher: querying and loading labels touches disk.
 */
class InstalledAppsSource(private val context: Context) {

    fun load(): List<AppEntry> {
        val pm = context.packageManager
        val byPackage = LinkedHashMap<String, AppEntry>()

        // Leanback activities are queried first so a TV activity wins over a phone activity
        // of the same package.
        val queries = listOf(
            Intent.CATEGORY_LEANBACK_LAUNCHER to true,
            Intent.CATEGORY_LAUNCHER to false,
        )
        for ((category, isTv) in queries) {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(category)
            for (info in query(pm, intent)) {
                val activity = info.activityInfo ?: continue
                if (byPackage.containsKey(activity.packageName)) continue
                val label = runCatching { info.loadLabel(pm).toString() }
                    .getOrNull()
                    ?.takeIf { it.isNotBlank() }
                    ?: activity.packageName
                byPackage[activity.packageName] = AppEntry(
                    packageName = activity.packageName,
                    activityName = activity.name,
                    label = label,
                    isTv = isTv,
                )
            }
        }
        return byPackage.values.sortedBy { it.label.lowercase(Locale.ROOT) }
    }

    @Suppress("DEPRECATION")
    private fun query(pm: PackageManager, intent: Intent): List<ResolveInfo> =
        if (Build.VERSION.SDK_INT >= 33) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            pm.queryIntentActivities(intent, 0)
        }
}
