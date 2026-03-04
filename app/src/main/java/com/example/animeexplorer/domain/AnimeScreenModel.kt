package com.example.animeexplorer.domain

data class AnimeUiModel(
    val id: Int,
    val title: String,
    val description: String ,
    val duration: String ,
    val imageUrl: String ,
    val type: String
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