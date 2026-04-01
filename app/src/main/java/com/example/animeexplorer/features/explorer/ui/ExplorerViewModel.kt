package com.example.animeexplorer.features.explorer.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.features.explorer.domain.ExplorerCategory
import com.example.animeexplorer.features.explorer.domain.ExplorerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExplorerViewModel @Inject constructor(
    private val explorerRepository: ExplorerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExplorerUiState())
    val uiState: StateFlow<ExplorerUiState> = _uiState.asStateFlow()

    fun initializeCategory(category: ExplorerCategory) {
        _uiState.update {
            it.copy(
                category = category,
                animeList = emptyList(),
                currentPage = 1,
                hasNextPage = false
            )
        }
        loadAnime()
    }

    fun loadAnime() {
        val currentState = _uiState.value
        if (currentState.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            explorerRepository.getAnimeByCategory(
                category = currentState.category,
                page = currentState.currentPage
            ).onSuccess { response ->
                _uiState.update { state ->
                    val newList = if (currentState.currentPage == 1) {
                        response.data
                    } else {
                        state.animeList + response.data
                    }
                    state.copy(
                        animeList = newList.distinctBy { it.id },
                        hasNextPage = response.pagination.hasNextPage,
                        isLoading = false,
                        currentPage = currentState.currentPage
                    )
                }
                Log.d(
                    "ExplorerViewModel",
                    "Loaded page ${currentState.currentPage}: ${response.data.size} items"
                )
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
                Log.e("ExplorerViewModel", "Error loading anime: ${exception.message}", exception)
            }
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoading || !currentState.hasNextPage) return

        _uiState.update { it.copy(currentPage = it.currentPage + 1) }
        loadAnime()
    }
}

