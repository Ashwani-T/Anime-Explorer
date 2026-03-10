package com.example.animeexplorer.screens

import com.example.animeexplorer.domain.AnimeUiModel

data class AnimeSearchUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val animeList: List<AnimeUiModel> = emptyList(),
    val errorMessage: String? = null
)