package com.nexatv.launcher.domain.usecase

import com.nexatv.launcher.domain.model.AppEntry
import java.util.Locale

/**
 * Case-insensitive search over installed apps.
 * Ranking: label starts with query, then a word in the label starts with it, then label contains
 * it, then package name contains it. Ties are broken alphabetically.
 */
class SearchAppsUseCase {

    operator fun invoke(apps: List<AppEntry>, query: String): List<AppEntry> {
        val q = normalize(query)
        if (q.isEmpty()) return emptyList()

        return apps
            .mapNotNull { app ->
                val label = normalize(app.label)
                val rank = when {
                    label.startsWith(q) -> 0
                    label.split(' ', '-', '+', '.').any { it.startsWith(q) } -> 1
                    label.contains(q) -> 2
                    app.packageName.lowercase(Locale.ROOT).contains(q) -> 3
                    else -> return@mapNotNull null
                }
                rank to app
            }
            .sortedWith(compareBy({ it.first }, { it.second.label.lowercase(Locale.ROOT) }))
            .map { it.second }
    }

    private fun normalize(value: String): String = value.trim().lowercase(Locale.ROOT)
}
