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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AnimeLibraryViewModel @Inject constructor(
    private val collectionRepository: AnimeCollectionRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(AnimeLibraryUiState())
    val uiState: StateFlow<AnimeLibraryUiState> = combine(
        collectionRepository.getAllLibraryCollections(),
        _uiState
    ){ data, state ->
        val filteredList = applyCurrentFilter(data,state.selectedFilter)
        state.copy(allCollections = data, collections = filteredList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnimeLibraryUiState(isLoading = true)
    )
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

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