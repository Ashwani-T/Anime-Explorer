package com.example.animeexplorer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.animeexplorer.data.Image
import kotlinx.serialization.SerialName


@Entity(tableName = "anime_details")
data class AnimeDetailsEntity(
    @PrimaryKey
    val malId: Int,

    val title: String,
    val year: Int,
    val imageUrl: String,
    val synopsis: String,
    val score: Double,
    val status: String,
    val episodes:Int,
    val type: String

)