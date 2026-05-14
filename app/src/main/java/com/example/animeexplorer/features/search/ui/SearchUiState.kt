package com.example.animeexplorer.features.search.ui

import com.example.animeexplorer.core.domain.AnimeUiModel
import com.example.animeexplorer.core.domain.enums.FormatType
import com.example.animeexplorer.core.domain.enums.RatingType
import com.example.animeexplorer.core.domain.enums.SortOrder
import com.example.animeexplorer.core.domain.enums.SortType
import com.example.animeexplorer.core.domain.enums.StatusType
import com.example.animeexplorer.features.search.domain.model.GenreModel
import com.example.animeexplorer.features.search.domain.model.genreList

data class SearchUiState(
    val searchQuery: String = "",
    val hasNextPage: Boolean = true,
    val pageNumber: Int = 1,
    val animeList: List<AnimeUiModel> = emptyList(),
    val selectedSort: SortType? = null,
    val sortOrder: SortOrder = SortOrder.DESC,
    val selectedFormat: FormatType? = null,
    val selectedStatus: StatusType? = null,
    val selectedRating: RatingType? = null,
    val selectedGenres: Set<GenreModel> = emptySet(),
    val availableGenres: List<GenreModel> = genreList,
    val isLoading: Boolean = false
)
