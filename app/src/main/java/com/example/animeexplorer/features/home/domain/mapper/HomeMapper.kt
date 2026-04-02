package com.example.animeexplorer.features.home.domain.mapper

import com.example.animeexplorer.core.data.remote.dto.AnimeDto
import com.example.animeexplorer.features.home.data.local.entity.AnimeCacheEntity
import com.example.animeexplorer.core.domain.AnimeUiModel

fun AnimeDto.toAnimeCacheEntity(category: String): AnimeCacheEntity {
    return AnimeCacheEntity(
        id = id,
        title = title ?: "No Title",
        description = description ?: "No description",
        imageUrl = imageUrl.webp.imageUrl ?: "No Image",
        score = score ?: 0.0,
        category = category,
    )
}

fun AnimeCacheEntity.toUiModel(): AnimeUiModel {
    return AnimeUiModel(
        id = id,
        title = title,
        description = description,
        duration = "",
        imageUrl = imageUrl,
        score = score
    )
}
