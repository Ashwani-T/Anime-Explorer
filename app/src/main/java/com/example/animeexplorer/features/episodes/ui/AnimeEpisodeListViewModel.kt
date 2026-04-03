package com.example.animeexplorer.features.episodes.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.animeexplorer.HomeDestination
import com.example.animeexplorer.features.episodes.domain.EpisodeRepository
import com.example.animeexplorer.features.episodes.domain.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AnimeEpisodeListViewModel @Inject constructor(
    private val repository: EpisodeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val malId: Int = savedStateHandle.toRoute<HomeDestination.AnimeEpisodes>().malId


    private val _uiState = MutableStateFlow<AnimeEpisodeListUiState>(AnimeEpisodeListUiState.Loading)
    val uiState: StateFlow<AnimeEpisodeListUiState> = _uiState.asStateFlow()

    init {
        loadAnimeEpisodes()
    }

    fun loadAnimeEpisodes() {
        viewModelScope.launch {
            _uiState.value = AnimeEpisodeListUiState.Loading

            val response = repository.getAnimeEpisodes(malId)

            response.fold(
                onSuccess = { episodeResponse ->
                    _uiState.value = AnimeEpisodeListUiState.Success(episodes = episodeResponse)
                    Log.d("AnimeEpisodeVM", "Episodes loaded: ${episodeResponse.size}")
                },
                onFailure = { exception ->
                    _uiState.value = AnimeEpisodeListUiState.Error(exception.message!!)
                    Log.e("AnimeEpisodeVM", "Error loading episodes")
                }
            )
        }
    }

    fun retry() {
        loadAnimeEpisodes()
    }
}

