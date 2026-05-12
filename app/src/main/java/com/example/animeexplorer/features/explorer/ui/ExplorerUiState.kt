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
) {
    val contentState: ExplorerContentState
        get() = when {
            isLoading && animeList.isEmpty() -> ExplorerContentState.Loading
            error != null && animeList.isEmpty() -> ExplorerContentState.Error
            animeList.isEmpty() -> ExplorerContentState.Empty
            else -> ExplorerContentState.Content(
                animeList = animeList,
                isLoadingNextPage = isLoading
            )
        }
}

sealed interface ExplorerContentState {
    data object Loading : ExplorerContentState
    data object Error : ExplorerContentState
    data object Empty : ExplorerContentState
    data class Content(
        val animeList: List<AnimeUiModel>,
        val isLoadingNextPage: Boolean
    ) : ExplorerContentState
}
