package com.example.animeexplorer.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class EpisodeResponseDto(
    val data: List<AnimeEpisodeDto>,
    val pagination: PaginationDto
)

@Serializable
data class AnimeEpisodeDto(
    @SerialName("mal_id")
    val malId: Int,

    @SerialName("title")
    val title: String? = null,
)

@Serializable
data class PaginationDto(
    @SerialName("last_visible_page")
    val lastVisiblePage: Int,

    @SerialName("has_next_page")
    val hasNextPage: Boolean
)

