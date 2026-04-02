package com.example.animeexplorer.features.episodes.data

import android.util.Log
import com.example.animeexplorer.core.data.remote.AnimeApiService
import com.example.animeexplorer.core.data.remote.dto.EpisodeResponseDto
import com.example.animeexplorer.features.episodes.domain.EpisodeRepository
import javax.inject.Inject

class EpisodeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService
) : EpisodeRepository {
    override suspend fun getAnimeEpisodes(malId: Int): Result<EpisodeResponseDto> {
        return try {
            val response = apiService.getAnimeEpisodes(malId)
            Result.success(response)
        } catch (e: Exception) {
            Log.w("EPISODE_API", e.message ?: "Unknown error")
            Log.e("EPISODE_API_ERROR", e.stackTraceToString())
            Result.failure(e)
        }
    }
}

