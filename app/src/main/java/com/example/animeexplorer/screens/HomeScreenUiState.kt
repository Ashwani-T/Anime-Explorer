package com.example.animeexplorer.screens

import com.example.animeexplorer.domain.AnimeUiModel

data class HomeSection(
    val items: List<AnimeUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
data class HomeUiState(
    val query: String = "",
    val horizontalPager: List<AnimeUiModel> = emptyList(),
    val recentlyReleasedEpisodes: List<AnimeUiModel> = emptyList(),
    val trending: HomeSection = HomeSection(),
    val currentSeason: HomeSection = HomeSection(),
    val top: HomeSection = HomeSection(),
    val upcoming: HomeSection = HomeSection(),
    val favorites: HomeSection = HomeSection(),

    val isRefreshing: Boolean = false
)