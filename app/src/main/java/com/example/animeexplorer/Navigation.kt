package com.example.animeexplorer

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

data class TopLevelDestination(
    val route: AppDestination,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val label: String
)

sealed interface AppDestination {
    @Serializable data object Home: AppDestination
    @Serializable data object Category: AppDestination
    @Serializable data object MyCollection: AppDestination

}

sealed interface HomeDestination : AppDestination {
    @Serializable data object AnimeList : HomeDestination
     @Serializable data class AnimeDetail(val malId: Int) : HomeDestination
}