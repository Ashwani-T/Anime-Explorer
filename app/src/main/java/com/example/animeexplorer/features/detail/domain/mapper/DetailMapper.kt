package com.example.animeexplorer.features.detail.domain.mapper

import android.util.Log
import com.example.animeexplorer.core.data.remote.dto.AnimeDetailDto
import com.example.animeexplorer.features.detail.data.local.entity.AnimeDetailsEntity
import com.example.animeexplorer.features.detail.domain.AnimeDetail
import com.example.animeexplorer.core.domain.AnimeDetailUiModel

fun formatEmbededTrailerUrl(url: String): String {
    val embedUrl = url.replace("www.youtube-nocookie.com","www.youtube.com").replaceAfter("?","").replace("embed/", "watch?v=")
    Log.d("TAG", "formatEmbededTrailerUrl: $embedUrl ")
    return embedUrl
}
fun AnimeDetailDto.toDomain(): AnimeDetail = AnimeDetail(
    id = id,
    title = title.orEmpty(),
    synopsis = synopsis.orEmpty(),
    imageUrl = images.webp.imageUrl.orEmpty(),
    trailerUrl = trailer?.url.orEmpty(),
    score = score ?: 0.0,
    status = status.orEmpty(),
    type = type.orEmpty(),
    episodes = episodes ?: 0,
    year = year ?: 0
)

fun AnimeDetailsEntity.toDomain(): AnimeDetail = AnimeDetail(
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

fun AnimeDetail.toEntity(): AnimeDetailsEntity = AnimeDetailsEntity(
    malId = id,
    title = title,
    year = year,
    imageUrl = imageUrl,
    trailerUrl = trailerUrl,
    synopsis = synopsis,
    score = score,
    status = status,
    episodes = episodes,
    type = type
)

fun AnimeDetail.toUiModel(): AnimeDetailUiModel = AnimeDetailUiModel(
    id = id,
    title = title,
    synopsis = synopsis,
    imageUrl = imageUrl,
    trailerUrl = formatEmbededTrailerUrl(trailerUrl),
    score = score,
    status = status,
    type = type,
    episodes = episodes,
    year = year
)
