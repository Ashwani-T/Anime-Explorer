package com.example.animeexplorer.data

import android.util.Log
import com.example.animeexplorer.data.dao.AnimeDetailsDao
import com.example.animeexplorer.domain.AnimeRepository
import com.example.animeexplorer.domain.AnimeResponseModel
import com.example.animeexplorer.domain.AnimeUiModel
import com.example.animeexplorer.domain.PageInfo
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


class AnimeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
    private val animeDetailsDao: AnimeDetailsDao
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

            if(query.isBlank()){
                animeList = animeDetailsDao.getAnimeList().map {cachedAnime ->
                    cachedAnime.toUiModel()
                }
            }else{
                animeList = animeDetailsDao.getSearchedAnimeList(query).map {cachedAnimeItem ->
                    cachedAnimeItem.toUiModel()
                }
            }
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

    override suspend fun getAnimeDetail(malId: Int): Result<AnimeDetail> {

        return try{
            val cachedAnimeDetail = animeDetailsDao.getAnimeDetails(malId)

            cachedAnimeDetail?.let {
                Log.d("AnimeRepoImpl", "getAnimeDetail: Returned the reponse from roomdb")
                return Result.success(cachedAnimeDetail.toDomain())
            }

            val response = apiService.getAnimeDetail(malId)
            val responseDomain: AnimeDetail = response.data.toDomain()
            animeDetailsDao.insertAnimeDetails(responseDomain.toEntity())
            Result.success(responseDomain)

        }catch (e: Exception){
            Result.failure(exception = e)
        }
    }
}