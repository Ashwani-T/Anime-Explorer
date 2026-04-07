package com.example.animeexplorer.features.detail.data

import android.util.Log
import com.example.animeexplorer.core.data.remote.AnimeApiService
import com.example.animeexplorer.core.domain.AnimeDetailUiModel
import com.example.animeexplorer.core.domain.mapper.toAnimeDetailUiModel
import com.example.animeexplorer.features.detail.data.local.dao.AnimeDetailsDao
import com.example.animeexplorer.features.detail.domain.DetailRepository
import com.example.animeexplorer.features.detail.domain.mapper.toAnimeDetailEntity
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
    private val animeDetailsDao: AnimeDetailsDao
) : DetailRepository {

    override suspend fun getAnimeDetail(malId: Int): Result<AnimeDetailUiModel> {
        return try {
            val cachedAnimeDetail = animeDetailsDao.getAnimeDetails(malId)

            cachedAnimeDetail?.let {
                Log.d("DetailRepoImpl", "getAnimeDetail: Returned the response from roomdb")
                return Result.success(cachedAnimeDetail.toAnimeDetailUiModel())
            }

            val response = apiService.getAnimeDetail(malId)

            val responseEntity = response.data.toAnimeDetailEntity()
            animeDetailsDao.insertAnimeDetails(responseEntity)

            Result.success(responseEntity.toAnimeDetailUiModel())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
