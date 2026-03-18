package com.example.animeexplorer.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GenresResponseDto(
    @SerialName("mal_id")
    val genreId: Int,

    @SerialName("name")
    val genreName: String
)