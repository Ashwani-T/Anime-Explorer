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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
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


    init {
        observeSearchQuery()
    }

    @OptIn(FlowPreview::class)
    fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQueryState
                .debounce(300)
                .collect {
                    _uiState.update { state->
                        state.copy(animeList = emptyList(), pageNumber = 1, isLoading = true)
                    }
                    loadAnime()
                }
        }
    }
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQueryState.value = query
    }

    fun onSortTypeChange(sortType: SortType) {
        _uiState.update { it.copy(selectedSort = sortType) }
    }

    fun toggleSortOrder() {
        _uiState.update {
            it.copy(sortOrder = if (it.sortOrder == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC)
        }
        onApplyFilter()

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
            Log.d("Ashwani", "ui state =  ${uiState.value.animeList.size}")
            val result = animeRepository.getFilteredAnime(
                query = currentState.searchQuery,
                page = currentState.pageNumber,
                orderBy = currentState.selectedSort,
                sortOrder = currentState.sortOrder,
                format = currentState.selectedFormat,
                status = currentState.selectedStatus,
                rating = currentState.selectedRating,
                genres = currentState.selectedGenres
            )

            result.onSuccess { response ->
                Log.d("TAG", "loadAnime: ${response.pagination}")
                val filteredList = (uiState.value.animeList + response.data).distinctBy { it.id }
                _uiState.update {
                    it.copy(
                        animeList = filteredList,
                        hasNextPage = response.pagination.hasNextPage,
                        isLoading = false
                    )
                }

            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, pageNumber = it.pageNumber - 1) }
                Log.d("ANIME EXPLORER", "${error.message}")
            }

        }
    }

    fun onApplyFilter(){
        _uiState.update { state ->
            state.copy(animeList = emptyList(), isLoading = true)
        }

        loadAnime()
    }

    fun onLoadMore() {
        Log.d("ANIME EXPLORER", "laoding entered: ")


        if (!(uiState.value.isLoading) && uiState.value.hasNextPage) {
            _uiState.update { it.copy(isLoading = true, pageNumber = it.pageNumber + 1) }
            loadAnime()
        } else {
            Log.d("TAG", "onLoadMore: REACHED END OF LIST")
        }

    }

    fun resetAndReloadAnime(){
        _uiState.value = SearchUiState(searchQuery = _searchQueryState.value, isLoading = true)
        loadAnime()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TAG", "VIEWMODEL DESTROYED: SearchViewModel")
    }

}
