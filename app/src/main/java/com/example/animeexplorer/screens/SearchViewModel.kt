package com.example.animeexplorer.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.domain.AnimeRepository
import com.example.animeexplorer.domain.enums.FormatType
import com.example.animeexplorer.domain.enums.RatingType
import com.example.animeexplorer.domain.enums.SortOrder
import com.example.animeexplorer.domain.enums.SortType
import com.example.animeexplorer.domain.enums.StatusType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val animeRepository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()


    private val _searchQueryState = MutableStateFlow("")

    private var searchJob: Job? = null


    init {
        observeSearchQuery()
    }

    @OptIn(FlowPreview::class)
    fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQueryState
                .debounce(300)
                .collect {
                    onApplyFilter()
                }
        }
    }
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQueryState.value = query
    }

    fun onSortTypeChange(sortType: SortType?) {
        _uiState.update { it.copy(selectedSort = sortType) }
        onApplyFilter()
    }

    fun onFormatChange(format: FormatType?) {
        _uiState.update { it.copy(selectedFormat = format) }
        onApplyFilter()
    }

    fun onStatusChange(status: StatusType?) {
        _uiState.update { it.copy(selectedStatus = status) }
        onApplyFilter()
    }

    fun onRatingChange(rating: RatingType?) {
        _uiState.update { it.copy(selectedRating = rating) }
        onApplyFilter()
    }

    fun removeGenre(genre: GenreModel) {
        _uiState.update { state ->
            val newGenres = state.selectedGenres.filterNot { it.malId == genre.malId }.toSet()
            state.copy(selectedGenres = newGenres)
        }
        onApplyFilter()
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
    fun toggleSortOrder() {
        _uiState.update {
            it.copy(sortOrder = if (it.sortOrder == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC)
        }
        onApplyFilter()

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
        onApplyFilter()
    }

    fun loadAnime() {
        searchJob?.cancel().also {
            _uiState.update { state ->
                state.copy(isLoading = true)
            }
        }
        searchJob = viewModelScope.launch {
            val currentState = uiState.value
            val requestedPage = currentState.pageNumber
            
            val result = animeRepository.getFilteredAnime(
                query = currentState.searchQuery,
                page = requestedPage,
                orderBy = currentState.selectedSort,
                sortOrder = currentState.sortOrder,
                format = currentState.selectedFormat,
                status = currentState.selectedStatus,
                rating = currentState.selectedRating,
                genres = currentState.selectedGenres
            )

            result.onSuccess { response ->
                _uiState.update { state ->
                    val newList = if (requestedPage == 1) {
                        response.data
                    } else {
                        state.animeList + response.data
                    }
                    state.copy(
                        animeList = newList.distinctBy { it.id },
                        hasNextPage = response.pagination.hasNextPage,
                        isLoading = false
                    )
                }

            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        pageNumber = if (it.pageNumber > 1) it.pageNumber - 1 else 1 
                    ) 
                }
                Log.d("ANIME EXPLORER", "${error.message}")
            }

        }
    }

    fun onApplyFilter() {
        _uiState.update { state ->
            state.copy(animeList = emptyList(), pageNumber = 1, isLoading = true)
        }
        loadAnime()
    }

    fun onLoadMore() {
        if (!(uiState.value.isLoading) && uiState.value.hasNextPage) {
            _uiState.update { it.copy(isLoading = true, pageNumber = it.pageNumber + 1) }
            loadAnime()
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        Log.d("TAG", "VIEWMODEL DESTROYED: SearchViewModel")
    }

}
