package com.example.animeexplorer.domain

import com.example.animeexplorer.data.local.entity.LibraryStatus
import kotlinx.coroutines.flow.Flow

interface AnimeCollectionRepository {
    suspend fun addToLibrary(malId: Int, status: LibraryStatus): Result<Unit>
    suspend fun updateLibraryStatus(malId: Int, status: LibraryStatus): Result<Unit>
    suspend fun updateEpisodesCompleted(malId: Int, count: Int): Result<Unit>
    suspend fun removeFromLibrary(malId: Int): Result<Unit>
    suspend fun getLibraryCollection(malId: Int): Result<AnimeCollectionUiModel?>
    fun getAllLibraryCollections(): Flow<List<AnimeCollectionUiModel>>
}