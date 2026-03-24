package com.example.animeexplorer.data.mapper

import com.example.animeexplorer.data.remote.dto.GenresResponseDto
import com.example.animeexplorer.screens.GenreModel

fun GenresResponseDto.toGenreModel(): GenreModel {
    return GenreModel(
        malId = genreId,
        name = genreName
    )
}