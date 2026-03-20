package com.example.animeexplorer.domain.enums

enum class SortType(val apiName: String, val displayName: String) {
    POPULARITY("popularity", "Popularity"),
    SCORE("score", "Score"),
    START_DATE("start_date", "Latest"),
    FAVORITE("favorites", "Favorite")
}

enum class SortOrder(val apiName: String, val displayName: String) {
    ASC("asc", "ASC"),
    DESC("desc", "DESC")
}

enum class FormatType(val apiName: String, val displayName: String) {
    TV("tv", "TV"),
    MOVIE("movie", "Movie"),
    OVA("ova", "OVA"),
    SPECIAL("special", "Special"),
    ONA("ona", "ONA")
}

enum class StatusType(val apiName: String, val displayName: String) {
    AIRING("airing", "Airing"),
    FINISHED("complete", "Finished"),
    UPCOMING("upcoming", "Upcoming")
}

enum class RatingType(val apiName: String, val displayName: String) {
    G("g", "All Ages"),
    PG("pg", "Children"),
    PG13("pg13", "Teens 13 or older"),
    R17("r17", "17+ (violence & profanity)"),
    R("r", "Mild Nudity"),
    RX("rx", "Hentai")
}
