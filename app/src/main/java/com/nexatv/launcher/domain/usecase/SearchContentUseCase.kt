package com.nexatv.launcher.domain.usecase

import com.nexatv.launcher.domain.model.ContentItem
import java.util.Locale

/** Searches content by title, tagline or genre. */
class SearchContentUseCase {

    operator fun invoke(items: List<ContentItem>, query: String): List<ContentItem> {
        val q = query.trim().lowercase(Locale.ROOT)
        if (q.isEmpty()) return emptyList()
        return items
            .filter { item ->
                item.title.lowercase(Locale.ROOT).contains(q) ||
                    item.tagline.lowercase(Locale.ROOT).contains(q) ||
                    item.genres.any { it.lowercase(Locale.ROOT).contains(q) }
            }
            .sortedBy { if (it.title.lowercase(Locale.ROOT).startsWith(q)) 0 else 1 }
    }
}
