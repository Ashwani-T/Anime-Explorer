package com.example.animeexplorer.features.episodes.data

import android.util.Log
import com.example.animeexplorer.core.data.remote.AnimeApiService
import com.example.animeexplorer.core.data.remote.dto.EpisodeResponseDto
import com.example.animeexplorer.features.episodes.domain.AnimeEpisodeUiModel
import com.example.animeexplorer.features.episodes.domain.EpisodeRepository
import com.example.animeexplorer.features.episodes.domain.toUiModel
import javax.inject.Inject

class EpisodeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService
) : EpisodeRepository {


    override suspend fun getAnimeEpisodes(malId: Int): Result<List<AnimeEpisodeUiModel>> {
        val episodeList: MutableList<AnimeEpisodeUiModel> = mutableListOf()

        return try {
            var page =1
            var hasNextPage = true

            while(hasNextPage){
                val response = apiService.getAnimeEpisodes(malId,page)
                val episodeUiModelList = response.data.map { it.toUiModel() }
                episodeList.addAll(episodeUiModelList)
                hasNextPage = response.pagination.hasNextPage
                page++
            }


            Result.success(episodeList)
        } catch (e: Exception) {
            Log.w("EPISODE_API", e.message ?: "Unknown error")
            Log.e("EPISODE_API_ERROR", e.stackTraceToString())
            Result.failure(e)
        }
    }
}

