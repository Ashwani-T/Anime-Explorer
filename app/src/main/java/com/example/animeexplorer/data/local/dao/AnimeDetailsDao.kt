package com.example.animeexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.animeexplorer.data.mapper.CachedAnimeItem
import com.example.animeexplorer.data.local.entity.AnimeDetailsEntity



@Dao
interface AnimeDetailsDao{


    @Insert
    suspend fun insertAnimeDetails(anime: AnimeDetailsEntity)

    @Query("SELECT * FROM anime_details WHERE malId = :id")
    suspend fun getAnimeDetails(id: Int): AnimeDetailsEntity?

    @Query("SELECT malId, imageUrl, title, synopsis as description, type FROM anime_details")
    suspend fun getAnimeList(): List<CachedAnimeItem>

    @Query("SELECT malId, imageUrl, title, synopsis as description, type FROM anime_details WHERE title LIKE '%'||:query ||'%'")
    suspend fun getSearchedAnimeList(query: String): List<CachedAnimeItem>
}