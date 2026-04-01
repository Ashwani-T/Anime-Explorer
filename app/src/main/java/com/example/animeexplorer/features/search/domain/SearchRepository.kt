package com.example.animeexplorer.features.search.domain

import com.example.animeexplorer.core.domain.AnimeResponseModel
import com.example.animeexplorer.core.domain.AnimeUiModel
import com.example.animeexplorer.core.domain.enums.AnimeFilter
import com.example.animeexplorer.core.domain.enums.AnimeType
import com.example.animeexplorer.core.domain.enums.FormatType
import com.example.animeexplorer.core.domain.enums.RatingType
import com.example.animeexplorer.core.domain.enums.SortOrder
import com.example.animeexplorer.core.domain.enums.SortType
import com.example.animeexplorer.core.domain.enums.StatusType
import com.example.animeexplorer.features.search.ui.GenreModel

interface SearchRepository {
    suspend fun getAnimeList(query: String, page: Int): AnimeResponseModel

    suspend fun getTopAnime(
        type: AnimeType? = null,
        filter: AnimeFilter? = null,
        rating: RatingType? = null
    ): Result<List<AnimeUiModel>>

    suspend fun getFilteredAnime(
        query: String,
        page: Int?,
        orderBy: SortType? = null,
        sortOrder: SortOrder? = null,
        format: FormatType? = null,
        status: StatusType? = null,
        rating: RatingType? = null,
        genres: Set<GenreModel>? = emptySet(),
    ): Result<AnimeResponseModel>

    suspend fun getThisSeasonAnime(): Result<List<AnimeUiModel>>
}
