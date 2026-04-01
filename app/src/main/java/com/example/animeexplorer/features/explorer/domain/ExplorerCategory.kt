package com.example.animeexplorer.features.explorer.domain

import com.example.animeexplorer.core.domain.enums.AnimeFilter

enum class ExplorerCategory(
    val displayName: String,
    val filter: AnimeFilter?
) {
    TRENDING("Trending", AnimeFilter.BY_POPULARITY),
    TOP("Top", null),
    UPCOMING("Upcoming", AnimeFilter.UPCOMING),
    FAVORITE("Favorite", AnimeFilter.FAVORITE),
    SEASON("This Season", null)
}

