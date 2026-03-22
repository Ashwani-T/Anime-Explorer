package com.example.animeexplorer.screens

import com.example.animeexplorer.data.local.entity.LibraryStatus
import com.example.animeexplorer.domain.AnimeCollectionUiModel

data class AnimeLibraryUiState(
    val isLoading: Boolean = false,
    val allCollections: List<AnimeCollectionUiModel> = emptyList(),
    val collections: List<AnimeCollectionUiModel> = emptyList(),
    val error: String? = null,
    val selectedFilter: LibraryStatus? = null,
    val selectedPreset: String = "Default"
)

