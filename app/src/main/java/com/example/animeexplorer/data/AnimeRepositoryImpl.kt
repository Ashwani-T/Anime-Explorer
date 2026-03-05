package com.example.animeexplorer.data

import android.util.Log
import com.example.animeexplorer.domain.AnimeRepository
import com.example.animeexplorer.domain.AnimeResponseModel
import com.example.animeexplorer.domain.AnimeUiModel
import com.example.animeexplorer.domain.PageInfo
import jakarta.inject.Inject
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import java.io.IOException
import java.net.UnknownHostException
import kotlin.coroutines.coroutineContext


class AnimeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService
): AnimeRepository {
    override suspend fun getAnimeList(page: Int): AnimeResponseModel {
        var animeList: List<AnimeUiModel> = emptyList()
        var pageInfo: PageInfo = PageInfo(
            hasNextPage = false,
            currentPage = page,
        )

        runCatching {
            Log.d("ApiServiceException", "getAnimeList: Api call was made")
            apiService.getAnimeList(page)
        }.onFailure { exception ->
            Log.d("ApiServiceException", "Failed to fetch anime list: ${exception.message}")
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