package com.example.animeexplorer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.animeexplorer.features.collection.data.local.dao.AnimeCollectionDao
import com.example.animeexplorer.features.detail.data.local.dao.AnimeDetailsDao
import com.example.animeexplorer.features.home.data.local.dao.HomeCachedDao
import com.example.animeexplorer.features.home.data.local.entity.HomeCacheEntity
import com.example.animeexplorer.features.collection.data.local.entity.AnimeCollectionsEntity
import com.example.animeexplorer.features.detail.data.local.entity.AnimeDetailsEntity

@Database(
    entities = [AnimeDetailsEntity::class, AnimeCollectionsEntity::class,
        HomeCacheEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animeDetailsDao(): AnimeDetailsDao
    abstract fun animeCollectionDao(): AnimeCollectionDao

    abstract fun homeCachedDao(): HomeCachedDao
}
