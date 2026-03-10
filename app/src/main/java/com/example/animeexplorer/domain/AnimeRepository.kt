package com.example.animeexplorer.domain

import com.example.animeexplorer.data.AnimeDetailResponse


interface AnimeRepository {
    suspend fun getAnimeList(page: Int): AnimeResponseModel
    suspend fun getAnimeDetail(malId: Int): Result<AnimeDetailResponse>
    suspend fun searchAnime(query: String, page: Int): AnimeResponseModel?
}