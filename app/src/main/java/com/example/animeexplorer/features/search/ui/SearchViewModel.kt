package com.example.animeexplorer.features.search.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.core.domain.enums.FormatType
import com.example.animeexplorer.core.domain.enums.RatingType
import com.example.animeexplorer.core.domain.enums.SortOrder
import com.example.animeexplorer.core.domain.enums.SortType
import com.example.animeexplorer.core.domain.enums.StatusType
import com.example.animeexplorer.features.search.domain.SearchRepository
import com.example.animeexplorer.features.search.domain.mapper.toQueryMap
import com.example.animeexplorer.features.search.domain.mapper.toRequestParams
import com.example.animeexplorer.features.search.domain.model.GenreModel
import com.example.animeexplorer.features.search.domain.model.SearchRequestParamModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.any
import kotlin.collections.distinctBy
import kotlin.collections.emptyList
import kotlin.collections.emptySet
import kotlin.collections.filterNot
import kotlin.collections.plus
import kotlin.collections.toSet
import kotlin.sequences.toSet
import kotlin.text.toSet

private const val SEARCH_QUERY_KEY = "search_query"

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Restoring saved search query
        val savedQuery = savedStateHandle.get<String>(SEARCH_QUERY_KEY) ?: ""
        Log.d("SearchViewModel", "Restored search query: '$savedQuery'")

        _uiState.update { it.copy(searchQuery = savedQuery) }

        viewModelScope.launch {
            _uiState
                .map {it.searchQuery}
                .distinctUntilChanged()
                .debounce(300)
                .collect {
                    onApplyFilter()
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        savedStateHandle[SEARCH_QUERY_KEY] = query
        Log.d("SearchViewModel", "Saved search query: '$query'")
    }

    fun onSortTypeChange(sortType: SortType?) {
        _uiState.update { it.copy(selectedSort = sortType) }
        //onApplyFilter()
    }

    fun onFormatChange(format: FormatType?) {
        _uiState.update { it.copy(selectedFormat = format) }
        //onApplyFilter()
    }

    fun onStatusChange(status: StatusType?) {
        _uiState.update { it.copy(selectedStatus = status) }
        //onApplyFilter()
    }

    fun onRatingChange(rating: RatingType?) {
        _uiState.update { it.copy(selectedRating = rating) }
        //onApplyFilter()
    }

    fun removeGenre(genre: GenreModel) {
        _uiState.update { state ->
            val newGenres = state.selectedGenres.filterNot { it.malId == genre.malId }.toSet()
            state.copy(selectedGenres = newGenres)
        }
        //onApplyFilter()
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
        searchJob?.cancel()

        _uiState.update { state ->
            state.copy(isLoading = true)
        }

        searchJob = viewModelScope.launch {
            val currentState = uiState.value
            val requestedPage = currentState.pageNumber

            val queryMap = getRequestParams(currentState, requestedPage).toQueryMap()

            
            val result = searchRepository.getFilteredAnime(
                queryMap
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
    private fun getRequestParams(currentState: SearchUiState, requestedPage: Int): SearchRequestParamModel{
        return currentState.toRequestParams().copy(page = requestedPage)
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