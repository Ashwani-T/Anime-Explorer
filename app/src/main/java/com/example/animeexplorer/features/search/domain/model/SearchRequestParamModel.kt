package com.example.animeexplorer.features.search.domain.model

data class SearchRequestParamModel(
    val q: String? = null,           // if API expects "q"
    val page: Int = 1,
    val orderBy: String? = null,     // or "order_by" based on backend
    val sort: String? = null,        // asc/desc
    val type: String? = null,        // format
    val status: String? = null,
    val rating: String? = null,
    val genres: String? = null       // comma-separated ids
)