package com.example.animeexplorer.features.search.domain.model


data class GenreModel(
    val malId: Int,
    val name: String
)


val genreList = listOf(
    GenreModel(1, "Action"),
    GenreModel(2, "Adventure"),
    GenreModel(4, "Comedy"),
    GenreModel(8, "Drama"),
    GenreModel(10, "Fantasy"),
    GenreModel(14, "Horror"),
    GenreModel(7, "Mystery"),
    GenreModel(22, "Romance"),
    GenreModel(24, "Sci-Fi"),
    GenreModel(36, "Slice of Life")
)