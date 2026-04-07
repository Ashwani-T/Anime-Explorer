package com.example.animeexplorer.features.detail.domain.mapper

import android.util.Log
import com.example.animeexplorer.core.data.remote.dto.AnimeDetailDto
import com.example.animeexplorer.features.detail.data.local.entity.AnimeDetailsEntity
import com.example.animeexplorer.core.domain.AnimeDetailUiModel

fun formatEmbeddedTrailerUrl(url: String?): String {

    if (url.isNullOrEmpty()) {
        return ""
    }
    val embedUrl = url.replace("www.youtube-nocookie.com","www.youtube.com").replaceAfter("?","").replace("embed/", "watch?v=")
    Log.d("TAG", "formatEmbededTrailerUrl: $embedUrl ")
    return embedUrl
}

fun AnimeDetailDto.toAnimeDetailEntity(): AnimeDetailsEntity = AnimeDetailsEntity(
    malId = id,
    title = title.orEmpty(),
    synopsis = synopsis.orEmpty(),
    imageUrl = images.webp.imageUrl.orEmpty(),
    trailerUrl = formatEmbeddedTrailerUrl(trailer?.url),
    score = score ?: 0.0,
    status = status.orEmpty(),
    type = type.orEmpty(),
    episodes = episodes ?: 0,
    year = year ?: 0
)
