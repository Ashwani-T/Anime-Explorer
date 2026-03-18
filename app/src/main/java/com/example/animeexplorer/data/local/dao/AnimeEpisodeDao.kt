package com.example.animeexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.animeexplorer.data.local.entity.EpisodeEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface AnimeEpisodeDao {

    @Insert
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>)

    @Update
    suspend fun updateEpisode(episode: EpisodeEntity)

    @Query("SELECT * FROM episodes WHERE malId = :malId ORDER BY episodeNumber ASC")
    suspend fun getEpisodesByMalId(malId: Int): List<EpisodeEntity>

    @Query("SELECT * FROM episodes WHERE malId = :malId ORDER BY episodeNumber ASC")
    fun observeEpisodesByMalId(malId: Int): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE malId = :malId AND episodeNumber = :episodeNumber")
    suspend fun getEpisodeByNumber(malId: Int, episodeNumber: Int): EpisodeEntity?

    @Query("""
    UPDATE episodes
    SET isCompleted = CASE 
        WHEN episodeNumber <= :episodeNumber THEN 1
        ELSE 0
    END
    WHERE malId = :malId
""")
    suspend fun updateEpisodeInRange(malId: Int, episodeNumber: Int)

    @Query("UPDATE episodes SET isCompleted = :isCompleted WHERE malId = :malId AND episodeNumber = :episodeNumber")
    suspend fun updateEpisodeStatus(malId: Int, episodeNumber: Int, isCompleted: Boolean)

    @Query("SELECT COUNT(*) FROM episodes WHERE malId = :malId AND isCompleted = 1")
    suspend fun getCompletedEpisodesCount(malId: Int): Int

    @Query("SELECT COUNT(*) FROM episodes WHERE malId = :malId AND isCompleted = 1")
    fun observeCompletedEpisodesCount(malId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM episodes WHERE malId = :malId")
    suspend fun getTotalEpisodesCount(malId: Int): Int

    @Query("SELECT COUNT(*) FROM episodes WHERE malId = :malId")
    fun observeTotalEpisodesCount(malId: Int): Flow<Int>

    @Query("DELETE FROM episodes WHERE malId = :malId")
    suspend fun deleteEpisodesByMalId(malId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM episodes WHERE malId = :malId)")
    suspend fun hasEpisodes(malId: Int): Boolean

    @Query("SELECT MAX(episodeNumber) FROM episodes WHERE malId = :malId")
    suspend fun getLastEpisodeNumber(malId: Int): Int?
}

