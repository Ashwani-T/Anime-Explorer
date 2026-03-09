package com.example.animeexplorer.screens

import com.example.animeexplorer.domain.AnimeUiModel


sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val homeUiModel: List<AnimeUiModel>,
        val isLoadingMore: Boolean = false
    ) : HomeUiState()
    data class Error(
        val homeUiModel: List<AnimeUiModel> = emptyList(),
        val isLoading: Boolean = false,
        val message: String) : HomeUiState()
}