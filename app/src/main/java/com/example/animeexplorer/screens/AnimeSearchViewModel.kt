package com.example.animeexplorer.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.domain.AnimeRepository
import com.example.animeexplorer.domain.AnimeResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AnimeSearchViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {


    private var currentPage = 1
    private var hasNextPage = true
    private var searchJob: Job? = null


    // Home Ui State for supplying the results to Home Screen



    init {
        viewModelScope.launch {
            searchQuery
                .debounce(400)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .collectLatest { searchQuery ->
                    searchAnime(query = searchQuery, page = currentPage)
                }
        }
    }

    fun loadNextPage() {
        if (searchUiState.value.isLoading) return
        _searchUiState.update { state ->
            state.copy(isLoading = true, errorMessage = null)
        }
        searchAnime(query = searchUiState.value.query, page = currentPage)
    }

    fun cancelSearch() {
        searchJob?.cancel()
        searchJob = null
        _searchUiState.update { state ->
            state.copy(isLoading = false, errorMessage = null)
        }
    }

    fun retry() {
        if (searchUiState.value.query.isNotBlank()) {
            loadNextPage()
        }
    }

    fun onQueryChange(newQuery: String) {
        searchJob?.cancel()
        currentPage = 1
        if(newQuery.isBlank()) {
             _searchUiState.update { state ->
                 state.copy(query = newQuery, isLoading = false, errorMessage = null)
             }
        } else {
            searchQuery.value = newQuery
        }
        _searchUiState.update { state ->
            state.copy(query = newQuery, isLoading = true, errorMessage = null)
        }
    }


    private fun searchAnime(query: String, page: Int) {

        searchJob = viewModelScope.launch {
            var response: AnimeResponseModel? = null
            runCatching {
                repository.searchAnime(query, page)
            }.onSuccess { searchResponse ->
                response = searchResponse
            }.onFailure { exception ->
                coroutineContext.ensureActive()
                Log.d("ExceptionInRepoSearch", "${exception.message}")
            }

            if (response != null) {
                _searchUiState.update { state ->
                    val updatedList = if (page == 1) {
                        response.data
                    } else {
                        state.animeList + response.data
                    }
                    state.copy(animeList = updatedList, isLoading = false, errorMessage = null)
                }
                currentPage = response.pagination.currentPage + 1
                hasNextPage = response.pagination.hasNextPage
            } else {
                _searchUiState.update { state ->
                    state.copy(isLoading = false, errorMessage = "Failed to load search results")
                }
            }
        }
    }
}