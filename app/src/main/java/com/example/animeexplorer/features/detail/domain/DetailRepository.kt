package com.example.animeexplorer.features.detail.domain

interface DetailRepository {
    suspend fun getAnimeDetail(malId: Int): Result<AnimeDetail>
}
