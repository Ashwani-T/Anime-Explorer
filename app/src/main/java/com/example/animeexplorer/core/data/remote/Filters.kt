package com.example.animeexplorer.core.data.remote

import retrofit2.http.Query

data class Filters(
    val genres: String? = null,
    val type: String? = null,
    val status: String? = null,
    val rating: String?= null,
    val orderBy: String? = null,
    val sort: String? = null
){
    fun toQueryMap(): Map<String, String> = buildMap {
        genres?.let { put("genres",it) }
        type?.let { put("type",it) }
        status?.let { put("status",it) }
        rating?.let { put("rating",it) }
        orderBy?.let { put("order_by",it) }
        sort?.let { put("sort",it) }
    }
}