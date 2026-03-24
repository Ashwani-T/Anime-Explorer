package com.example.animeexplorer.di

import com.example.animeexplorer.data.AnimeCollectionRepositoryImpl
import com.example.animeexplorer.data.AnimeRepositoryImpl
import com.example.animeexplorer.data.HomeRepositoryImpl
import com.example.animeexplorer.domain.AnimeCollectionRepository
import com.example.animeexplorer.domain.AnimeRepository
import com.example.animeexplorer.domain.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAnimeRepository(
        animeRepositoryImpl: AnimeRepositoryImpl
    ): AnimeRepository

    @Binds
    @Singleton
    abstract fun bindAnimeCollectionRepository(
        animeCollectionRepositoryImpl: AnimeCollectionRepositoryImpl
    ): AnimeCollectionRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepositoryImpl(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository
}
