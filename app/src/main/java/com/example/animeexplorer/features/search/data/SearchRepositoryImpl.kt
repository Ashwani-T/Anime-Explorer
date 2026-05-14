package com.example.animeexplorer.features.search.data

import com.example.animeexplorer.core.data.remote.AnimeApiService
import com.example.animeexplorer.core.domain.AnimeResponseModel
import com.example.animeexplorer.core.domain.AnimeUiModel
import com.example.animeexplorer.core.domain.enums.AnimeFilter
import com.example.animeexplorer.core.domain.enums.AnimeType
import com.example.animeexplorer.core.domain.enums.RatingType
import com.example.animeexplorer.core.domain.mapper.toAnimeUiModel
import com.example.animeexplorer.core.domain.mapper.toPageInfo
import com.example.animeexplorer.features.search.domain.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
) : SearchRepository {


    override suspend fun getTopAnime(
        type: AnimeType?,
        filter: AnimeFilter?,
        rating: RatingType?
    ): Result<List<AnimeUiModel>> {
        return try {
            val response = apiService.getTopAnime(
                type = type?.type,
                filter = filter?.filter,
                rating = rating?.name?.lowercase()
            )
            val domainList = response.data.map { it.toAnimeUiModel() }
            Result.success(domainList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFilteredAnime(
        queryParamsMap: Map<String,String>?
    ): Result<AnimeResponseModel> {
        return try {

            val searchedResult = apiService.getAnimeList(
                queryParamsMap = queryParamsMap
            )

            val animeList = searchedResult.data.map { it.toAnimeUiModel() }
            val pageInfo = searchedResult.pagination.toPageInfo()
            Result.success(
                AnimeResponseModel(
                    data = animeList,
                    pagination = pageInfo,
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getThisSeasonAnime(): Result<List<AnimeUiModel>> {
        return try {
            val result = apiService.getThisSeasonAnime().data.map { it.toAnimeUiModel() }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
