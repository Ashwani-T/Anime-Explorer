package com.example.animeexplorer.features.detail.data

import android.util.Log
import com.example.animeexplorer.core.data.remote.AnimeApiService
import com.example.animeexplorer.features.detail.data.local.dao.AnimeDetailsDao
import com.example.animeexplorer.features.detail.domain.mapper.toDomain
import com.example.animeexplorer.features.detail.domain.mapper.toEntity
import com.example.animeexplorer.features.detail.domain.AnimeDetail
import com.example.animeexplorer.features.detail.domain.DetailRepository
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
    private val animeDetailsDao: AnimeDetailsDao
) : DetailRepository {

    override suspend fun getAnimeDetail(malId: Int): Result<AnimeDetail> {
        return try {
            val cachedAnimeDetail = animeDetailsDao.getAnimeDetails(malId)

            cachedAnimeDetail?.let {
                Log.d("DetailRepoImpl", "getAnimeDetail: Returned the response from roomdb")
                return Result.success(cachedAnimeDetail.toDomain())
            }

            val response = apiService.getAnimeDetail(malId)
            val responseDomain: AnimeDetail = response.data.toDomain()
            animeDetailsDao.insertAnimeDetails(responseDomain.toEntity())
            Result.success(responseDomain)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
