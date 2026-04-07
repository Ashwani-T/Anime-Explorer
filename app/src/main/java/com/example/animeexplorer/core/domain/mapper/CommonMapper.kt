package com.example.animeexplorer.core.domain.mapper

import com.example.animeexplorer.core.data.remote.dto.AnimeDto
import com.example.animeexplorer.core.data.remote.dto.Pagination
import com.example.animeexplorer.core.domain.AnimeDetailUiModel
import com.example.animeexplorer.core.domain.AnimeUiModel
import com.example.animeexplorer.core.domain.PageInfo
import com.example.animeexplorer.features.collection.data.local.entity.AnimeCollectionsEntity
import com.example.animeexplorer.features.collection.data.local.entity.LibraryStatus
import com.example.animeexplorer.features.detail.data.local.entity.AnimeDetailsEntity


fun AnimeDetailsEntity.toAnimeUiModel(): AnimeUiModel{
    return AnimeUiModel(
        id = malId,
        title = title,
        description = synopsis,
        duration = "No duration",
        imageUrl = imageUrl,
        score = score
    )
}

fun AnimeDetailsEntity.toAnimeCollectionEntity(status: LibraryStatus): AnimeCollectionsEntity{
    return  AnimeCollectionsEntity(
        malId = malId,
        title = title,
        imageUrl = imageUrl,
        type = type,
        status = status,
        episodesCompleted = 0,
        totalEpisodes = episodes
    )
}

fun AnimeDetailsEntity.toAnimeDetailUiModel(): AnimeDetailUiModel = AnimeDetailUiModel(
    id = malId,
    title = title,
    synopsis = synopsis,
    imageUrl = imageUrl,
    trailerUrl = trailerUrl,
    score = score,
    status = status,
    type = type,
    episodes = episodes,
    year = year
)

fun AnimeDto.toAnimeUiModel(): AnimeUiModel {
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
