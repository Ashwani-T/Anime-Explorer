package com.example.animeexplorer.features.explorer.data

import android.util.Log
import com.example.animeexplorer.core.data.remote.AnimeApiService
import com.example.animeexplorer.core.domain.AnimeResponseModel
import com.example.animeexplorer.features.explorer.domain.mapper.toPageInfo
import com.example.animeexplorer.features.explorer.domain.mapper.toUiModel
import com.example.animeexplorer.features.explorer.domain.ExplorerCategory
import com.example.animeexplorer.features.explorer.domain.ExplorerRepository
import javax.inject.Inject

class ExplorerRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService
) : ExplorerRepository {

    override suspend fun getAnimeByCategory(
        category: ExplorerCategory,
        page: Int
    ): Result<AnimeResponseModel> {
        return try {
            val response = when (category) {
                ExplorerCategory.TRENDING -> {
                    apiService.getTopAnime(
                        filter = category.filter?.filter,
                        type = null,
                        rating = null,
                        page = page
                    )
                }
                ExplorerCategory.TOP -> {
                    apiService.getTopAnime(
                        filter = null,
                        type = null,
                        rating = null,
                        page = page
                    )
                }
                ExplorerCategory.UPCOMING -> {
                    apiService.getTopAnime(
                        filter = category.filter?.filter,
                        type = null,
                        rating = null,
                        page = page
                    )
                }
                ExplorerCategory.FAVORITE -> {
                    apiService.getTopAnime(
                        filter = category.filter?.filter,
                        type = null,
                        rating = null,
                        page = page
                    )
                }
                ExplorerCategory.SEASON -> {
                    apiService.getThisSeasonAnime()
                }
            }

            val animeList = response.data.map { it.toUiModel() }
            val pageInfo = response.pagination.toPageInfo()

            Result.success(
                AnimeResponseModel(
                    data = animeList,
                    pagination = pageInfo
                )
            )
        } catch (e: Exception) {
            Log.e("ExplorerRepositoryImpl", "Error fetching anime by category: ${e.message}", e)
            Result.failure(e)
        }
    }
}


