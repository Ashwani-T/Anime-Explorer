package com.example.animeexplorer.features.search.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.animeexplorer.core.domain.enums.FormatType
import com.example.animeexplorer.core.domain.enums.RatingType
import com.example.animeexplorer.core.domain.enums.SortType
import com.example.animeexplorer.core.domain.enums.StatusType
import com.example.animeexplorer.features.search.ui.SearchTestTags
import com.example.animeexplorer.features.search.ui.SubSheetType


@Composable
fun SelectionSheetContent(type: SubSheetType, onSelect: (Any) -> Unit) {
    val options: List<Any> = when (type) {
        SubSheetType.SORT -> SortType.entries
        SubSheetType.FORMAT -> FormatType.entries
        SubSheetType.STATUS -> StatusType.entries
        SubSheetType.RATING -> RatingType.entries
    }

    Column(
        modifier = Modifier
            .testTag(SearchTestTags.filterSubSheet(type))
            .padding(16.dp)
    ) {
        Text(
            text = "Select ${type.name.lowercase().replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn {
            items(options) { option ->
                val displayName = when (option) {
                    is SortType -> option.displayName
                    is FormatType -> option.displayName
                    is StatusType -> option.displayName
                    is RatingType -> option.displayName
                    else -> option.toString()
                }
                Text(
                    text = displayName,
                    modifier = Modifier
                        .testTag(SearchTestTags.filterSubSheetOption(type, displayName))
                        .fillMaxWidth()
                        .clickable { onSelect(option) }
                        .padding(vertical = 12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
