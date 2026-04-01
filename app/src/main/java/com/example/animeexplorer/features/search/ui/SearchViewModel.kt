package com.example.animeexplorer.features.search.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.features.search.domain.SearchRepository
import com.example.animeexplorer.core.domain.enums.FormatType
import com.example.animeexplorer.core.domain.enums.RatingType
import com.example.animeexplorer.core.domain.enums.SortOrder
import com.example.animeexplorer.core.domain.enums.SortType
import com.example.animeexplorer.core.domain.enums.StatusType
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

private const val SEARCH_QUERY_KEY = "search_query"

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQueryState = MutableStateFlow("")

    private var searchJob: Job? = null

    init {
        // Restore saved search query on initialization
        val savedQuery = savedStateHandle.get<String>(SEARCH_QUERY_KEY) ?: ""
        Log.d("SearchViewModel", "Restored search query: '$savedQuery'")

        _uiState.update { it.copy(searchQuery = savedQuery) }
        _searchQueryState.value = savedQuery

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
        // Save query to SavedStateHandle whenever it changes
        savedStateHandle[SEARCH_QUERY_KEY] = query
        Log.d("SearchViewModel", "Saved search query: '$query'")
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
            
            val result = searchRepository.getFilteredAnime(
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