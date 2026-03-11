package com.example.animeexplorer.screens

import com.example.animeexplorer.domain.AnimeUiModel


data class HomeUiState(
    val query: String = "",
    val animeList: List<AnimeUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val endReached: Boolean = false
)