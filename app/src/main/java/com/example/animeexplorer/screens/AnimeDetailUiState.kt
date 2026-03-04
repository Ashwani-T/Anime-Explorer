package com.example.animeexplorer.screens

import com.example.animeexplorer.data.AnimeDetailDto
import com.example.animeexplorer.domain.AnimeDetailUiModel



sealed class AnimeDetailUiState {
    object Loading : AnimeDetailUiState()
    data class Success(val anime: AnimeDetailUiModel) : AnimeDetailUiState()
    data class Error(val message: String) : AnimeDetailUiState()
}
