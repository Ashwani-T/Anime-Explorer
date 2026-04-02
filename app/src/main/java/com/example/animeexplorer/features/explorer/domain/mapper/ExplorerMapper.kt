package com.example.animeexplorer.features.explorer.domain.mapper

import com.example.animeexplorer.core.data.remote.dto.AnimeDto
import com.example.animeexplorer.core.data.remote.dto.Pagination
import com.example.animeexplorer.core.domain.AnimeUiModel
import com.example.animeexplorer.core.domain.PageInfo

fun AnimeDto.toUiModel(): AnimeUiModel {
    return AnimeUiModel(
        id = id,
        title = title ?: "No Title",
        description = description ?: "No description",
        duration = duration ?: "No duration",
        imageUrl = imageUrl.webp.imageUrl ?: "No Image",
        score = score ?: 0.0
    )
}

fun Pagination.toPageInfo(): PageInfo {
    return PageInfo(
        currentPage = currentPage,
        hasNextPage = hasNextPage
    )
}


