package com.example.animeexplorer.domain

import com.example.animeexplorer.data.AnimeResponse



interface AnimeRepository {
    suspend fun getAnimeList(page: Int): Result<AnimeResponse>
}