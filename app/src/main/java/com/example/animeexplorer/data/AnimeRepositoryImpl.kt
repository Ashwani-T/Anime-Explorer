package com.example.animeexplorer.data

import android.util.Log
import com.example.animeexplorer.domain.AnimeRepository
import com.example.animeexplorer.domain.AnimeResponseModel
import com.example.animeexplorer.domain.AnimeUiModel
import com.example.animeexplorer.domain.PageInfo
import jakarta.inject.Inject


class AnimeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService
): AnimeRepository {
    override suspend fun getAnimeList(query: String, page: Int): AnimeResponseModel {


        var animeList: List<AnimeUiModel> = emptyList()
        var pageInfo: PageInfo = PageInfo(
            hasNextPage = true,
            currentPage = page,
        )

        runCatching {
            apiService.getAnimeList(query = query.takeIf{ query.isNotBlank() }, page = page)
        }.onFailure { exception ->
            Log.d("AnimeRepoException", "${exception.message}")
        }.onSuccess {response ->
            animeList = response.data.map { anime ->
                anime.toUiModel()
            }
            pageInfo = response.pagination.toPageInfo()
        }

        return AnimeResponseModel(
            data = animeList,
            pagination = pageInfo
        )

    }

    override suspend fun getAnimeDetail(malId: Int): Result<AnimeDetailResponse> {
        return try {
            val response = apiService.getAnimeDetail(malId)
            Result.success(response)
        } catch (e: Exception) {
            Log.w("API_MESSAGE", e.message ?: "Unknown error")
            Log.e("API_ERROR", e.stackTraceToString())
            Result.failure(e)
        }
    }
}