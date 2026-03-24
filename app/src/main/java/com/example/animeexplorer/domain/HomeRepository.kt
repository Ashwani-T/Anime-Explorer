package com.example.animeexplorer.domain

import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getTrendingAnime(): Flow<List<AnimeUiModel>>
    fun getUpcomingAnime(): Flow<List<AnimeUiModel>>
    fun getTopAnime(): Flow<List<AnimeUiModel>>
    fun getSeasonAnime(): Flow<List<AnimeUiModel>>

    fun getFavoriteAnime(): Flow<List<AnimeUiModel>>
    suspend fun refreshHomeData(forceRefresh: Boolean = false)

}