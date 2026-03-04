package com.example.animeexplorer.data

import com.example.animeexplorer.domain.AnimeDetailUiModel
import com.example.animeexplorer.domain.AnimeUiModel

fun AnimeDto.toUiModel(): AnimeUiModel {
    return AnimeUiModel(
        id = id,
        title = title?:"No Title" ,
        description = description?:"No description",
        duration = duration?:"No duration",
        imageUrl = imageUrl.webp.imageUrl?:"No Image",
        type = type?:"No Type"
    )
}



fun AnimeDetailDto.toDetailUiModel(): AnimeDetailUiModel {
    return AnimeDetailUiModel(
        id = id,
        title = title ?: "No Title",
        synopsis = synopsis ?: "No Synopsis",
        imageUrl = images.webp.imageUrl ?: "No Image",
        score = score ?: 0.0,
        status = status ?: "Unknown",
        type = type ?: "Unknown",
        episodes = episodes ?: 0,
        year = year ?: 0
    )
}
