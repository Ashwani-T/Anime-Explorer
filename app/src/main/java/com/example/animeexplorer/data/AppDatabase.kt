package com.example.animeexplorer.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.animeexplorer.data.dao.AnimeDetailsDao
import com.example.animeexplorer.data.dao.AnimeListDao
import com.example.animeexplorer.data.entity.AnimeCollectionsEntity
import com.example.animeexplorer.data.entity.AnimeDetailsEntity


@Database(
    entities = [AnimeDetailsEntity::class, AnimeCollectionsEntity::class],
    version = 2,
    autoMigrations = [AutoMigration(
        from = 1,
        to=2
    )]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animeDetailsDao(): AnimeDetailsDao
    abstract fun animeListDao(): AnimeListDao
}