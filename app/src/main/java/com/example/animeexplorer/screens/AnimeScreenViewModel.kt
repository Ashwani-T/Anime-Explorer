package com.example.animeexplorer.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.data.toUiModel
import com.example.animeexplorer.domain.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AnimeScreenViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeUiState>(AnimeUiState.Loading)
    val uiState: StateFlow<AnimeUiState> = _uiState

    private var currentPage = 1
    private var hasNextPage = true

    private var paginationJob: Job? = null


    private fun loadNextPage() {

        val currentList = when (val currentState = _uiState.value) {
            is AnimeUiState.Success -> currentState.animeUiModel
            is AnimeUiState.Error -> currentState.animeUiModel
            is AnimeUiState.Loading -> emptyList()
        }

        _uiState.update { state ->
            when (state) {
                is AnimeUiState.Success -> state.copy(isLoadingMore = true)
                is AnimeUiState.Error -> state.copy(animeUiModel = currentList, isLoading = true, message = "Loading....")
                is AnimeUiState.Loading -> state
            }
        }
        paginationJob = viewModelScope.launch {

            // not using withContext because retrofit already runs on a background thread,
            // and using withContext would unnecessarily switch back to the main thread before making the API call

            val response = repository.getAnimeList(currentPage)

            _uiState.value = AnimeUiState.Success(animeUiModel = currentList + response.data)
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