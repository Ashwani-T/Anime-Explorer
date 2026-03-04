package com.example.animeexplorer.screens

import com.example.animeexplorer.data.AnimeDto


fun AnimeDto.toUiModel(): AnimeUiModel {
    return AnimeUiModel(
        id = id,
        title = title?:"No Title" ,
        description = description?:"No description",
        duration = duration?:"No duration",
        imageUrl = imageUrl.webp.imageUrl?:"No Image",
        type = type?:"No Type"
    )
}


data class AnimeUiModel(
    val id: Int,
    val title: String,
    val description: String ,
    val duration: String ,
    val imageUrl: String ,
    val type: String
)



sealed class AnimeUiState {
    object Loading : AnimeUiState()
    data class Success(
        val animeUiModel: List<AnimeUiModel>,
        val isLoadingMore: Boolean = false
    ) : AnimeUiState()
    data class Error(
        val animeUiModel: List<AnimeUiModel> = emptyList(),
        val message: String) : AnimeUiState()
}