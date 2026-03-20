package com.example.animeexplorer.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.domain.AnimeRepository
import com.example.animeexplorer.domain.AnimeUiModel
import com.example.animeexplorer.domain.enums.FormatType
import com.example.animeexplorer.domain.enums.RatingType
import com.example.animeexplorer.domain.enums.SortOrder
import com.example.animeexplorer.domain.enums.SortType
import com.example.animeexplorer.domain.enums.StatusType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GenreModel(
    val malId: Int,
    val name: String
)

data class SearchUiState(
    val searchQuery: String = "",
    val animeList: List<AnimeUiModel> = emptyList(),
    val selectedSort: SortType? = null,
    val sortOrder: SortOrder = SortOrder.DESC,
    val selectedFormat: FormatType? = null,
    val selectedStatus: StatusType? = null,
    val selectedRating: RatingType? = null,
    val selectedGenres: Set<GenreModel> = emptySet(),
    val availableGenres: List<GenreModel> = emptyList(),
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val animeRepository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()


    init {
        loadGenres()
        loadAnime()
        observeUiStateChange()
    }

    @OptIn(FlowPreview::class)
    fun observeUiStateChange(){
        viewModelScope.launch {
            uiState
                .map { it.searchQuery }
                .distinctUntilChanged()
                .debounce { 200 }
                .collectLatest { loadAnime() }
        }
    }

    private fun loadGenres() {
        val mockGenres = listOf(
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
        _uiState.update { it.copy(availableGenres = mockGenres) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSortTypeChange(sortType: SortType) {
        _uiState.update { it.copy(selectedSort = sortType) }
    }

    fun toggleSortOrder() {
        _uiState.update {
            it.copy(sortOrder = if (it.sortOrder == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC)
        }
    }

    fun onFormatChange(format: FormatType?) {
        _uiState.update { it.copy(selectedFormat = format) }
    }

    fun onStatusChange(status: StatusType?) {
        _uiState.update { it.copy(selectedStatus = status) }
    }

    fun onRatingChange(rating: RatingType?) {
        _uiState.update { it.copy(selectedRating = rating) }
    }

    fun toggleGenre(genre: GenreModel) {
        _uiState.update { state ->
            val isSelected = state.selectedGenres.any { it.malId == genre.malId }
            val newGenres = if (isSelected) {
                state.selectedGenres.filterNot { it.malId == genre.malId }.toSet()
            } else {
                state.selectedGenres + genre
            }
            state.copy(selectedGenres = newGenres)
        }
    }


    fun resetFilters() {
        _uiState.update {
            it.copy(
                selectedSort = null,
                sortOrder = SortOrder.DESC,
                selectedFormat = null,
                selectedStatus = null,
                selectedRating = null,
                selectedGenres = emptySet()
            )
        }
    }

    fun loadAnime() {
        viewModelScope.launch {
            val currentState = uiState.value
            Log.d("Ashwani", "ui state =  ${uiState.value}")
            val result = animeRepository.getFilteredAnime(
                currentState.searchQuery,
                currentState.selectedSort,
                currentState.sortOrder,
                currentState.selectedFormat,
                currentState.selectedStatus,
                currentState.selectedRating,
                currentState.selectedGenres
            )

            result.onSuccess {animeList ->
                val filteredList = animeList.distinctBy { it.id }
                _uiState.update { it.copy(animeList = filteredList)}
            }.onFailure { error ->
                Log.d("ANIME EXPLORER", "${error.message}")
            }

        }
    }
}
