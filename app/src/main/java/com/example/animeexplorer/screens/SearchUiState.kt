package com.example.animeexplorer.screens

import com.example.animeexplorer.domain.AnimeUiModel
import com.example.animeexplorer.domain.enums.FormatType
import com.example.animeexplorer.domain.enums.RatingType
import com.example.animeexplorer.domain.enums.SortOrder
import com.example.animeexplorer.domain.enums.SortType
import com.example.animeexplorer.domain.enums.StatusType

data class GenreModel(
    val malId: Int,
    val name: String
)

data class SearchUiState(
    val searchQuery: String = "",
    val pageNumber: Int = 1,
    val animeList: List<AnimeUiModel> = emptyList(),
    val selectedSort: SortType? = null,
    val sortOrder: SortOrder = SortOrder.DESC,
    val selectedFormat: FormatType? = null,
    val selectedStatus: StatusType? = null,
    val selectedRating: RatingType? = null,
    val selectedGenres: Set<GenreModel> = emptySet(),
    val availableGenres: List<GenreModel> = emptyList(),
    val isLoading: Boolean = false
)
