package com.example.animeexplorer.features.home.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.animeexplorer.features.home.data.local.entity.HomeCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface  HomeCachedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeEntity(animeEntity: List<HomeCacheEntity>)

    @Query("DELETE FROM anime_cache")
    suspend fun clearAnimeCacheTable()

    @Query("SELECT * FROM anime_cache WHERE category = :category")
    fun getAnimeByCategory(category: String): Flow<List<HomeCacheEntity>>

    @Query("SELECT Max(timeStamp) from anime_cache  ")
    suspend fun getLastSyncTime(): Long?
}
