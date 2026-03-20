package com.example.animeexplorer.data

import android.util.Log
import com.example.animeexplorer.data.local.dao.AnimeDetailsDao
import com.example.animeexplorer.data.mapper.AnimeDetail
import com.example.animeexplorer.data.mapper.toDomain
import com.example.animeexplorer.data.mapper.toEntity
import com.example.animeexplorer.data.mapper.toPageInfo
import com.example.animeexplorer.data.mapper.toUiModel
import com.example.animeexplorer.data.remote.AnimeApiService
import com.example.animeexplorer.domain.AnimeRepository
import com.example.animeexplorer.domain.AnimeResponseModel
import com.example.animeexplorer.domain.AnimeUiModel
import com.example.animeexplorer.domain.PageInfo
import com.example.animeexplorer.domain.enums.AnimeFilter
import com.example.animeexplorer.domain.enums.AnimeType
import com.example.animeexplorer.domain.enums.FormatType
import com.example.animeexplorer.domain.enums.RatingType
import com.example.animeexplorer.domain.enums.SortOrder
import com.example.animeexplorer.domain.enums.SortType
import com.example.animeexplorer.domain.enums.StatusType
import com.example.animeexplorer.screens.GenreModel
import jakarta.inject.Inject


class AnimeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
    private val animeDetailsDao: AnimeDetailsDao,
) : AnimeRepository {
    override suspend fun getAnimeList(query: String, page: Int): AnimeResponseModel {


        var animeList: List<AnimeUiModel> = emptyList()
        var pageInfo: PageInfo = PageInfo(
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
                animeDetailsDao.getAnimeList().map { cachedAnime ->
                    cachedAnime.toUiModel()
                }
            } else {
                animeDetailsDao.getSearchedAnimeList(query).map { cachedAnimeItem ->
                    cachedAnimeItem.toUiModel()
                }
            }
            Log.d("AnimeRepoException", "${exception.message}")
        }.onSuccess { response ->
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

    override suspend fun getAnimeDetail(malId: Int): Result<AnimeDetail> {

        return try {
            val cachedAnimeDetail = animeDetailsDao.getAnimeDetails(malId)

            cachedAnimeDetail?.let {
                Log.d("AnimeRepoImpl", "getAnimeDetail: Returned the reponse from roomdb")
                return Result.success(cachedAnimeDetail.toDomain())
            }

            val response = apiService.getAnimeDetail(malId)
            val responseDomain: AnimeDetail = response.data.toDomain()
            animeDetailsDao.insertAnimeDetails(responseDomain.toEntity())
            Result.success(responseDomain)

        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }

    override suspend fun getTopAnime(
        type: AnimeType?,
        filter: AnimeFilter?,
        rating: RatingType?,
    ): Result<List<AnimeUiModel>> {
        return try {
            Log.d("TAG", "getTopAnime: IN")
            val response = apiService.getTopAnime(
                type = type?.type,
                filter = filter?.filter,
                rating = rating?.name?.lowercase()
            )

            val domainList = response.data.map { it.toUiModel() }

            Result.success(domainList)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFilteredAnime(
        query: String,
        orderBy: SortType?,
        sortOrder: SortOrder?,
        format: FormatType?,
        status: StatusType?,
        rating: RatingType?,
        genres: Set<GenreModel>?
    ): Result<List<AnimeUiModel>> {
        return try {
            val searchedResult = apiService.getAnimeList(
                query = query.takeIf { query.isNotBlank() },
                genres = genres?.joinToString(separator = ",") { it.malId.toString() },
                type = format?.apiName,
                status = status?.apiName,
                rating = rating?.apiName,
                orderBy = orderBy?.apiName,
                sortOrder = sortOrder?.apiName,
            )
            val animeList: List<AnimeUiModel> = searchedResult.data.map { it.toUiModel() }

            Result.success(animeList)
        }catch (e: Exception){
            Result.failure(e)
        }
    }


    override suspend fun getThisSeasonAnime(): Result<List<AnimeUiModel>> {
        return try {
            val recommendationResult = apiService.getThisSeasonAnime().data.map {anime ->
                anime.toUiModel()
            }
            Log.d("Ashwani", "${recommendationResult.size}: ")

            Result.success(recommendationResult)
        }catch (e: Exception){
            Log.d("Ashwani", "${e.message}: ")
            Result.failure(Exception(e.message))
        }
    }
}