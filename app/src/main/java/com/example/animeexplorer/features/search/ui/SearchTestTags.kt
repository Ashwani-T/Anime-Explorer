package com.example.animeexplorer.features.search.ui

import java.util.Locale

object SearchTestTags {
    private const val PREFIX = "search"

    const val SCREEN = "$PREFIX:screen"
    const val SEARCH_BAR = "$PREFIX:search_bar"
    const val SEARCH_INPUT = "$PREFIX:search_input"
    const val SORT_ORDER_BUTTON = "$PREFIX:sort_order_button"
    const val CLEAR_SEARCH_BUTTON = "$PREFIX:clear_search_button"
    const val OPEN_FILTER_BUTTON = "$PREFIX:open_filter_button"
    const val ACTIVE_FILTER_ROW = "$PREFIX:active_filter_row"
    const val ANIME_GRID = "$PREFIX:anime_grid"
    const val EMPTY_STATE = "$PREFIX:empty_state"
    const val INITIAL_LOADING = "$PREFIX:initial_loading"
    const val PAGINATION_LOADING = "$PREFIX:pagination_loading"
    const val MAIN_FILTER_SHEET = "$PREFIX:main_filter_sheet"
    const val SUB_FILTER_SHEET = "$PREFIX:sub_filter_sheet"
    const val FILTER_SHEET_CONTENT = "$PREFIX:filter_sheet_content"
    const val FILTER_GENRE_ROW = "$PREFIX:filter_genre_row"
    const val FILTER_RESET_BUTTON = "$PREFIX:filter_reset_button"
    const val FILTER_APPLY_BUTTON = "$PREFIX:filter_apply_button"

    fun animeItem(id: Int): String = "$PREFIX:anime_item:$id"

    fun filterOption(type: SubSheetType): String = "$PREFIX:filter_option:${type.name.lowercase(Locale.ROOT)}"

    fun filterSubSheet(type: SubSheetType): String = "$PREFIX:sub_sheet:${type.name.lowercase(Locale.ROOT)}"

    fun filterSubSheetOption(type: SubSheetType, value: String): String =
        "$PREFIX:sub_sheet_option:${type.name.lowercase(Locale.ROOT)}:${toKey(value)}"

    fun appliedFilterChip(value: String): String = "$PREFIX:applied_chip:${toKey(value)}"

    fun appliedFilterRemove(value: String): String = "$PREFIX:applied_chip_remove:${toKey(value)}"

    fun genreChip(value: String): String = "$PREFIX:genre_chip:${toKey(value)}"

    private fun toKey(value: String): String {
        return value
            .trim()
            .lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
    }
}
