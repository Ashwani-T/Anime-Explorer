package com.example.animeexplorer.di

import android.content.Context
import androidx.room.Room
import com.example.animeexplorer.core.database.AppDatabase
import com.example.animeexplorer.data.local.dao.AnimeCollectionDao
import com.example.animeexplorer.data.local.dao.AnimeDetailsDao
import com.example.animeexplorer.data.local.dao.AnimeEpisodeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            klass = AppDatabase::class.java,
            name = "anime_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAnimeDetailsDao(
        database: AppDatabase
    ): AnimeDetailsDao{
        return database.animeDetailsDao()
    }

    @Provides
    @Singleton
    fun provideAnimeCollectionDao(
        database: AppDatabase
    ): AnimeCollectionDao{
        return database.animeCollectionDao()
    }

    @Provides
    fun provideEpisodeDao(
        database: AppDatabase
    ): AnimeEpisodeDao{
        return database.animeEpisodeDao()
    }
}