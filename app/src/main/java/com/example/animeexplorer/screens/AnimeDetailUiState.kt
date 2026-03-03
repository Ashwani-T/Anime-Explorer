package com.example.animeexplorer.screens

import com.example.animeexplorer.data.AnimeDetailDto


fun AnimeDetailDto.toDetailUiModel(): AnimeDetailUiModel {
    return AnimeDetailUiModel(
        id = id,
        title = title ?: "No Title",
        synopsis = synopsis ?: "No Synopsis",
        imageUrl = images.webp.imageUrl ?: "No Image",
        score = score ?: 0.0,
        status = status ?: "Unknown",
        type = type ?: "Unknown",
        episodes = episodes ?: 0,
        year = year ?: 0
    )
}

data class AnimeDetailUiModel(
    val id: Int,
    val title: String,
    val synopsis: String,
    val imageUrl: String,
    val score: Double,
    val status: String,
    val type: String,
    val episodes: Int,
    val year: Int
)

sealed class AnimeDetailUiState {
    object Loading : AnimeDetailUiState()
    data class Success(val anime: AnimeDetailUiModel) : AnimeDetailUiState()
    data class Error(val message: String) : AnimeDetailUiState()
}
