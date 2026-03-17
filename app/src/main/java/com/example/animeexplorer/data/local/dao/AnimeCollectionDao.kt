package com.example.animeexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.animeexplorer.data.local.entity.AnimeCollectionsEntity
import com.example.animeexplorer.data.local.entity.LibraryStatus


@Dao
interface AnimeCollectionDao {

    @Upsert
    suspend fun addToLibrary(anime: AnimeCollectionsEntity)

    @Update
    suspend fun updateAnimeCollection(anime: AnimeCollectionsEntity)

    @Delete
    suspend fun removeFromLibrary(anime: AnimeCollectionsEntity)

    @Query("SELECT * FROM anime_collections WHERE malId = :malId")
    suspend fun getCollectionByMalId(malId: Int): AnimeCollectionsEntity?

    @Query("SELECT * FROM anime_collections")
    suspend fun getAllCollections(): List<AnimeCollectionsEntity>

    @Query("SELECT * FROM anime_collections WHERE status = :status")
    suspend fun getCollectionsByStatus(status: LibraryStatus): List<AnimeCollectionsEntity>

    @Query("UPDATE anime_collections SET episodesCompleted = episodesCompleted + :increment WHERE malId = :malId")
    suspend fun updateEpisodeProgress(malId: Int, increment: Int)

    @Query("UPDATE anime_collections SET status = :status WHERE malId = :malId")
    suspend fun updateLibraryStatus(malId: Int, status: LibraryStatus)

    @Query("UPDATE anime_collections SET episodesCompleted = :completedEpisodes WHERE malId = :malId")
    suspend fun setEpisodesCompleted(malId: Int, completedEpisodes: Int)
}

