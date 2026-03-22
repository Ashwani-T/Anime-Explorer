package com.example.animeexplorer.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.data.local.entity.LibraryStatus
import com.example.animeexplorer.domain.AnimeCollectionRepository
import com.example.animeexplorer.domain.AnimeCollectionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AnimeLibraryViewModel @Inject constructor(
    private val collectionRepository: AnimeCollectionRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(AnimeLibraryUiState())
    val uiState: StateFlow<AnimeLibraryUiState> = _uiState.asStateFlow()
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    init {
        loadLibraryCollections()
         listenToCollectionUpdates()
    }

    fun loadLibraryCollections() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = collectionRepository.getAllLibraryCollections()
                result.fold(
                    onSuccess = { collections ->
                        _uiState.update { state ->
                            state.copy(
                                allCollections = collections,
                                collections = applyCurrentFilter(collections, state.selectedFilter),
                                isLoading = false,
                                error = null
                            )
                        }
                        Log.d("AnimeLibraryVM", "Loaded ${collections.size} collections")
                    },
                    onFailure = { exception ->
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to load collections"
                            )
                        }
                        Log.e("AnimeLibraryVM", "Error loading collections: ${exception.message}", exception)
                    }
                )
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = "Unexpected error: ${e.message}"
                    )
                }
                Log.e("AnimeLibraryVM", "Unexpected error: ${e.message}", e)
            }
        }
    }

    // NEW: Listen to collection update events from repository
    private fun listenToCollectionUpdates() {
        viewModelScope.launch {
            collectionRepository.getCollectionUpdates().collectLatest { event ->
                Log.d("AnimeLibraryVM", "Collection update event received: $event")
                // Reload collections when any update occurs
                loadLibraryCollections()
            }
        }
    }

    // ...existing code...
    fun filterByStatus(status: LibraryStatus) {
        _uiState.update { state ->
            val filtered = applyCurrentFilter(state.allCollections, status)
            state.copy(
                selectedFilter = status,
                collections = filtered,
                selectedPreset = status.name.replace("_", " ")
                    .lowercase()
                    .replaceFirstChar { it.uppercase() }
            )
        }
        Log.d("AnimeLibraryVM", "Filtered by status: $status, found ${uiState.value.collections.size} items")
    }

    fun clearFilter() {
        _uiState.update { state ->
            state.copy(
                selectedFilter = null,
                collections = state.allCollections,
                selectedPreset = "Default"
            )
        }
        Log.d("AnimeLibraryVM", "Cleared filter, showing all ${uiState.value.collections.size} collections")
    }

    fun setPreset(presetName: String) {
        when (presetName) {
            "Default" -> clearFilter()
            "Watching" -> filterByStatus(LibraryStatus.WATCHING)
            "On Hold" -> filterByStatus(LibraryStatus.ON_Hold)
            "Completed" -> filterByStatus(LibraryStatus.COMPLETED)
            "Watch Later" -> filterByStatus(LibraryStatus.WATCH_LATER)
        }
    }


    private fun applyCurrentFilter(
        collections: List<AnimeCollectionUiModel>,
        selectedFilter: LibraryStatus?
    ): List<AnimeCollectionUiModel> {
        return if (selectedFilter != null) {
            collections.filter { it.status == selectedFilter }
        } else {
            collections
        }
    }
}