package com.example.animeexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.animeexplorer.data.local.entity.AnimeCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface  AnimeCachedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeEntity(animeEntity: List<AnimeCacheEntity>)

    @Query("DELETE FROM anime_cache")
    suspend fun clearAnimeCacheTable()

    @Query("SELECT * FROM anime_cache WHERE category = :category")
    fun getAnimeByCategory(category: String): Flow<List<AnimeCacheEntity>>

    @Query("SELECT Max(timeStamp) from anime_cache  ")
    suspend fun getLastSyncTime(): Long?
}