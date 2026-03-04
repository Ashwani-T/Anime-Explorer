package com.example.animeexplorer.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.animeexplorer.AppDestination
import com.example.animeexplorer.data.toDetailUiModel
import com.example.animeexplorer.domain.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    private val repository: AnimeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val malId: Int = savedStateHandle.toRoute<AppDestination.AnimeDetail>().malId

    private val _uiState = MutableStateFlow<AnimeDetailUiState>(AnimeDetailUiState.Loading)
    val uiState: StateFlow<AnimeDetailUiState> = _uiState

    init {
        loadAnimeDetail()
    }

    fun loadAnimeDetail() {
        viewModelScope.launch {
            _uiState.value = AnimeDetailUiState.Loading

            val response = repository.getAnimeDetail(malId)

            response.fold(
                onSuccess = { detailResponse ->
                    _uiState.value = AnimeDetailUiState.Success(
                        anime = detailResponse.data.toDetailUiModel()
                    )
                },
                onFailure = { exception ->
                    _uiState.value = AnimeDetailUiState.Error(
                        exception.message ?: "Unknown error"
                    )
                }
            )
        }
    }

    fun retry() {
        loadAnimeDetail()
    }
}
