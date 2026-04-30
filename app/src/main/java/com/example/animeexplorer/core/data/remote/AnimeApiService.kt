package com.example.animeexplorer.core.data.remote

import com.example.animeexplorer.core.data.remote.dto.AnimeDetailResponse
import com.example.animeexplorer.core.data.remote.dto.AnimeResponseDto
import com.example.animeexplorer.core.data.remote.dto.EpisodeResponseDto
import com.example.animeexplorer.core.domain.enums.AnimeFilter
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface AnimeApiService {

    @GET("anime")
    suspend fun getAnimeList(
        @Query("q") query: String?,
        @QueryMap filters: Map<String, String>? = emptyMap(),
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

    @GET("anime/{id}/episodes")
    suspend fun getAnimeEpisodes(
        @Path("id") malId: Int,
        @Query("page") page: Int = 1

    ): EpisodeResponseDto

}