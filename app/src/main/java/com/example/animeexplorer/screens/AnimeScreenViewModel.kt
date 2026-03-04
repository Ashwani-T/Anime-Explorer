package com.example.animeexplorer.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.domain.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AnimeScreenViewModel @Inject constructor(
    private val repository: AnimeRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<AnimeUiState>(AnimeUiState.Loading)
    val uiState: StateFlow<AnimeUiState> = _uiState

    private var currentPage = 1
    private var isLoading = false
    private var hasNextPage = true


    init {
        loadAnimeList()
    }
    fun loadAnimeList(){
        if(isLoading || !hasNextPage){
            return
        }

        viewModelScope.launch {
            isLoading = true

            val currentList = when(_uiState.value){
                is AnimeUiState.Success -> (_uiState.value as AnimeUiState.Success).animeUiModel
                is AnimeUiState.Error -> (_uiState.value as AnimeUiState.Error).animeUiModel
                is AnimeUiState.Loading -> emptyList()
            }

            _uiState.value = AnimeUiState.Success(
                animeUiModel = currentList,
                isLoadingMore = true
            )
            delay(1000)
            val response = repository.getAnimeList(currentPage)

            response.fold(
                onSuccess = { animeResponse ->
                    val newItem = animeResponse.data.map { it.toUiModel() }
                    _uiState.value = AnimeUiState.Success(
                        animeUiModel = (currentList + newItem),
                        isLoadingMore = false
                    )
                    currentPage++
                    hasNextPage = animeResponse.pagination.hasNextPage
                },
                onFailure = { exception ->
                    _uiState.value = AnimeUiState.Error(animeUiModel = currentList, message = exception.message ?: "Unknown error")
                }
            )

            isLoading = false
        }
    }
    fun onRetry() {
        loadAnimeList()
    }
}