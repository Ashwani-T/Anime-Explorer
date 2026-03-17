package com.example.animeexplorer.data.mapper

import com.example.animeexplorer.data.local.entity.EpisodeEntity
import com.example.animeexplorer.data.remote.dto.AnimeEpisodeDto


fun AnimeEpisodeDto.toEpisodeEntity(malId: Int): EpisodeEntity {
    return EpisodeEntity(
        malId = malId,
        episodeNumber = this.malId, // mal_id is the episode number in the episodes endpoint
        title = this.title,
        isCompleted = false
    )
}

fun List<AnimeEpisodeDto>.toEpisodeEntityList(malId: Int): List<EpisodeEntity> {
    return this.map { episode ->
        episode.toEpisodeEntity(malId)
    }
}

