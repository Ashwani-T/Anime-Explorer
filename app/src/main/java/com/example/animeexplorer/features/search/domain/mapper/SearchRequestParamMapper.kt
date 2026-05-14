package com.example.animeexplorer.features.search.domain.mapper

import com.example.animeexplorer.features.search.domain.model.SearchRequestParamModel
import com.example.animeexplorer.features.search.ui.SearchUiState


fun SearchUiState.toRequestParams(): SearchRequestParamModel {
    return SearchRequestParamModel(
        q = searchQuery.takeIf { it.isNotBlank() },
        page = pageNumber,
        orderBy = selectedSort?.apiName,      // expose apiValue on enum
        sort = sortOrder.name.lowercase(),     // "asc"/"desc"
        type = selectedFormat?.apiName,
        status = selectedStatus?.apiName,
        rating = selectedRating?.apiName,
        genres = selectedGenres
            .map { it.malId }
            .sorted()
            .joinToString(",")
            .takeIf { it.isNotBlank() }
    )
}

fun SearchRequestParamModel.toQueryMap(): Map<String, String> = buildMap {
    q?.let { put("q", it) }
    put("page", page.toString())
    orderBy?.let { put("order_by", it) }
    sort?.let { put("sort", it) }
    type?.let { put("type", it) }
    status?.let { put("status", it) }
    rating?.let { put("rating", it) }
    genres?.let { put("genres", it) }
}