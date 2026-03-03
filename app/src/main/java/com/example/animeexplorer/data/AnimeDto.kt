package com.example.animeexplorer.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class AnimeResponse(
    val pagination: Pagination,
    val data: List<AnimeDto>
)

@Serializable
data class Pagination(
    @SerialName("last_visible_page")
    val lastVisiblePage: Int,
    @SerialName("has_next_page")
    val hasNextPage: Boolean,
    @SerialName("current_page")
    val currentPage: Int,
)

@Serializable
data class AnimeDto(
    @SerialName("mal_id")
    val id: Int,

    @SerialName("title")
    val title: String? = null,

    @SerialName("synopsis")
    val description: String? = null,

    @SerialName("duration")
    val duration: String? = null,

    @SerialName("images")
    val imageUrl: Image,

    @SerialName("type")
    val type: String? = null,
)

@Serializable
data class Image(
    @SerialName("webp")
    val webp: Webp
)

@Serializable
data class Webp(
    @SerialName("image_url")
    val imageUrl: String? = null
)