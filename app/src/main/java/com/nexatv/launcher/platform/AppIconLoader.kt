package com.nexatv.launcher.platform

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.LruCache
import androidx.core.graphics.drawable.toBitmap
import com.nexatv.launcher.domain.model.AppEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** A rendered app icon plus its average color, used to tint the app's glass tile. */
class AppIcon(val bitmap: Bitmap, val dominantColor: Int)

/**
 * Loads app icons off the main thread and keeps a small byte-bounded LRU cache, so scrolling the
 * apps grid on a low-memory TV does not re-decode drawables.
 */
class AppIconLoader(private val context: Context) {

    private val cache = object : LruCache<String, AppIcon>(cacheSizeKb()) {
        override fun sizeOf(key: String, value: AppIcon): Int = value.bitmap.byteCount / 1024
    }

    fun cached(app: AppEntry): AppIcon? = cache.get(app.key)

    suspend fun load(app: AppEntry): AppIcon? {
        cache.get(app.key)?.let { return it }
        return withContext(Dispatchers.IO) {
            runCatching { render(app) }.getOrNull()?.also { cache.put(app.key, it) }
        }
    }

    private fun render(app: AppEntry): AppIcon {
        val drawable = context.packageManager.getActivityIcon(
            ComponentName(app.packageName, app.activityName),
        )
        val bitmap = drawable.toBitmap(ICON_PX, ICON_PX, Bitmap.Config.ARGB_8888)
        return AppIcon(bitmap, dominantColor(bitmap))
    }

    private fun dominantColor(source: Bitmap): Int {
        val small = Bitmap.createScaledBitmap(source, SAMPLE_PX, SAMPLE_PX, true)
        var r = 0L
        var g = 0L
        var b = 0L
        var count = 0L
        for (y in 0 until SAMPLE_PX) {
            for (x in 0 until SAMPLE_PX) {
                val pixel = small.getPixel(x, y)
                if (Color.alpha(pixel) < 128) continue
                r += Color.red(pixel)
                g += Color.green(pixel)
                b += Color.blue(pixel)
                count++
            }
        }
        if (small !== source) small.recycle()
        return if (count == 0L) {
            FALLBACK_COLOR
        } else {
            Color.rgb((r / count).toInt(), (g / count).toInt(), (b / count).toInt())
        }
    }

    private companion object {
        const val ICON_PX = 128
        const val SAMPLE_PX = 12
        const val FALLBACK_COLOR = 0xFF3A4A7A.toInt()

        /** At most 1/16 of the heap and never more than 8 MB. */
        fun cacheSizeKb(): Int {
            val heapKb = (Runtime.getRuntime().maxMemory() / 1024L).toInt()
            return minOf(heapKb / 16, 8 * 1024)
        }
    }
}
