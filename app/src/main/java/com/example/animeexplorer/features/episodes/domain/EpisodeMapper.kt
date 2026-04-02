package com.example.animeexplorer.features.episodes.domain

import com.example.animeexplorer.core.data.remote.dto.AnimeEpisodeDto

fun AnimeEpisodeDto.toUiModel(): AnimeEpisodeUiModel {
    return AnimeEpisodeUiModel(
        id = id,
        title = title ?: "No Title",
        score = score
    )
}

