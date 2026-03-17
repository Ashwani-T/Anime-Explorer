package com.example.animeexplorer.data

import android.util.Log
import com.example.animeexplorer.data.local.dao.AnimeCollectionDao
import com.example.animeexplorer.data.local.dao.AnimeDetailsDao
import com.example.animeexplorer.data.local.dao.AnimeEpisodeDao
import com.example.animeexplorer.data.local.entity.AnimeCollectionsEntity
import com.example.animeexplorer.data.local.entity.LibraryStatus
import com.example.animeexplorer.data.mapper.toAnimeCollectionUiModel
import com.example.animeexplorer.data.mapper.toEpisodeEntity
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

    // MutableSharedFlow to emit collection update events
    private val _collectionUpdates = MutableSharedFlow<CollectionUpdateEvent>()

    override fun getCollectionUpdates(): Flow<CollectionUpdateEvent> = _collectionUpdates

    private suspend fun emitUpdate(event: CollectionUpdateEvent) {
        _collectionUpdates.emit(event)
    }
    override suspend fun addToLibrary(malId: Int, status: LibraryStatus): Result<Unit> {
        return try {
            // Fetch anime details
            val animeDetailResult = animeDetailsDao.getAnimeDetails(malId) ?: return Result.failure(
                Exception("Unable to fetch Anime Details")
            )

            // Fetch episodes for the first page
            val episodesResult = runCatching {
                apiService.getAnimeEpisodes(malId, page = 1)
            }

            val episodes = episodesResult.getOrNull()?.data ?: emptyList()

            // Create AnimeCollectionsEntity
            val collectionEntity = AnimeCollectionsEntity(
                malId = malId,
                title = animeDetailResult.title,
                imageUrl = animeDetailResult.imageUrl,
                type = animeDetailResult.type,
                status = status,
                episodesCompleted = 0,
                totalEpisodes = animeDetailResult.episodes
            )

            // Save to collection
            animeCollectionDao.addToLibrary(collectionEntity)

            // Save episodes if available
            if (episodes.isNotEmpty()) {
                val episodeEntities = episodes.toEpisodeEntityList(malId)
                animeEpisodeDao.insertEpisodes(episodeEntities)
                Log.d("AnimeCollectionRepo", "Added ${episodeEntities.size} episodes for anime $malId")
            } else {
                Log.d("AnimeCollectionRepo", "No episodes found for anime $malId")
            }

            // Emit event
            emitUpdate(CollectionUpdateEvent.Added(malId))
            Log.d("AnimeCollectionRepo", "Added anime $malId to library with status $status")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("AnimeCollectionRepo", "Error adding to library: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateLibraryStatus(malId: Int, status: LibraryStatus): Result<Unit> {
        return try {
            animeCollectionDao.updateLibraryStatus(malId, status)
            // Emit event
            emitUpdate(CollectionUpdateEvent.Updated(malId))
            Log.d("AnimeCollectionRepo", "Updated library status for anime $malId to $status")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AnimeCollectionRepo", "Error updating library status: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun markEpisodeComplete(
        malId: Int,
        episodeNumber: Int,
        isCompleted: Boolean
    ): Result<Unit> {
        return try {
            // Update episode status
            animeEpisodeDao.updateEpisodeStatus(malId, episodeNumber, isCompleted)

            // Update episode progress in collection
            val completedCount = animeEpisodeDao.getCompletedEpisodesCount(malId)
            animeCollectionDao.setEpisodesCompleted(malId, completedCount)

            Log.d(
                "AnimeCollectionRepo",
                "Marked episode $episodeNumber of anime $malId as $isCompleted. Total completed: $completedCount"
            )
            emitUpdate(CollectionUpdateEvent.Updated(malId))
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("AnimeCollectionRepo", "Error marking episode complete: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateEpisodeRange(malId: Int, upToEpisode: Int): Result<Unit> {
        return try {
            var currentPage= 1
            var currentRangeLimit = animeEpisodeDao.getEpisodesByMalId(malId).last().episodeNumber



            while(currentRangeLimit < upToEpisode){
                val currentRangeLimitResult = runCatching{
                    val resp = apiService.getAnimeEpisodes(malId, page = currentPage+1)

                    if (resp.data.isNotEmpty()) {
                        val episodeEntities = resp.data.toEpisodeEntityList(malId)
                        animeEpisodeDao.insertEpisodes(episodeEntities)
                    } else {
                        Log.d("AnimeCollectionRepo", "No episodes found for anime $malId")
                    }
                    currentPage++
                    resp.data.last().toEpisodeEntity(malId).episodeNumber
                }
                currentRangeLimit = currentRangeLimitResult.getOrNull() ?: break
            }

            animeEpisodeDao.updateEpisodeInRange(malId, upToEpisode)

            val completedCount = animeEpisodeDao.getCompletedEpisodesCount(malId)
            animeCollectionDao.setEpisodesCompleted(malId, completedCount)

            Log.d(
                "AnimeCollectionRepo",
                "Updated episode range for anime $malId up to episode $upToEpisode. Total completed: $completedCount"
            )
            emitUpdate(CollectionUpdateEvent.Updated(malId))
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("AnimeCollectionRepo", "Error updating episode range: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun markAllEpisodeCompleted(malId: Int): Result<Unit> {
        return try {
            val episodes = animeEpisodeDao.getEpisodesByMalId(malId)

            animeEpisodeDao.updateEpisodeInRange(malId, episodes.last().episodeNumber)

            val completedCount = animeEpisodeDao.getCompletedEpisodesCount(malId)
            animeCollectionDao.setEpisodesCompleted(malId, completedCount)

            Log.d(
                "AnimeCollectionRepo",
                "Completely marked episode for anime $malId . Total completed: $completedCount"
            )
            emitUpdate(CollectionUpdateEvent.Updated(malId))
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun removeFromLibrary(malId: Int): Result<Unit> {
        return try {
            val collection = animeCollectionDao.getCollectionByMalId(malId)
            if (collection != null) {
                animeCollectionDao.removeFromLibrary(collection)
                // Optionally delete episodes associated with this anime
                animeEpisodeDao.deleteEpisodesByMalId(malId)
                // Emit event
                emitUpdate(CollectionUpdateEvent.Removed(malId))
                Log.d("AnimeCollectionRepo", "Removed anime $malId from library")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Anime not found in library"))
            }
        } catch (e: Exception) {
            Log.e("AnimeCollectionRepo", "Error removing from library: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getLibraryCollection(malId: Int): Result<AnimeCollectionUiModel?> {
        return try {
            val collection = animeCollectionDao.getCollectionByMalId(malId)?.toAnimeCollectionUiModel()
            Result.success(collection)
        } catch (e: Exception) {
            Log.e("AnimeCollectionRepo", "Error fetching collection: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllLibraryCollections(): Result<List<AnimeCollectionUiModel>> {
        return try {
            val collections = animeCollectionDao.getAllCollections().map { entity->
                entity.toAnimeCollectionUiModel()
            }
            Result.success(collections)
        } catch (e: Exception) {
            Log.e("AnimeCollectionRepo", "Error fetching all collections: ${e.message}", e)
            Result.failure(e)
        }
    }
}