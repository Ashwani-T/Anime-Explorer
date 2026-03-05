package com.example.animeexplorer.screens

import com.example.animeexplorer.data.AnimeDto
import com.example.animeexplorer.domain.AnimeUiModel








sealed class AnimeUiState {
    object Loading : AnimeUiState()
    data class Success(
        val animeUiModel: List<AnimeUiModel>,
        val isLoadingMore: Boolean = false
    ) : AnimeUiState()
    data class Error(
        val animeUiModel: List<AnimeUiModel> = emptyList(),
        val isLoading: Boolean = false,
        val message: String) : AnimeUiState()
}