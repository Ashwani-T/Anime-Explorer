package com.example.animeexplorer.data

import android.util.Log
import com.example.animeexplorer.data.local.dao.AnimeCachedDao
import com.example.animeexplorer.data.mapper.toAnimeCacheEntity
import com.example.animeexplorer.data.mapper.toUiModel
import com.example.animeexplorer.data.remote.AnimeApiService
import com.example.animeexplorer.domain.AnimeUiModel
import com.example.animeexplorer.domain.HomeRepository
import com.example.animeexplorer.domain.enums.AnimeFilter
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class HomeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
    private val animeCachedDao: AnimeCachedDao,
) : HomeRepository {

    private val cacheRefreshTimeout: Long = 6 * 60  * 60  * 1000

    override suspend fun refreshHomeData(forceRefresh: Boolean) {
        val lastSyncTime = animeCachedDao.getLastSyncTime() ?: 0L
        val shouldRefresh: Boolean =
            System.currentTimeMillis() - lastSyncTime > cacheRefreshTimeout || forceRefresh

        if (shouldRefresh) {
            Log.d("HOME REPO IMPL", "refreshHomeData: Refreshing Again")

            coroutineScope {
                try{
                    //animeCachedDao.clearAnimeCacheTable()
                    Log.d("TAG", "refreshHomeData: CLEARED CACHED TABLE")
                    val trendingAnime = apiService.getTopAnime(filter = AnimeFilter.BY_POPULARITY.filter, type = null, rating = null).data

                    delay(150)

                    val upcomingAnime = apiService.getTopAnime(filter = AnimeFilter.UPCOMING.filter,type = null, rating = null).data

                    delay(150)

                    val favoriteAnime = apiService.getTopAnime(filter = AnimeFilter.FAVORITE.filter,type = null, rating = null).data

                    delay(150)

                    val topAnime = apiService.getTopAnime(filter = null,type = null, rating = null).data

                    delay(150)

                    val seasonAnime = apiService.getThisSeasonAnime().data

                    animeCachedDao.insertAnimeEntity(trendingAnime.map {
                        it.toAnimeCacheEntity(category = "Trending")
                    })
                    animeCachedDao.insertAnimeEntity(upcomingAnime.map {
                        it.toAnimeCacheEntity(category = "Upcoming")
                    })
                    animeCachedDao.insertAnimeEntity(favoriteAnime.map {
                        it.toAnimeCacheEntity(category = "Favorite")
                    })
                    animeCachedDao.insertAnimeEntity(topAnime.map {
                        it.toAnimeCacheEntity(category = "Top")
                    })
                    animeCachedDao.insertAnimeEntity(seasonAnime.map {
                        it.toAnimeCacheEntity(category = "Season")
                    })

                    Log.d("COMPLETED FETCHING", "refreshHomeData: ${trendingAnime.size} ${upcomingAnime.size} ${seasonAnime.size}")
                }catch (e: Exception){
                    Log.d("Home Repo Impl","${e.message}")
                }
            }
        }
    }

    override fun getTrendingAnime(): Flow<List<AnimeUiModel>> {
        return animeCachedDao.getAnimeByCategory("Trending").map { cachedAnimeList ->
            cachedAnimeList.map { cachedAnime ->
                cachedAnime.toUiModel()
            }
        }
    }

    override fun getUpcomingAnime(): Flow<List<AnimeUiModel>> {
        return animeCachedDao.getAnimeByCategory("Upcoming").map { cachedAnimeList ->
            cachedAnimeList.map { cachedAnime ->
                cachedAnime.toUiModel()
            }
        }
    }

    override fun getTopAnime(): Flow<List<AnimeUiModel>> {
        return animeCachedDao.getAnimeByCategory("Top").map { cachedAnimeList ->
            cachedAnimeList.map { cachedAnime ->
                cachedAnime.toUiModel()
            }
        }
    }

    override fun getSeasonAnime(): Flow<List<AnimeUiModel>> {
        return animeCachedDao.getAnimeByCategory("Season").map { cachedAnimeList ->
            cachedAnimeList.map { cachedAnime ->
                cachedAnime.toUiModel()
            }
        }
    }

    override fun getFavoriteAnime(): Flow<List<AnimeUiModel>> {
        return animeCachedDao.getAnimeByCategory("Favorite").map { cachedAnimeList ->
            cachedAnimeList.map {
                it.toUiModel()
            }
        }
    }
}