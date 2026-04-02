package com.example.animeexplorer.di

import com.example.animeexplorer.features.collection.data.AnimeCollectionRepositoryImpl
import com.example.animeexplorer.features.collection.domain.AnimeCollectionRepository
import com.example.animeexplorer.features.detail.data.DetailRepositoryImpl
import com.example.animeexplorer.features.detail.domain.DetailRepository
import com.example.animeexplorer.features.episodes.data.EpisodeRepositoryImpl
import com.example.animeexplorer.features.episodes.domain.EpisodeRepository
import com.example.animeexplorer.features.explorer.data.ExplorerRepositoryImpl
import com.example.animeexplorer.features.explorer.domain.ExplorerRepository
import com.example.animeexplorer.features.home.data.HomeRepositoryImpl
import com.example.animeexplorer.features.home.domain.HomeRepository
import com.example.animeexplorer.features.search.data.SearchRepositoryImpl
import com.example.animeexplorer.features.search.domain.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindDetailRepository(
        detailRepositoryImpl: DetailRepositoryImpl
    ): DetailRepository

    @Binds
    @Singleton
    abstract fun bindAnimeCollectionRepository(
        animeCollectionRepositoryImpl: AnimeCollectionRepositoryImpl
    ): AnimeCollectionRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindExplorerRepository(
        explorerRepositoryImpl: ExplorerRepositoryImpl
    ): ExplorerRepository

    @Binds
    @Singleton
    abstract fun bindEpisodeRepository(
        episodeRepositoryImpl: EpisodeRepositoryImpl
    ): EpisodeRepository
}

