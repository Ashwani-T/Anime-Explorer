package com.example.animeexplorer.data

import android.util.Log
import com.example.animeexplorer.data.local.dao.AnimeCollectionDao
import com.example.animeexplorer.data.local.dao.AnimeDetailsDao
import com.example.animeexplorer.data.local.dao.AnimeEpisodeDao
import com.example.animeexplorer.data.local.entity.AnimeCollectionsEntity
import com.example.animeexplorer.data.local.entity.LibraryStatus
import com.example.animeexplorer.data.mapper.toAnimeCollectionUiModel
import com.example.animeexplorer.data.mapper.toEpisodeEntityList
import com.example.animeexplorer.data.remote.AnimeApiService
import com.example.animeexplorer.domain.AnimeCollectionRepository
import com.example.animeexplorer.domain.AnimeCollectionUiModel
import com.example.animeexplorer.domain.CollectionUpdateEvent
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.Dispatcher

class AnimeCollectionRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
    private val animeDetailsDao: AnimeDetailsDao,
    private val animeCollectionDao: AnimeCollectionDao,
    private val animeEpisodeDao: AnimeEpisodeDao
) : AnimeCollectionRepository {

    private companion object {
        const val TAG = "AnimeCollectionRepo"
    }

    private suspend fun <T> executeSafe(
        errorMessage: String,
        action: suspend () -> T
    ): Result<T> = try {
        Result.success(action())
    } catch (e: Exception) {
        Log.e(TAG, "$errorMessage: ${e.message}", e)
        Result.failure(e)
    }

    /**
     * Fetches and saves episodes for an anime from the API
     * @return the last episode number fetched, or -1 if no episodes found
     */
    private suspend fun fetchAndSaveEpisodes(malId: Int, page: Int): Int {
        return try {
            val response = apiService.getAnimeEpisodes(malId, page)
            if (response.data.isNotEmpty()) {
                val episodeEntities = response.data.toEpisodeEntityList(malId)
                animeEpisodeDao.insertEpisodes(episodeEntities)
                Log.d(TAG, "Saved ${episodeEntities.size} episodes for $malId (Page $page)")
                episodeEntities.last().episodeNumber
            } else {
                -1
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch episodes for $malId: ${e.message}")
            -1
        }
    }

    /**
     * Synchronizes collection progress with completed episodes from database
     */
    private suspend fun syncProgressFromEpisodes(malId: Int) {
        val completedCount = animeEpisodeDao.getCompletedEpisodesCount(malId)
        animeCollectionDao.updateEpisodesCompleted(malId, completedCount)
    }

    /**
     * Ensures episodes up to a certain episode number are fetched and available
     */
    private suspend fun ensureEpisodesFetched(malId: Int, upToEpisode: Int) {
        val localEpisodes = animeEpisodeDao.getEpisodesByMalId(malId)
        var currentMax = localEpisodes.lastOrNull()?.episodeNumber ?: 0
        var currentPage = (localEpisodes.size / 100) + 1

        while (currentMax < upToEpisode) {
            val lastOnPage = fetchAndSaveEpisodes(malId, currentPage + 1)
            if (lastOnPage == -1) break
            currentMax = lastOnPage
            currentPage++
        }
    }

    override suspend fun addToLibrary(malId: Int, status: LibraryStatus): Result<Unit> =
        executeSafe("Error adding anime $malId to library") {
            val animeDetail = animeDetailsDao.getAnimeDetails(malId)
                ?: throw Exception("Anime details not found in local database")

            fetchAndSaveEpisodes(malId, page = 1)

            val collectionEntity = AnimeCollectionsEntity(
                malId = malId,
                title = animeDetail.title,
                imageUrl = animeDetail.imageUrl,
                type = animeDetail.type,
                status = status,
                episodesCompleted = 0,
                totalEpisodes = animeDetail.episodes
            )

            animeCollectionDao.addToLibrary(collectionEntity)
            Log.d(TAG, "Added anime $malId to library with status $status")
        }

    override suspend fun updateLibraryStatus(malId: Int, status: LibraryStatus): Result<Unit> =
        executeSafe("Error updating status for anime $malId") {
            animeCollectionDao.updateLibraryStatus(malId, status)
            Log.d(TAG, "Updated anime $malId status to $status")
        }

    override suspend fun updateEpisodeRange(malId: Int, upToEpisode: Int): Result<Unit> =
        executeSafe("Error updating episode range for $malId") {
            ensureEpisodesFetched(malId, upToEpisode)

            animeEpisodeDao.updateEpisodeInRange(malId, upToEpisode)

            syncProgressFromEpisodes(malId)
            Log.d(TAG, "Updated episode range for anime $malId up to episode $upToEpisode")
        }

    override suspend fun markAllEpisodeCompleted(malId: Int): Result<Unit> =
        executeSafe("Error marking all episodes completed for $malId") {
            val collection = animeCollectionDao.getCollectionByMalId(malId)
                ?: throw Exception("Collection not found for anime $malId")

            val lastEpisode = animeEpisodeDao.getLastEpisodeNumber(malId)
            if (lastEpisode != null && lastEpisode > 0) {
                animeEpisodeDao.updateEpisodeInRange(malId, lastEpisode)
            }

            // Sync collection progress
            syncProgressFromEpisodes(malId)
            Log.d(TAG, "Marked all episodes as completed for anime $malId")
        }

    override suspend fun removeFromLibrary(malId: Int): Result<Unit> =
        executeSafe("Error removing anime $malId from library") {
            val collection = animeCollectionDao.getCollectionByMalId(malId)
                ?: throw Exception("Anime $malId not found in library")

            animeCollectionDao.removeFromLibrary(collection)
            animeEpisodeDao.deleteEpisodesByMalId(malId)
            Log.d(TAG, "Removed anime $malId from library")
        }

    override suspend fun getLibraryCollection(malId: Int): Result<AnimeCollectionUiModel?> =
        executeSafe("Error fetching collection details for $malId") {
            animeCollectionDao.getCollectionByMalId(malId)?.toAnimeCollectionUiModel()
        }

    override fun getAllLibraryCollections(): Flow<List<AnimeCollectionUiModel>> {
        return animeCollectionDao
            .observeAllCollections()
            .map { entities ->
                entities.map { it.toAnimeCollectionUiModel() }
            }
            .flowOn(Dispatchers.Default)
    }
}
