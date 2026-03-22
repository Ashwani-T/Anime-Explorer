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


val genreList = listOf(
    GenreModel(1, "Action"),
    GenreModel(2, "Adventure"),
    GenreModel(4, "Comedy"),
    GenreModel(8, "Drama"),
    GenreModel(10, "Fantasy"),
    GenreModel(14, "Horror"),
    GenreModel(7, "Mystery"),
    GenreModel(22, "Romance"),
    GenreModel(24, "Sci-Fi"),
    GenreModel(36, "Slice of Life")
)
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
