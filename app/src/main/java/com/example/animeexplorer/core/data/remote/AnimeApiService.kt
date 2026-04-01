package com.example.animeexplorer.core.data.remote

import com.example.animeexplorer.core.data.remote.dto.AnimeDetailResponse
import com.example.animeexplorer.core.data.remote.dto.AnimeResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeApiService {

    @GET("anime")
    suspend fun getAnimeList(
        @Query("q") query: String?,
        @Query("genres") genres: String? = null,
        @Query("type") type: String? = null,
        @Query("status") status: String? = null,
        @Query("rating") rating: String? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("sort") sortOrder: String? = null,
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Int = 25
    ): AnimeResponseDto

    @GET("anime/{id}")
    suspend fun getAnimeDetail(
        @Path("id") malId: Int
    ): AnimeDetailResponse


    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("type") type: String?,
        @Query("filter") filter: String?,
        @Query("rating") rating: String?,
        @Query("page") page: Int = 1
    ): AnimeResponseDto

    @GET("seasons/now")
    suspend fun getThisSeasonAnime(): AnimeResponseDto

}