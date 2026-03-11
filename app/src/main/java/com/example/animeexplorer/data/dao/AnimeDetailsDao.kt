package com.example.animeexplorer.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.animeexplorer.data.entity.AnimeDetailsEntity



@Dao
interface AnimeDetailsDao{

    @Insert
    suspend fun insertAnimeDetails(anime: AnimeDetailsEntity)

    @Query("SELECT * FROM anime_details WHERE malId = :id")
    suspend fun getAnimeDetails(id: Int): AnimeDetailsEntity?
}