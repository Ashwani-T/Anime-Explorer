package com.example.animeexplorer.data.local.entity

import androidx.room.Entity


@Entity(
    tableName = "episodes",
    primaryKeys = ["malId", "episodeNumber"]
)
data class EpisodeEntity(
    val malId: Int,
    val episodeNumber: Int,
    val title: String?,
    val isCompleted: Boolean = false
)