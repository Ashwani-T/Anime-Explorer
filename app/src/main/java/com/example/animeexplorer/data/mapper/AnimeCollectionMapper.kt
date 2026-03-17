package com.example.animeexplorer.data.mapper

import com.example.animeexplorer.data.local.entity.AnimeCollectionsEntity
import com.example.animeexplorer.domain.AnimeCollectionUiModel


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