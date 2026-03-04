package com.example.animeexplorer.data

import android.util.Log
import com.example.animeexplorer.domain.AnimeRepository
import jakarta.inject.Inject
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.io.IOException
import java.net.UnknownHostException
import kotlin.coroutines.coroutineContext


class AnimeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService
): AnimeRepository {
    override suspend fun getAnimeList(page: Int): Result<AnimeResponse> {
        return try {
            val response = apiService.getAnimeList(page)
            Result.success(response)
        }catch (e: IOException) {
            Log.w("API_MESSAGE", "No internet connection:")
            Result.failure(Exception("No internet connection. Please check your network settings and try again."))
        }
        catch (e: Exception) {
            currentCoroutineContext().ensureActive()

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