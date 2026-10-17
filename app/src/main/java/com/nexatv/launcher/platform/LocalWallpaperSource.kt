package com.nexatv.launcher.platform

import android.content.Context
import java.io.File
import java.util.Locale

/**
 * Wallpapers the user placed in NEXA TV's own app-specific storage folder
 * (`Android/data/<package>/files/wallpapers`). This needs no storage permission and works on TVs
 * that ship without a document picker: copy images there over USB, adb or a file manager.
 *
 * Call from a background dispatcher.
 */
class LocalWallpaperSource(private val context: Context) {

    private val directory: File? get() = context.getExternalFilesDir(FOLDER)

    fun listImported(): List<File> {
        val dir = directory ?: return emptyList()
        if (!dir.exists()) dir.mkdirs()
        return dir.listFiles { file -> file.isFile && file.extension.lowercase(Locale.ROOT) in EXTENSIONS }
            ?.sortedBy { it.name.lowercase(Locale.ROOT) }
            ?.take(MAX_FILES)
            .orEmpty()
    }

    /** Human readable folder path shown in the wallpaper screen. */
    fun folderHint(): String = directory?.absolutePath ?: "Android/data/${context.packageName}/files/$FOLDER"

    private companion object {
        const val FOLDER = "wallpapers"
        const val MAX_FILES = 24
        val EXTENSIONS = setOf("jpg", "jpeg", "png", "webp")
    }
}
