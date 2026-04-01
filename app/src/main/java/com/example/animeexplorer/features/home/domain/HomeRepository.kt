package com.example.animeexplorer.features.home.domain

import com.example.animeexplorer.core.domain.AnimeUiModel
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getTrendingAnime(): Flow<List<AnimeUiModel>>
    fun getUpcomingAnime(): Flow<List<AnimeUiModel>>
    fun getTopAnime(): Flow<List<AnimeUiModel>>
    fun getSeasonAnime(): Flow<List<AnimeUiModel>>

    fun getFavoriteAnime(): Flow<List<AnimeUiModel>>
    suspend fun refreshHomeData(forceRefresh: Boolean = false)
}
