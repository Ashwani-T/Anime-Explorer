package com.example.animeexplorer

import kotlinx.serialization.Serializable


sealed interface AppDestination {
    @Serializable
    data object AnimeList : AppDestination

    @Serializable
    data class AnimeDetail(val malId: Int) : AppDestination
}

