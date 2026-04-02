package com.example.animeexplorer.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeResponseDto(
    val data: List<AnimeEpisodeDto>,
    val pagination: EpisodePagination
)

@Serializable
data class EpisodePagination(
    @SerialName("last_visible_page")
    val lastVisiblePage: Int,
    @SerialName("has_next_page")
    val hasNextPage: Boolean
)

@Serializable
data class AnimeEpisodeDto(
    @SerialName("mal_id")
    val id: Int,

    @SerialName("title")
    val title: String? = null,

    @SerialName("score")
    val score: Double? = null
)

