package com.example.animeexplorer.features.search.domain

import com.example.animeexplorer.core.domain.AnimeResponseModel
import com.example.animeexplorer.core.domain.AnimeUiModel
import com.example.animeexplorer.core.domain.enums.AnimeFilter
import com.example.animeexplorer.core.domain.enums.AnimeType
import com.example.animeexplorer.core.domain.enums.RatingType

interface SearchRepository {
    suspend fun getTopAnime(
        type: AnimeType? = null,
        filter: AnimeFilter? = null,
        rating: RatingType? = null
    ): Result<List<AnimeUiModel>>

    suspend fun getFilteredAnime(
        queryParamsMap: Map<String,String>? = emptyMap()
    ): Result<AnimeResponseModel>

    suspend fun getThisSeasonAnime(): Result<List<AnimeUiModel>>
}
