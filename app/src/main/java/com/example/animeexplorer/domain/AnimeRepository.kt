package com.example.animeexplorer.domain

import com.example.animeexplorer.data.local.entity.AnimeCollectionsEntity
import com.example.animeexplorer.data.local.entity.LibraryStatus
import com.example.animeexplorer.data.mapper.AnimeDetail


interface AnimeRepository {
    suspend fun getAnimeList(query: String, page: Int): AnimeResponseModel
    suspend fun getAnimeDetail(malId: Int): Result<AnimeDetail>
}

