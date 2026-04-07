package com.example.animeexplorer.features.detail.domain

import com.example.animeexplorer.core.domain.AnimeDetailUiModel

interface DetailRepository {
    suspend fun getAnimeDetail(malId: Int): Result<AnimeDetailUiModel>
}
