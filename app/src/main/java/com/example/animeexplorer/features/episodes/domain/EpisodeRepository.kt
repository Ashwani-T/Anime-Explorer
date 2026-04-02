package com.example.animeexplorer.features.episodes.domain

import com.example.animeexplorer.core.data.remote.dto.EpisodeResponseDto

interface EpisodeRepository {
    suspend fun getAnimeEpisodes(malId: Int): Result<EpisodeResponseDto>
}

