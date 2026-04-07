package com.example.animeexplorer.features.home.data

import android.util.Log
import com.example.animeexplorer.features.home.data.local.dao.HomeCachedDao
import com.example.animeexplorer.features.home.domain.mapper.toHomeCacheEntity
import com.example.animeexplorer.features.home.domain.mapper.toAnimeUiModel
import com.example.animeexplorer.core.data.remote.AnimeApiService
import com.example.animeexplorer.core.domain.AnimeUiModel
import com.example.animeexplorer.features.home.domain.HomeRepository
import com.example.animeexplorer.core.domain.enums.AnimeFilter
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class HomeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
    private val homeCachedDao: HomeCachedDao,
) : HomeRepository {

    private val cacheRefreshTimeout: Long = 6 * 60  * 60  * 1000

    override suspend fun refreshHomeData(forceRefresh: Boolean) {
        val lastSyncTime = homeCachedDao.getLastSyncTime() ?: 0L
        val shouldRefresh: Boolean =
            System.currentTimeMillis() - lastSyncTime > cacheRefreshTimeout || forceRefresh

        if (shouldRefresh) {
            Log.d("HOME REPO IMPL", "refreshHomeData: Refreshing Again")

            coroutineScope {
                try{
                    //homeCachedDao.clearAnimeCacheTable()
                    Log.d("TAG", "refreshHomeData: CLEARED CACHED TABLE")
                    val trendingAnime = apiService.getTopAnime(filter = AnimeFilter.BY_POPULARITY.filter, type = null, rating = null).data

                    delay(200)

                    val upcomingAnime = apiService.getTopAnime(filter = AnimeFilter.UPCOMING.filter,type = null, rating = null).data

                    delay(200)

                    val favoriteAnime = apiService.getTopAnime(filter = AnimeFilter.FAVORITE.filter,type = null, rating = null).data

                    delay(200)

                    val topAnime = apiService.getTopAnime(filter = null,type = null, rating = null).data

                    delay(200)

                    val seasonAnime = apiService.getThisSeasonAnime().data

                    homeCachedDao.insertAnimeEntity(trendingAnime.map {
                        it.toHomeCacheEntity(category = "Trending")
                    })
                    homeCachedDao.insertAnimeEntity(upcomingAnime.map {
                        it.toHomeCacheEntity(category = "Upcoming")
                    })
                    homeCachedDao.insertAnimeEntity(favoriteAnime.map {
                        it.toHomeCacheEntity(category = "Favorite")
                    })
                    homeCachedDao.insertAnimeEntity(topAnime.map {
                        it.toHomeCacheEntity(category = "Top")
                    })
                    homeCachedDao.insertAnimeEntity(seasonAnime.map {
                        it.toHomeCacheEntity(category = "Season")
                    })

                    Log.d("COMPLETED FETCHING", "refreshHomeData: ${trendingAnime.size} ${upcomingAnime.size} ${seasonAnime.size}")
                }catch (e: Exception){
                    Log.d("Home Repo Impl","${e.message}")
                }
            }
        }
    }

    override fun getTrendingAnime(): Flow<List<AnimeUiModel>> {
        return homeCachedDao.getAnimeByCategory("Trending").map { cachedAnimeList ->
            cachedAnimeList.map { cachedAnime ->
                cachedAnime.toAnimeUiModel()
            }
        }
    }

    override fun getUpcomingAnime(): Flow<List<AnimeUiModel>> {
        return homeCachedDao.getAnimeByCategory("Upcoming").map { cachedAnimeList ->
            cachedAnimeList.map { cachedAnime ->
                cachedAnime.toAnimeUiModel()
            }
        }
    }

    override fun getTopAnime(): Flow<List<AnimeUiModel>> {
        return homeCachedDao.getAnimeByCategory("Top").map { cachedAnimeList ->
            cachedAnimeList.map { cachedAnime ->
                cachedAnime.toAnimeUiModel()
            }
        }
    }

    override fun getSeasonAnime(): Flow<List<AnimeUiModel>> {
        return homeCachedDao.getAnimeByCategory("Season").map { cachedAnimeList ->
            cachedAnimeList.map { cachedAnime ->
                cachedAnime.toAnimeUiModel()
            }
        }
    }

    override fun getFavoriteAnime(): Flow<List<AnimeUiModel>> {
        return homeCachedDao.getAnimeByCategory("Favorite").map { cachedAnimeList ->
            cachedAnimeList.map {
                it.toAnimeUiModel()
            }
        }
    }
}