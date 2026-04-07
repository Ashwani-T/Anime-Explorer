package com.example.animeexplorer.features.home.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "anime_cache")
data class HomeCacheEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val imageUrl: String,
    val description: String,
    val score: Double,
    val category: String, // Season, Top, Trending, Upcoming
    val timeStamp: Long = System.currentTimeMillis()
)
