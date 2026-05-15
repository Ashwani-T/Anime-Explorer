package com.example.animeexplorer.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute

internal data class AppRouteUiState(
    val isAnimeList: Boolean,
    val isAnimeDetail: Boolean,
    val isAnimeEpisodes: Boolean,
    val isMyCollection: Boolean,
    val isSearch: Boolean,
    val showBottomBar: Boolean
)

internal fun resolveRouteUiState(currentDestination: NavDestination?): AppRouteUiState {
    val showBottomBar = listOf(
        HomeDestination.AnimeList,
        AppDestination.Search,
        AppDestination.MyCollection
    ).any { route ->
        currentDestination?.hasRoute(route::class) == true
    }

    return AppRouteUiState(
        isAnimeList = currentDestination?.hasRoute<HomeDestination.AnimeList>() ?: false,
        isAnimeDetail = currentDestination?.hasRoute<HomeDestination.AnimeDetail>() ?: false,
        isAnimeEpisodes = currentDestination?.hasRoute<HomeDestination.AnimeEpisodes>() ?: false,
        isMyCollection = currentDestination?.hasRoute<AppDestination.MyCollection>() ?: false,
        isSearch = currentDestination?.hasRoute<AppDestination.Search>() ?: false,
        showBottomBar = showBottomBar
    )
}

