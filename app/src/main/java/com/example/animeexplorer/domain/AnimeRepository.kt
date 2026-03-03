package com.example.animeexplorer.domain

import com.example.animeexplorer.data.AnimeResponse
import com.example.animeexplorer.data.AnimeDetailResponse



interface AnimeRepository {
    suspend fun getAnimeList(page: Int): Result<AnimeResponse>
    suspend fun getAnimeDetail(malId: Int): Result<AnimeDetailResponse>
}