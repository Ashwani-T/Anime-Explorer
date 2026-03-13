package com.example.animeexplorer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.animeexplorer.data.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Entity("anime_collections")
data class AnimeCollectionsEntity(
    @PrimaryKey
    val malId: Int,
    val title: String,
    val description: String,
    val duration: String,
    val imageUrl: String,
    val type: String
)