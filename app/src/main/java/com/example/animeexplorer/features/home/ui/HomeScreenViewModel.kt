package com.example.animeexplorer.features.home.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.core.data.connectivity.ConnectivityObserver
import com.example.animeexplorer.features.home.domain.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {
    private val isConnected = MutableStateFlow(false)

    private val animeDataFlow = combine(
        homeRepository.getTrendingAnime(),
        homeRepository.getUpcomingAnime(),
        homeRepository.getTopAnime(),
        homeRepository.getSeasonAnime(),
        homeRepository.getFavoriteAnime()
    ) { trending, upcoming, top, season, favorites ->
        listOf(trending, upcoming, top, season, favorites)
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = combine(
        animeDataFlow, _uiState
    ) { data, state ->
        val (trending, upcoming, top, season, favorites) = data
        HomeUiState(
            horizontalPager = top,
            trending = HomeSection(items = trending),
            upcoming = HomeSection(items = upcoming),
            top = HomeSection(items = top),
            currentSeason = HomeSection(items = season),
            favorites = HomeSection(items = favorites),
            isRefreshing = state.isRefreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isRefreshing = true)
    )


    init {
        Log.d("HomeViewModel", "INIT CALLED")
        observeNetworkStatus()
    }

    @OptIn(FlowPreview::class)
    private fun observeNetworkStatus() {
        viewModelScope.launch {
            connectivityObserver.observer()
                .distinctUntilChanged()
                .debounce(500)
                .collectLatest { status ->
                    isConnected.value = status

                    if (status) {
                        loadAnimePage(false)
                    }
                }
        }
    }

    private fun loadAnimePage(forceRefresh: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                Log.d("TAG", ": running loadanimepage function ")
                homeRepository.refreshHomeData(forceRefresh)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error refreshing data", e)
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun refresh() {
        if (!uiState.value.isRefreshing) {
            loadAnimePage(false)
        }
    }
    fun forceRefresh(forceRefresh: Boolean){
        if (uiState.value.isRefreshing) {
            return
        } else {
            loadAnimePage(forceRefresh)
        }
    }
}