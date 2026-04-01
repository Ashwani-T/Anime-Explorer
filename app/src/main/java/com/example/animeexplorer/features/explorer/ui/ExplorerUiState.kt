package com.example.animeexplorer.features.explorer.ui

import com.example.animeexplorer.core.domain.AnimeUiModel
import com.example.animeexplorer.features.explorer.domain.ExplorerCategory

data class ExplorerUiState(
    val category: ExplorerCategory = ExplorerCategory.TRENDING,
    val animeList: List<AnimeUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val hasNextPage: Boolean = false,
    val currentPage: Int = 1,
    val error: String? = null
)

