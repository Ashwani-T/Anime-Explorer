package com.example.animeexplorer.features.detail.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.animeexplorer.navigation.HomeDestination
import com.example.animeexplorer.core.domain.AnimeCollectionUiModel
import com.example.animeexplorer.features.collection.data.local.entity.LibraryStatus
import com.example.animeexplorer.features.collection.domain.AnimeCollectionRepository
import com.example.animeexplorer.features.detail.domain.DetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    private val repository: DetailRepository,
    private val collectionRepository: AnimeCollectionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val malId: Int = savedStateHandle.toRoute<HomeDestination.AnimeDetail>().malId

    private val _uiState = MutableStateFlow<AnimeDetailUiState>(AnimeDetailUiState.Loading)
    val uiState: StateFlow<AnimeDetailUiState> = _uiState.asStateFlow()

    private val _collectionState = MutableStateFlow<AnimeCollectionUiModel?>(null)
    val collectionState: StateFlow<AnimeCollectionUiModel?> = _collectionState.asStateFlow()

    init {
        loadAnimeDetail()
        loadCollectionStatus()
    }

    fun loadAnimeDetail() {
        viewModelScope.launch {
            _uiState.value = AnimeDetailUiState.Loading

            val response = repository.getAnimeDetail(malId)

            response.fold(
                onSuccess = { detailResponse ->
                    _uiState.value = AnimeDetailUiState.Success(
                        anime = detailResponse
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

    fun loadCollectionStatus() {
        viewModelScope.launch {
            try {
                val result = collectionRepository.getLibraryCollection(malId)
                result.fold(
                    onSuccess = { collection ->
                        _collectionState.value = collection
                        Log.d("AnimeDetailVM", "Collection loaded: ${collection?.title}")
                    },
                    onFailure = { exception ->
                        _collectionState.value = null
                        Log.d("AnimeDetailVM", "Anime not in collection: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _collectionState.value = null
                Log.e("AnimeDetailVM", "Error loading collection: ${e.message}", e)
            }
        }
    }

    fun retry() {
        loadAnimeDetail()
    }

    fun addToCollection(status: LibraryStatus, episodes: Int) {
        viewModelScope.launch {
            try {
                collectionRepository.addToLibrary(malId, status).fold(
                    onSuccess = {
                        Log.d("TAG", "addToCollection: $status")
                        collectionRepository.updateEpisodesCompleted(malId, episodes)
                        loadCollectionStatus()
                        Log.d("AnimeDetailVM", "Added to collection with status: $status, episodes: $episodes")
                    },
                    onFailure = { exception ->
                        Log.e("AnimeDetailVM", "Error adding to collection: ${exception.message}", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("AnimeDetailVM", "Error: ${e.message}", e)
            }
        }
    }

    fun updateCollection(status: LibraryStatus, episodes: Int) {
        viewModelScope.launch {
            try {
                collectionRepository.updateLibraryStatus(malId, status).fold(
                    onSuccess = {
                        if (episodes >= 0) {
                            collectionRepository.updateEpisodesCompleted(malId, episodes)
                        }
                        loadCollectionStatus()
                        Log.d("AnimeDetailVM", "Updated collection with status: $status, episodes: $episodes")
                    },
                    onFailure = { exception ->
                        Log.e("AnimeDetailVM", "Error updating collection: ${exception.message}", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("AnimeDetailVM", "Error: ${e.message}", e)
            }
        }
    }

    fun removeFromCollection() {
        viewModelScope.launch {
            try {
                collectionRepository.removeFromLibrary(malId).fold(
                    onSuccess = {
                        _collectionState.value = null
                        Log.d("AnimeDetailVM", "Removed from collection")
                    },
                    onFailure = { exception ->
                        Log.e("AnimeDetailVM", "Error removing from collection: ${exception.message}", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("AnimeDetailVM", "Error: ${e.message}", e)
            }
        }
    }
}