package com.example.animeexplorer.features.collection.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


enum class LibraryStatus{
    WATCH_LATER,
    COMPLETED,
    WATCHING,
    ON_Hold,
    UNLISTED
}

@Entity("anime_collections")
data class AnimeCollectionsEntity(
    @PrimaryKey
    val malId: Int,
    val title: String,
    val imageUrl: String,
    val type: String,
    val status: LibraryStatus,
    val episodesCompleted: Int = 0,
    val totalEpisodes: Int,
    val createdAt: Long = System.currentTimeMillis()
)