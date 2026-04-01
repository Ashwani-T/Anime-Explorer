package com.example.animeexplorer.core.domain

import com.example.animeexplorer.features.collection.data.local.entity.LibraryStatus

data class AnimeResponseModel(
    val data: List<AnimeUiModel>,
    val pagination: PageInfo
)


data class PageInfo(
    val currentPage: Int,
    val hasNextPage: Boolean
)

data class AnimeUiModel(
    val id: Int,
    val title: String,
    val description: String ,
    val duration: String ,
    val imageUrl: String,
    val score: Double
)


data class AnimeDetailUiModel(
    val id: Int,
    val title: String,
    val synopsis: String,
    val imageUrl: String,
    val score: Double,
    val status: String,
    val type: String,
    val episodes: Int,
    val year: Int
)

data class AnimeCollectionUiModel(
    val malId: Int,
    val title: String,
    val imageUrl: String,
    val type: String,
    val status: LibraryStatus,
    val episodesCompleted: Int,
    val totalEpisodes: Int
){
    val progress: Float = if (totalEpisodes > 0) {
        (episodesCompleted.coerceAtMost(totalEpisodes)).toFloat() / totalEpisodes
    } else 0f
}