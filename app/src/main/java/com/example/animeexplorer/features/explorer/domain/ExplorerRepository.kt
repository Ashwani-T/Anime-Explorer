package com.example.animeexplorer.features.explorer.domain

import com.example.animeexplorer.core.domain.AnimeResponseModel

interface ExplorerRepository {
    suspend fun getAnimeByCategory(
        category: ExplorerCategory,
        page: Int
    ): Result<AnimeResponseModel>
}

