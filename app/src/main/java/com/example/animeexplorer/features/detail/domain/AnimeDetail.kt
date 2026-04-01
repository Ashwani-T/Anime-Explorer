package com.example.animeexplorer.features.detail.domain

data class AnimeDetail(
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