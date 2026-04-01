package com.example.animeexplorer.features.detail.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.animeexplorer.features.detail.data.local.entity.AnimeDetailsEntity
import com.example.animeexplorer.core.domain.AnimeUiModel

data class CachedAnimeItem(
    val malId: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val type: String,
    val score: Double
)

fun CachedAnimeItem.toUiModel(): AnimeUiModel {
    return AnimeUiModel(
        id = malId,
        title = title,
        description = description,
        duration = "",
        imageUrl = imageUrl,
        score = score
    )
}

@Dao
interface AnimeDetailsDao{


    @Insert
    suspend fun insertAnimeDetails(anime: AnimeDetailsEntity)

    @Query("SELECT * FROM anime_details WHERE malId = :id")
    suspend fun getAnimeDetails(id: Int): AnimeDetailsEntity?

    @Query("SELECT malId, imageUrl, title, synopsis as description, type, score FROM anime_details")
    suspend fun getAnimeList(): List<CachedAnimeItem>

    @Query("SELECT malId, imageUrl, title, synopsis as description, type, score FROM anime_details WHERE title LIKE '%'||:query ||'%'")
    suspend fun getSearchedAnimeList(query: String): List<CachedAnimeItem>
}
