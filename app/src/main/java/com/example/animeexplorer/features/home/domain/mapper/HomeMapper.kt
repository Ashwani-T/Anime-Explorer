package com.example.animeexplorer.features.home.domain.mapper

import com.example.animeexplorer.core.data.remote.dto.AnimeDto
import com.example.animeexplorer.features.home.data.local.entity.HomeCacheEntity
import com.example.animeexplorer.core.domain.AnimeUiModel

fun AnimeDto.toHomeCacheEntity(category: String): HomeCacheEntity {
    return HomeCacheEntity(
        id = id,
        title = title ?: "No Title",
        description = description ?: "No description",
        imageUrl = imageUrl.webp.imageUrl ?: "No Image",
        score = score ?: 0.0,
        category = category,
    )
}

fun HomeCacheEntity.toAnimeUiModel(): AnimeUiModel {
    return AnimeUiModel(
        id = id,
        title = title,
        description = description,
        duration = "",
        imageUrl = imageUrl,
        score = score
    )
}
