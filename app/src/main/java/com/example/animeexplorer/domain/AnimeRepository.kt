package com.example.animeexplorer.domain

import com.example.animeexplorer.data.mapper.AnimeDetail
import com.example.animeexplorer.domain.enums.AnimeFilter
import com.example.animeexplorer.domain.enums.AnimeRating
import com.example.animeexplorer.domain.enums.AnimeType


interface AnimeRepository {
    suspend fun getAnimeList(query: String, page: Int): AnimeResponseModel
    suspend fun getAnimeDetail(malId: Int): Result<AnimeDetail>

    suspend fun getTopAnime(type: AnimeType? = null, filter: AnimeFilter? = null, rating: AnimeRating? = null): Result<List<AnimeUiModel>>
}

