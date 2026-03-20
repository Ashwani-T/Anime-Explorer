package com.example.animeexplorer.domain

import com.example.animeexplorer.data.mapper.AnimeDetail
import com.example.animeexplorer.domain.enums.AnimeFilter
import com.example.animeexplorer.domain.enums.AnimeType
import com.example.animeexplorer.domain.enums.FormatType
import com.example.animeexplorer.domain.enums.RatingType
import com.example.animeexplorer.domain.enums.SortOrder
import com.example.animeexplorer.domain.enums.SortType
import com.example.animeexplorer.domain.enums.StatusType
import com.example.animeexplorer.screens.GenreModel


interface AnimeRepository {
    suspend fun getAnimeList(query: String, page: Int): AnimeResponseModel
    suspend fun getAnimeDetail(malId: Int): Result<AnimeDetail>

    suspend fun getTopAnime(
        type: AnimeType? = null,
        filter: AnimeFilter? = null,
        rating: RatingType? = null
    ): Result<List<AnimeUiModel>>

    suspend fun getFilteredAnime(
        query: String,
        orderBy: SortType? = null,
        sortOrder: SortOrder? = null,
        format: FormatType? = null,
        status: StatusType? = null,
        rating: RatingType? = null,
        genres: Set<GenreModel>? = emptySet(),
    ): Result<List<AnimeUiModel>>

    suspend fun getThisSeasonAnime(): Result<List<AnimeUiModel>>
}

