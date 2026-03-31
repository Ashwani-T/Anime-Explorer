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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class AnimeLibraryViewModel @Inject constructor(
    private val collectionRepository: AnimeCollectionRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(AnimeLibraryUiState())
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val uiState: StateFlow<AnimeLibraryUiState> = combine(
        collectionRepository.getAllLibraryCollections(),
        _uiState,
        _query
    ){ data, state, searchQuery ->
        val filteredList = applyFilters(data, state.selectedFilter, searchQuery)
        state.copy(allCollections = data, collections = filteredList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnimeLibraryUiState(isLoading = true)
    )

    fun onSearchQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun filterByStatus(status: LibraryStatus) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = status,
                selectedPreset = status.name.replace("_", " ")
                    .lowercase()
                    .replaceFirstChar { it.uppercase() }
            )
        }
    }

    fun clearFilter() {
        _uiState.update { state ->
            state.copy(
                selectedFilter = null,
                selectedPreset = "collections"
            )
        }
    }

    private fun applyFilters(
        collections: List<AnimeCollectionUiModel>,
        selectedFilter: LibraryStatus?,
        searchQuery: String
    ): List<AnimeCollectionUiModel> {
        return collections.filter { item ->
            val matchesStatus = selectedFilter == null || item.status == selectedFilter
            val matchesQuery = searchQuery.isEmpty() || item.title.contains(searchQuery, ignoreCase = true)
            matchesStatus && matchesQuery
        }
    }
}
