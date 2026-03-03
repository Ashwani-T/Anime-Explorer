package com.example.animeexplorer.data

import android.util.Log
import com.example.animeexplorer.domain.AnimeRepository
import jakarta.inject.Inject


class AnimeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService
): AnimeRepository {
    override suspend fun getAnimeList(page: Int): Result<AnimeResponse> {
        return try {
            val response = apiService.getAnimeList(page)
            Result.success(response)
        }catch (e: Exception) {

            Log.w("API_MESSAGE", e.message ?: "Unknown error")
            Log.e("API_ERROR", e.stackTraceToString())


            Result.failure(e)
        }
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