package com.example.animeexplorer.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.domain.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {


    // Home Ui State for supplying random anime results to Home Screen
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    // Search Ui State for the search bar only
    private val _searchUiState = MutableStateFlow(AnimeSearchUiState())
    val searchUiState: StateFlow<AnimeSearchUiState> = _searchUiState.asStateFlow()
    private val searchQuery = MutableStateFlow("")

    // Pagination variable
    private var currentPage = 1
    private var hasNextPage = true
    private var paginationJob: Job? = null

    private fun loadNextPage() {

        val currentList = when (val currentState = _uiState.value) {
            is HomeUiState.Success -> currentState.homeUiModel
            is HomeUiState.Error -> currentState.homeUiModel
            is HomeUiState.Loading -> emptyList()
        }

        _uiState.update { state ->
            when (state) {
                is HomeUiState.Success -> state.copy(isLoadingMore = true)
                is HomeUiState.Error -> state.copy(homeUiModel = currentList, isLoading = true, message = "Loading....")
                is HomeUiState.Loading -> state
            }
        }
        paginationJob = viewModelScope.launch {

            // not using withContext because retrofit already runs on a background thread,
            // and using withContext would unnecessarily switch back to the main thread before making the API call

            val response = repository.getAnimeList(currentPage)

            _uiState.value = HomeUiState.Success(homeUiModel = currentList + response.data)
            if(response.data.isNotEmpty()) {
                currentPage = response.pagination.currentPage + 1
                hasNextPage = response.pagination.hasNextPage
            }

            Log.d("viewmodelscope", "loadNextPage: $response")
        }
    }

    fun runAnimeListJob() {
        if (paginationJob?.isActive == true) return
        Log.d("ViewModel", "runAnimeListJob ")
        loadNextPage()
    }

    fun stopAnimeListJob() {
        if (paginationJob?.isActive == true) {
            paginationJob!!.cancel()
            Log.d("viewmodel", "stopAnimeListJob")
        }

    }

    fun onRetry() {
        hasNextPage = true
        runAnimeListJob()
    }
}