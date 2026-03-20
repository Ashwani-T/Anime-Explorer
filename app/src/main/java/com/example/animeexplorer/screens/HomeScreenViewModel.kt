package com.example.animeexplorer.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.data.ConnectivityObserver
import com.example.animeexplorer.domain.AnimeRepository
import com.example.animeexplorer.domain.enums.AnimeFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: AnimeRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private val isConnected = MutableStateFlow(false)

    init {
        observeNetworkStatus()
    }
    private suspend fun loadAnimePage() {

        _uiState.update { it.copy(isRefreshing = true) }

        try {
            /**
             * Loading the relevant animeList for home screen
             */
            val trendingAnime = repository.getTopAnime(filter = AnimeFilter.BY_POPULARITY)
                .onSuccess { result ->
                _uiState.update {
                    it.copy(
                        top = HomeSection(items = result, isLoading = false, error = null)
                    )
                }
            }
                .onFailure { e -> Log.d("Ashwani", "${e.message}") }

            val topAnime = repository.getTopAnime()
                .onSuccess { result ->
                _uiState.update {
                    it.copy(
                        horizontalPager = result.take(10),
                        trending = HomeSection(items = result, isLoading = false, error = null)
                    )
                }
            }

            val upcomingAnime = repository.getTopAnime(filter = AnimeFilter.UPCOMING)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            upcoming = HomeSection(items = result, isLoading = false, error = null)
                        )
                    }
                }

            val favoriteAnime = repository.getTopAnime(filter = AnimeFilter.FAVORITE)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            favorites = HomeSection(items = result, isLoading = false, error = null)
                        )
                    }
                }

            val currentSeasonAnime = repository.getThisSeasonAnime()
                .onSuccess { animeList ->
                    _uiState.update { state ->
                        state.copy(currentSeason = HomeSection(items = animeList, isLoading = false, error = null))
                    }
                }



            _uiState.update { it.copy(isRefreshing = false) }

        } catch (e: Exception) {
            Log.d("HomeScreenViewModel", "loadAnimePageFunction Failed ")
        }
    }


    private fun observeNetworkStatus() {
        viewModelScope.launch {
            connectivityObserver.observer()
                .distinctUntilChanged()
                .collectLatest { status ->
                    isConnected.value = status

                    if (status) {
                       loadAnimePage()
                    }
                }
        }
    }
}