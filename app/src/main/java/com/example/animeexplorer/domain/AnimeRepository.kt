package com.example.animeexplorer.domain

import com.example.animeexplorer.data.AnimeDetail
import com.example.animeexplorer.data.AnimeDetailResponse


interface AnimeRepository {
    suspend fun getAnimeList(query: String, page: Int): AnimeResponseModel
    suspend fun getAnimeDetail(malId: Int): Result<AnimeDetail>

    suspend fun addAnimeToCollection(mal)
}