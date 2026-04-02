package com.example.animeexplorer.features.episodes.ui

import com.example.animeexplorer.features.episodes.domain.AnimeEpisodeUiModel

sealed class AnimeEpisodeListUiState {
    data object Loading : AnimeEpisodeListUiState()
    data class Success(val episodes: List<AnimeEpisodeUiModel>) : AnimeEpisodeListUiState()
    data class Error(val message: String) : AnimeEpisodeListUiState()
}

