package com.nexatv.launcher.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.nexatv.launcher.domain.model.WallpaperChoice
import com.nexatv.launcher.ui.screens.NexaRoot
import kotlinx.coroutines.launch

/**
 * The single activity. It is both the launcher entry (LEANBACK_LAUNCHER / LAUNCHER) and the Home
 * activity (HOME). It owns the Android flows that need an Activity: the document picker for
 * wallpapers and the system "make this your Home app" dialog.
 */
class MainActivity : ComponentActivity() {

    private val container: AppContainer
        get() = (application as NexaApplication).container

    private val wallpaperPicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) onWallpaperPicked(uri)
    }

    private val homeRoleRequest = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        // Whatever the user chose in the system dialog, re-read the real state.
        container.refreshDefaultHome()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container.refreshDefaultHome()
        setContent {
            NexaRoot(
                container = container,
                onRequestDefaultHome = ::requestDefaultHome,
                onPickWallpaper = ::pickWallpaper,
            )
        }
    }

    /** singleTask: pressing the remote's Home button while we are open arrives here. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.hasCategory(Intent.CATEGORY_HOME)) container.notifyHomePressed()
    }

    private fun requestDefaultHome() {
        val controller = container.defaultHomeController
        if (controller.isDefaultHome()) {
            container.messages.post("Harsh Reality is already your default Home app.")
            return
        }
        val roleIntent = controller.roleRequestIntent()
        if (roleIntent == null) {
            // No role dialog on this device: send the user to Android's own Home / default-app settings.
            container.appActions.openHomeSettings()
            return
        }
        try {
            homeRoleRequest.launch(roleIntent)
        } catch (e: ActivityNotFoundException) {
            container.appActions.openHomeSettings()
        }
    }

    private fun pickWallpaper() {
        try {
            wallpaperPicker.launch(arrayOf("image/*"))
        } catch (e: ActivityNotFoundException) {
            container.messages.post(
                "This TV has no file picker. Copy images into Harsh Reality's wallpaper folder instead.",
                isError = true,
            )
        }
    }

    private fun onWallpaperPicked(uri: Uri) {
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (e: SecurityException) {
            // The provider does not offer persistable grants; the image still works for this session.
        }
        lifecycleScope.launch {
            container.settingsRepository.setWallpaper(WallpaperChoice.Image(uri.toString()))
            container.messages.post("Wallpaper updated")
        }
    }
}
