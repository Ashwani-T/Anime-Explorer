package com.example.animeexplorer.features.search.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.animeexplorer.features.search.domain.model.GenreModel
import com.example.animeexplorer.features.search.ui.SearchTestTags
import com.example.animeexplorer.features.search.ui.SearchUiState


@Composable
fun ActiveFilterChips(
    uiState: SearchUiState,
    onRemoveSort: () -> Unit,
    onRemoveFormat: () -> Unit,
    onRemoveStatus: () -> Unit,
    onRemoveRating: () -> Unit,
    onRemoveGenre: (GenreModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasActiveFilters = uiState.selectedSort != null ||
            uiState.selectedFormat != null ||
            uiState.selectedStatus != null ||
            uiState.selectedRating != null ||
            uiState.selectedGenres.isNotEmpty()

    if (hasActiveFilters) {
        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .testTag(SearchTestTags.ACTIVE_FILTER_ROW),
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            uiState.selectedSort?.let {
                item {
                    AppliedFilterChip(
                        label = "Sort: ${it.displayName}",
                        onRemove = onRemoveSort,
                        tagKey = "sort_${it.name}"
                    )
                }
            }
            uiState.selectedFormat?.let {
                item {
                    AppliedFilterChip(
                        label = "Format: ${it.displayName}",
                        onRemove = onRemoveFormat,
                        tagKey = "format_${it.name}"
                    )
                }
            }
            uiState.selectedStatus?.let {
                item {
                    AppliedFilterChip(
                        label = "Status: ${it.displayName}",
                        onRemove = onRemoveStatus,
                        tagKey = "status_${it.name}"
                    )
                }
            }
            uiState.selectedRating?.let {
                item {
                    AppliedFilterChip(
                        label = "Rating: ${it.displayName}",
                        onRemove = onRemoveRating,
                        tagKey = "rating_${it.name}"
                    )
                }
            }
            items(uiState.selectedGenres.toList()) { genre ->
                AppliedFilterChip(
                    label = genre.name,
                    onRemove = { onRemoveGenre(genre) },
                    tagKey = "genre_${genre.malId}_${genre.name}"
                )
            }
        }
    }
}

@Composable
fun AppliedFilterChip(
    label: String,
    onRemove: () -> Unit,
    tagKey: String = label
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .testTag(SearchTestTags.appliedFilterChip(tagKey))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Remove filter",
                modifier = Modifier
                    .size(14.dp)
                    .testTag(SearchTestTags.appliedFilterRemove(tagKey))
                    .clickable { onRemove() },
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
