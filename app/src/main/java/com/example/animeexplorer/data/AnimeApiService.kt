package com.example.animeexplorer.data

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
}