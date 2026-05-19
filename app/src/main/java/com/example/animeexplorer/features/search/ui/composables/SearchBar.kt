package com.example.animeexplorer.features.search.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.animeexplorer.core.domain.enums.SortOrder
import com.example.animeexplorer.features.search.ui.SearchTestTags


@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortOrder: SortOrder,
    enableSortOrder: Boolean = true,
    toggleSortOrder: () -> Unit,
    showMainFilterSheet: () -> Unit,
    enableFilterSheet: Boolean = true,
) {
    Row(
        modifier = Modifier
            .testTag(SearchTestTags.SEARCH_BAR)
            .padding(horizontal = 4.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { onSearchQueryChange(it) },
            modifier = Modifier
                .weight(1f)
                .testTag(SearchTestTags.SEARCH_INPUT),
            placeholder = { Text("Search anime...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            trailingIcon = {
                SearchBarTrailingActions(
                    searchQuery = searchQuery,
                    onClear = { onSearchQueryChange("") },
                    enableFilterSheet = enableFilterSheet,
                    onOpenFilter = showMainFilterSheet
                )
            },
            shape = RoundedCornerShape(28.dp),
            singleLine = true
        )
        if (enableSortOrder) {
            SortOrderButton(
                sortOrder = sortOrder,
                onToggle = toggleSortOrder
            )
        }
    }
}


@Composable
fun SearchBarTrailingActions(
    searchQuery: String,
    onClear: () -> Unit,
    enableFilterSheet: Boolean,
    onOpenFilter: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 4.dp)
    ) {
        if (searchQuery.isNotEmpty()) {
            IconButton(
                onClick = {
                    onClear()
                },
                modifier = Modifier
                    .size(40.dp)
                    .testTag(SearchTestTags.CLEAR_SEARCH_BUTTON)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear search",
                    modifier = Modifier.size(20.dp)
                )
            }

        }
        if (enableFilterSheet) {
            IconButton(
                onClick = { onOpenFilter() },
                modifier = Modifier.testTag(SearchTestTags.OPEN_FILTER_BUTTON)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter Icon"
                )
            }
        }
    }
}



@Composable
fun SortOrderButton(sortOrder: SortOrder, onToggle: () -> Unit) {
    IconButton(
        onClick = { onToggle() },
        modifier = Modifier
            .testTag(SearchTestTags.SORT_ORDER_BUTTON)
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = if (sortOrder == SortOrder.ASC)
                Icons.Default.ArrowUpward
            else
                Icons.Default.ArrowDownward,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )
    }
}


