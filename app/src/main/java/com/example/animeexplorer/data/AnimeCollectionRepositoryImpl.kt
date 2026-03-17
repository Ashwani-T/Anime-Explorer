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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class AnimeCollectionRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
    private val animeDetailsDao: AnimeDetailsDao,
    private val animeCollectionDao: AnimeCollectionDao,
    private val animeEpisodeDao: AnimeEpisodeDao
) : AnimeCollectionRepository {

    private companion object {
        const val TAG = "AnimeCollectionRepo"
    }

    private val _collectionUpdates = MutableSharedFlow<CollectionUpdateEvent>()

    override fun getCollectionUpdates(): Flow<CollectionUpdateEvent> = _collectionUpdates

    private suspend fun emitUpdate(event: CollectionUpdateEvent) {
        _collectionUpdates.emit(event)
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


    private suspend fun updateCollectionProgressDirectly(malId: Int, completedCount: Int) {
        animeCollectionDao.setEpisodesCompleted(malId, completedCount)
        emitUpdate(CollectionUpdateEvent.Updated(malId))
        Log.d(TAG, "Progress for anime $malId updated to $completedCount")
    }


    private suspend fun syncCollectionProgressFromEpisodes(malId: Int) {
        val completedCount = animeEpisodeDao.getCompletedEpisodesCount(malId)
        updateCollectionProgressDirectly(malId, completedCount)
    }

    private suspend fun handleProgressUpdate(
        malId: Int,
        fallbackCount: Int,
        onHasEpisodes: suspend () -> Unit
    ) {
        if (animeEpisodeDao.getTotalEpisodesCount(malId) > 0) {
            onHasEpisodes()
            syncCollectionProgressFromEpisodes(malId)
        } else {
            updateCollectionProgressDirectly(malId, fallbackCount)
        }
    }


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

    override suspend fun addToLibrary(malId: Int, status: LibraryStatus): Result<Unit> =
        executeSafe("Error adding anime $malId to library") {
            val animeDetail = animeDetailsDao.getAnimeDetails(malId)
                ?: throw Exception("Anime details not found in local database")

            // Attempt to fetch initial episodes
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
            emitUpdate(CollectionUpdateEvent.Added(malId))
        }

    override suspend fun updateLibraryStatus(malId: Int, status: LibraryStatus): Result<Unit> =
        executeSafe("Error updating status for anime $malId") {
            animeCollectionDao.updateLibraryStatus(malId, status)
            emitUpdate(CollectionUpdateEvent.Updated(malId))
        }

    override suspend fun markEpisodeComplete(
        malId: Int,
        episodeNumber: Int,
        isCompleted: Boolean
    ): Result<Unit> = executeSafe("Error marking episode $episodeNumber as completed for $malId") {
        handleProgressUpdate(malId, if (isCompleted) episodeNumber else 0) {
            animeEpisodeDao.updateEpisodeStatus(malId, episodeNumber, isCompleted)
        }
    }

    override suspend fun updateEpisodeRange(malId: Int, upToEpisode: Int): Result<Unit> =
        executeSafe("Error updating episode range for $malId") {
            val localEpisodes = animeEpisodeDao.getEpisodesByMalId(malId)
            var currentMax = localEpisodes.lastOrNull()?.episodeNumber ?: 0
            var currentPage = (localEpisodes.size / 100) + 1

            // Fetch missing episodes if necessary
            while (currentMax < upToEpisode) {
                val lastOnPage = fetchAndSaveEpisodes(malId, currentPage + 1)
                if (lastOnPage == -1) break
                currentMax = lastOnPage
                currentPage++
            }

            handleProgressUpdate(malId, upToEpisode) {
                animeEpisodeDao.updateEpisodeInRange(malId, upToEpisode)
            }
        }

    override suspend fun markAllEpisodeCompleted(malId: Int): Result<Unit> =
        executeSafe("Error marking all episodes completed for $malId") {
            val collection = animeCollectionDao.getCollectionByMalId(malId)
            val totalCount = collection?.totalEpisodes ?: 1
            
            handleProgressUpdate(malId, totalCount) {
                val lastEpisode = animeEpisodeDao.getEpisodesByMalId(malId).lastOrNull()?.episodeNumber ?: 0
                if (lastEpisode > 0) {
                    animeEpisodeDao.updateEpisodeInRange(malId, lastEpisode)
                }
            }
        }

    override suspend fun removeFromLibrary(malId: Int): Result<Unit> =
        executeSafe("Error removing anime $malId from library") {
            val collection = animeCollectionDao.getCollectionByMalId(malId)
                ?: throw Exception("Anime $malId not found in library")

            animeCollectionDao.removeFromLibrary(collection)
            animeEpisodeDao.deleteEpisodesByMalId(malId)
            emitUpdate(CollectionUpdateEvent.Removed(malId))
        }

    override suspend fun getLibraryCollection(malId: Int): Result<AnimeCollectionUiModel?> =
        executeSafe("Error fetching collection details for $malId") {
            animeCollectionDao.getCollectionByMalId(malId)?.toAnimeCollectionUiModel()
        }

    override suspend fun getAllLibraryCollections(): Result<List<AnimeCollectionUiModel>> =
        executeSafe("Error fetching all library collections") {
            animeCollectionDao.getAllCollections().map { it.toAnimeCollectionUiModel() }
        }
}
