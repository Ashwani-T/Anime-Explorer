package com.example.animeexplorer.features.detail.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "anime_details")
data class AnimeDetailsEntity(
    @PrimaryKey
    val malId: Int,
    val title: String,
    val year: Int,
    val imageUrl: String,
    val trailerUrl: String,
    val synopsis: String,
    val score: Double,
    val status: String,
    val episodes:Int,
    val type: String

)