package com.example.animeexplorer.data

import android.util.Log
import com.example.animeexplorer.data.local.dao.AnimeCollectionDao
import com.example.animeexplorer.data.local.dao.AnimeDetailsDao
import com.example.animeexplorer.data.local.entity.AnimeCollectionsEntity
import com.example.animeexplorer.data.local.entity.LibraryStatus
import com.example.animeexplorer.data.mapper.toAnimeCollectionUiModel
import com.example.animeexplorer.domain.AnimeCollectionRepository
import com.example.animeexplorer.domain.AnimeCollectionUiModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class AnimeCollectionRepositoryImpl @Inject constructor(
    private val animeDetailsDao: AnimeDetailsDao,
    private val animeCollectionDao: AnimeCollectionDao
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

    override suspend fun addToLibrary(malId: Int, status: LibraryStatus): Result<Unit> =
        executeSafe("Error adding anime $malId to library") {
            val animeDetail = animeDetailsDao.getAnimeDetails(malId)
                ?: throw Exception("Anime details not found in local database")

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

    override suspend fun updateEpisodesCompleted(malId: Int, count: Int): Result<Unit> =
        executeSafe("Error updating episodes completed for $malId") {
            animeCollectionDao.updateEpisodesCompleted(malId, count)
            Log.d(TAG, "Updated episodes completed for anime $malId to $count")
        }

    override suspend fun removeFromLibrary(malId: Int): Result<Unit> =
        executeSafe("Error removing anime $malId from library") {
            val collection = animeCollectionDao.getCollectionByMalId(malId)
                ?: throw Exception("Anime $malId not found in library")

            animeCollectionDao.removeFromLibrary(collection)
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