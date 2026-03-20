package com.example.animeexplorer.data.remote

import com.example.animeexplorer.data.remote.dto.AnimeDetailResponse
import com.example.animeexplorer.data.remote.dto.AnimeResponseDto
import com.example.animeexplorer.data.remote.dto.EpisodeResponseDto
import com.example.animeexplorer.data.remote.dto.GenresResponseDto
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
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 25
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

    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("type") type: String?,
        @Query("filter") filter: String?,
        @Query("rating") rating: String?,
        @Query("page") page: Int = 1
    ): AnimeResponseDto

    @GET("recommendations/anime")
    suspend fun getAnimeRecommendation(): AnimeResponseDto

    @GET("genres/anime")
    suspend fun getAnimeGenres(
        @Query("filter") filter: String? = "genres"
    ): GenresResponseDto

}