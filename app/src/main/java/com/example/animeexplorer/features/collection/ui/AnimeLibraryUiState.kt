package com.example.animeexplorer.features.collection.ui

import com.example.animeexplorer.features.collection.data.local.entity.LibraryStatus
import com.example.animeexplorer.core.domain.AnimeCollectionUiModel

data class AnimeLibraryUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val allCollections: List<AnimeCollectionUiModel> = emptyList(),
    val collections: List<AnimeCollectionUiModel> = emptyList(),
    val error: String? = null,
    val selectedFilter: LibraryStatus? = null,
    val selectedPreset: String = "collections"
)
