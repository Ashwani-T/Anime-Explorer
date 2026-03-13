package com.example.animeexplorer.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.animeexplorer.data.dao.AnimeDetailsDao
import com.example.animeexplorer.data.dao.AnimeListDao
import com.example.animeexplorer.data.entity.AnimeCollectionsEntity
import com.example.animeexplorer.data.entity.AnimeDetailsEntity


@Database(entities = [AnimeDetailsEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun animeDetailsDao(): AnimeDetailsDao
    abstract fun animeListDao(): AnimeListDao
}