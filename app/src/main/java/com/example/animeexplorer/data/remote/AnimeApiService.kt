package com.example.animeexplorer.data.remote

import com.example.animeexplorer.data.remote.dto.AnimeDetailResponse
import com.example.animeexplorer.data.remote.dto.AnimeResponseDto
import com.example.animeexplorer.data.remote.dto.EpisodeResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeApiService {

    @GET("anime")
    suspend fun getAnimeList(
        @Query("q") query: String?,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): AnimeResponseDto

    @GET("anime/{id}")
    suspend fun getAnimeDetail(
        @Path("id") malId: Int
    ): AnimeDetailResponse

    @GET("anime/{id}/episodes")
    suspend fun getAnimeEpisodes(
        @Path("id") malId: Int,
        @Query("page") page: Int = 1
    ): EpisodeResponseDto
}