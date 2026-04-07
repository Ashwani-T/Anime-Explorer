package com.example.animeexplorer.features.search.data

import android.util.Log
import com.example.animeexplorer.core.data.remote.AnimeApiService
import com.example.animeexplorer.core.domain.AnimeResponseModel
import com.example.animeexplorer.core.domain.AnimeUiModel
import com.example.animeexplorer.core.domain.PageInfo
import com.example.animeexplorer.core.domain.enums.AnimeFilter
import com.example.animeexplorer.core.domain.enums.AnimeType
import com.example.animeexplorer.core.domain.enums.FormatType
import com.example.animeexplorer.core.domain.enums.RatingType
import com.example.animeexplorer.core.domain.enums.SortOrder
import com.example.animeexplorer.core.domain.enums.SortType
import com.example.animeexplorer.core.domain.enums.StatusType
import com.example.animeexplorer.core.domain.mapper.toAnimeUiModel
import com.example.animeexplorer.core.domain.mapper.toPageInfo
import com.example.animeexplorer.features.detail.data.local.dao.AnimeDetailsDao
import com.example.animeexplorer.features.search.domain.SearchRepository
import com.example.animeexplorer.features.search.ui.GenreModel
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
    private val animeDetailsDao: AnimeDetailsDao
) : SearchRepository {

    override suspend fun getAnimeList(query: String, page: Int): AnimeResponseModel {
        var animeList: List<AnimeUiModel> = emptyList()
        var pageInfo = PageInfo(
            hasNextPage = true,
            currentPage = page,
        )

        runCatching {
            apiService.getAnimeList(
                query = query.takeIf { query.isNotBlank() },
                page = page,
            )
        }.onFailure { exception ->
            animeList = if (query.isBlank()) {
                animeDetailsDao.getAnimeList().map { it.toAnimeUiModel() }
            } else {
                animeDetailsDao.getSearchedAnimeList(query).map { it.toAnimeUiModel() }
            }
            Log.d("SearchRepoException", "${exception.message}")
        }.onSuccess { response ->
            animeList = response.data.map { it.toAnimeUiModel() }
            pageInfo = response.pagination.toPageInfo()
        }

        return AnimeResponseModel(
            data = animeList,
            pagination = pageInfo
        )
    }

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
        query: String,
        page: Int?,
        orderBy: SortType?,
        sortOrder: SortOrder?,
        format: FormatType?,
        status: StatusType?,
        rating: RatingType?,
        genres: Set<GenreModel>?
    ): Result<AnimeResponseModel> {
        return try {
            val searchedResult = apiService.getAnimeList(
                query = query.takeIf { query.isNotBlank() },
                page = page,
                genres = genres?.joinToString(separator = ",") { it.malId.toString() },
                type = format?.apiName,
                status = status?.apiName,
                rating = rating?.apiName,
                orderBy = orderBy?.apiName,
                sortOrder = sortOrder?.apiName,
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
