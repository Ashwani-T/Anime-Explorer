package com.example.animeexplorer.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AnimeDetailResponse(
    val data: AnimeDetailDto
)

@Serializable
data class AnimeDetailDto(
    @SerialName("mal_id")
    val id: Int,

    @SerialName("title")
    val title: String? = null,

    @SerialName("synopsis")
    val synopsis: String? = null,

    @SerialName("images")
    val images: Image,

    @SerialName("score")
    val score: Double? = null,

    @SerialName("status")
    val status: String? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("episodes")
    val episodes: Int? = null,

    @SerialName("year")
    val year: Int? = null
)
