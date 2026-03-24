package com.example.animeexplorer.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeexplorer.data.ConnectivityObserver
import com.example.animeexplorer.domain.AnimeRepository
import com.example.animeexplorer.domain.HomeRepository
import com.example.animeexplorer.domain.enums.AnimeFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
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


    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = combine(
        homeRepository.getTrendingAnime(),
        homeRepository.getUpcomingAnime(),
        homeRepository.getTopAnime(),
        homeRepository.getSeasonAnime(),
        homeRepository.getFavoriteAnime()
    ){
        trending, upcoming, top, season, favorites ->
        HomeUiState(
            horizontalPager = top,
            trending = HomeSection(items = trending),
            upcoming = HomeSection(items = upcoming),
            top = HomeSection(items = top),
            currentSeason = HomeSection(items = season),
            favorites = HomeSection(items = favorites),
            isRefreshing = false
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
                       loadAnimePage()
                    }
                }
        }
    }

    private fun loadAnimePage(){
        viewModelScope.launch {
            try {
                Log.d("TAG", ": running loadanimepage function ")
                homeRepository.refreshHomeData(false)
            } catch (e: Exception) {
                // Handle error
            } finally {
                HomeUiState(isRefreshing = false)
            }
        }
    }
}