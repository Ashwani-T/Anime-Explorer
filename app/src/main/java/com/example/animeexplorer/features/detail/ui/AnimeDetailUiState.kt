package com.example.animeexplorer.features.detail.ui

import com.example.animeexplorer.core.domain.AnimeDetailUiModel

sealed class AnimeDetailUiState {
    data object Loading : AnimeDetailUiState()
    data class Success(val anime: AnimeDetailUiModel) : AnimeDetailUiState()
    data class Error(val message: String) : AnimeDetailUiState()
}