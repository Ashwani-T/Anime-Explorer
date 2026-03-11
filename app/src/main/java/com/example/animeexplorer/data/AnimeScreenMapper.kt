package com.example.animeexplorer.data

import com.example.animeexplorer.data.entity.AnimeDetailsEntity
import com.example.animeexplorer.domain.AnimeDetailUiModel
import com.example.animeexplorer.domain.AnimeUiModel
import com.example.animeexplorer.domain.PageInfo

// Middle Object between db and api
data class AnimeDetail(
    val id: Int,
    val title: String,
    val synopsis: String,
    val imageUrl: String,
    val score: Double,
    val status: String,
    val type: String,
    val episodes: Int,
    val year: Int
)

// Anime DTO to Ui
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

fun Pagination.toPageInfo(): PageInfo {
    return PageInfo(
        currentPage = currentPage,
        hasNextPage = hasNextPage
    )
}

/// Detail DTO TO DOMAIN

fun AnimeDetailDto.toDomain(): AnimeDetail = AnimeDetail(
    id = id,
    title = title.orEmpty(),
    synopsis = synopsis.orEmpty(),
    imageUrl = images.webp.imageUrl.orEmpty(),
    score = score ?: 0.0,
    status = status.orEmpty(),
    type = type.orEmpty(),
    episodes = episodes ?: 0,
    year = year ?: 0
)

/// Detail ENTITY TO DOMAIN

fun AnimeDetailsEntity.toDomain(): AnimeDetail = AnimeDetail(
    id = malId,
    title = title,
    synopsis = synopsis,
    imageUrl = imageUrl,
    score = score,
    status = status,
    type = type,
    episodes = episodes,
    year = year
)

// Detail Domain to Entity

fun AnimeDetail.toEntity(): AnimeDetailsEntity = AnimeDetailsEntity(
    malId = id,
    title = title,
    year = year,
    imageUrl = imageUrl,
    synopsis = synopsis,
    score = score,
    status = status,
    episodes = episodes,
    type = type
)


// Detail Domain to UI Model
fun AnimeDetail.toUiModel(): AnimeDetailUiModel = AnimeDetailUiModel(
    id = id,
    title = title,
    synopsis = synopsis,
    imageUrl = imageUrl,
    score = score,
    status = status,
    type = type,
    episodes = episodes,
    year = year
)


fun CachedAnimeItem.toUiModel(): AnimeUiModel{
    return AnimeUiModel(
        id = malId,
        title = title,
        description = description,
        duration = "",
        imageUrl = imageUrl,
        type = type
    )
}