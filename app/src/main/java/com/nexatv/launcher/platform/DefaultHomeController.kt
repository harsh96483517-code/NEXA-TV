package com.nexatv.launcher.platform

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

/**
 * Helps the user pick NEXA TV as their Home app through Android's supported flows. An app cannot
 * make itself the default launcher: it can only ask, and the user decides.
 *
 * - API 29+: RoleManager's "Home" role request (a system dialog the user confirms).
 * - Everything else (and TV builds that do not offer the role dialog): the system's Home settings,
 *   or Settings, where the user chooses the default Home app.
 */
class DefaultHomeController(private val context: Context) {

    fun isDefaultHome(): Boolean {
        if (Build.VERSION.SDK_INT >= 29) {
            val roles = context.getSystemService(RoleManager::class.java)
            if (roles != null && roles.isRoleAvailable(RoleManager.ROLE_HOME)) {
                return roles.isRoleHeld(RoleManager.ROLE_HOME)
            }
        }
        return resolveHomePackage() == context.packageName
    }

    /** Intent for the system role-request dialog, or null when it is unavailable / already held. */
    fun roleRequestIntent(): Intent? {
        if (Build.VERSION.SDK_INT < 29) return null
        val roles = context.getSystemService(RoleManager::class.java) ?: return null
        if (!roles.isRoleAvailable(RoleManager.ROLE_HOME) || roles.isRoleHeld(RoleManager.ROLE_HOME)) {
            return null
        }
        return roles.createRequestRoleIntent(RoleManager.ROLE_HOME)
    }

    @Suppress("DEPRECATION")
    private fun resolveHomePackage(): String? {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val pm = context.packageManager
        val info = if (Build.VERSION.SDK_INT >= 33) {
            pm.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
        } else {
            pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        return info?.activityInfo?.packageName
    }
}
