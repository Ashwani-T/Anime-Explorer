package com.example.animeexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


enum class LibraryStatus{
    WATCHING,
    ON_Hold,
    UNLISTED,
    COMPLETED,
    WATCH_LATER
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