package com.example.animeexplorer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.animeexplorer.data.local.dao.AnimeCollectionDao
import com.example.animeexplorer.data.local.dao.AnimeDetailsDao
import com.example.animeexplorer.data.local.dao.AnimeEpisodeDao
import com.example.animeexplorer.data.local.entity.AnimeCollectionsEntity
import com.example.animeexplorer.data.local.entity.AnimeDetailsEntity
import com.example.animeexplorer.data.local.entity.EpisodeEntity

@Database(
    entities = [AnimeDetailsEntity::class, AnimeCollectionsEntity::class, EpisodeEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animeDetailsDao(): AnimeDetailsDao
    abstract fun animeCollectionDao(): AnimeCollectionDao
    abstract fun animeEpisodeDao(): AnimeEpisodeDao
}