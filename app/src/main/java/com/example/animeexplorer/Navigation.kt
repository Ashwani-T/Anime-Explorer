package com.example.animeexplorer

import kotlinx.serialization.Serializable


sealed interface AppDestination {

    @Serializable data object Home
    @Serializable data object Category
    @Serializable data object MyCollection

}

sealed interface HomeDestination : AppDestination {
     @Serializable object AnimeList : HomeDestination
     @Serializable data class AnimeDetail(val malId: Int) : HomeDestination
}