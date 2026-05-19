package com.example.animeexplorer.features.search.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animeexplorer.features.search.domain.model.GenreModel
import com.example.animeexplorer.features.search.ui.SearchTestTags
import com.example.animeexplorer.features.search.ui.SearchUiState
import com.example.animeexplorer.features.search.ui.SubSheetType


@Composable
fun FilterSheetContent(
    uiState: SearchUiState,
    onOpenSubSheet: (SubSheetType) -> Unit,
    onGenreToggle: (GenreModel) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .testTag(SearchTestTags.FILTER_SHEET_CONTENT)
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "FILTERS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SORT Section
        SectionHeader(icon = Icons.AutoMirrored.Filled.Sort, title = "SORT")
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterOptionCard(
                label = "SORT BY",
                value = uiState.selectedSort?.displayName ?: "Select Sort",
                icon = Icons.AutoMirrored.Filled.Sort,
                onClick = { onOpenSubSheet(SubSheetType.SORT) },
                modifier = Modifier.weight(1f),
                testTag = SearchTestTags.filterOption(SubSheetType.SORT)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(icon = Icons.AutoMirrored.Filled.Sort, title = "FILTERS")
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            FilterOptionCard(
                label = "FORMAT",
                value = uiState.selectedFormat?.displayName ?: "Select Format",
                icon = Icons.Default.VideoLibrary,
                onClick = { onOpenSubSheet(SubSheetType.FORMAT) },
                modifier = Modifier.weight(1f),
                testTag = SearchTestTags.filterOption(SubSheetType.FORMAT)
            )
            Spacer(modifier = Modifier.width(12.dp))
            FilterOptionCard(
                label = "STATUS",
                value = uiState.selectedStatus?.displayName ?: "Select Status",
                icon = Icons.Default.Info,
                onClick = { onOpenSubSheet(SubSheetType.STATUS) },
                modifier = Modifier.weight(1f),
                testTag = SearchTestTags.filterOption(SubSheetType.STATUS)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        FilterOptionCard(
            label = "RATING",
            value = uiState.selectedRating?.displayName ?: "Select Rating",
            icon = Icons.Default.Star,
            onClick = { onOpenSubSheet(SubSheetType.RATING) },
            testTag = SearchTestTags.filterOption(SubSheetType.RATING)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // GENRES Section
        SectionHeader(icon = Icons.Default.Category, title = "GENRES")
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            modifier = Modifier.testTag(SearchTestTags.FILTER_GENRE_ROW),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.availableGenres) { genre ->
                GenreChip(
                    label = genre.name,
                    isSelected = uiState.selectedGenres.any { it.malId == genre.malId },
                    testTag = SearchTestTags.genreChip("${genre.malId}_${genre.name}"),
                    onClick = { onGenreToggle(genre) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Bottom Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .weight(0.4f)
                    .testTag(SearchTestTags.FILTER_RESET_BUTTON),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("RESET", color = MaterialTheme.colorScheme.onSurface)
            }
            Button(
                onClick = onApply,
                modifier = Modifier
                    .weight(0.6f)
                    .testTag(SearchTestTags.FILTER_APPLY_BUTTON),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9EA7E5))
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("APPLY FILTERS", color = Color.DarkGray, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(4.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun FilterOptionCard(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null
) {
    Card(
        modifier = modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.3f
            )
        ),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
        }
    }
}
