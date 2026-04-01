package com.example.animeexplorer.features.collection.data.mapper

import com.example.animeexplorer.features.collection.data.local.entity.AnimeCollectionsEntity
import com.example.animeexplorer.core.domain.AnimeCollectionUiModel


fun AnimeCollectionsEntity.toAnimeCollectionUiModel(): AnimeCollectionUiModel{
    return AnimeCollectionUiModel(
        malId = malId,
        title = title,
        imageUrl = imageUrl,
        type = type,
        status = status,
        episodesCompleted = episodesCompleted,
        totalEpisodes = totalEpisodes
    )
}