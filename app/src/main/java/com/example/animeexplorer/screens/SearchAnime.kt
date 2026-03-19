package com.example.animeexplorer.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAnime(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    var showMainFilterSheet by remember { mutableStateOf(false) }
    
    // Filter States
    var selectedSort by remember { mutableStateOf("Select Sort") }
    var selectedSeason by remember { mutableStateOf("Select Season") }
    var selectedStatus by remember { mutableStateOf("Select Status") }
    var selectedFormat by remember { mutableStateOf("Select Format") }
    var selectedGenres by remember { mutableStateOf(setOf<String>()) }

    // Sub-sheet states
    var activeSubSheet by remember { mutableStateOf<SubSheetType?>(null) }

    val filters = listOf("Season", "Trending", "Popular")
    var quickFilter by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search anime...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            trailingIcon = {
                IconButton(onClick = { showMainFilterSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter Icon"
                    )
                }
            },
            shape = RoundedCornerShape(28.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Prebuilt Filters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = quickFilter == filter,
                    onClick = { 
                        quickFilter = if (quickFilter == filter) null else filter 
                    },
                    label = { Text(filter) }
                )
            }
        }

        // Main Filter Bottom Sheet
        if (showMainFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMainFilterSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                FilterSheetContent(
                    selectedSort = selectedSort,
                    selectedSeason = selectedSeason,
                    selectedStatus = selectedStatus,
                    selectedFormat = selectedFormat,
                    selectedGenres = selectedGenres,
                    onOpenSubSheet = { activeSubSheet = it },
                    onGenreToggle = { genre ->
                        selectedGenres = if (selectedGenres.contains(genre)) {
                            selectedGenres - genre
                        } else {
                            selectedGenres + genre
                        }
                    },
                    onReset = {
                        selectedSort = "Select Sort"
                        selectedSeason = "Select Season"
                        selectedStatus = "Select Status"
                        selectedFormat = "Select Format"
                        selectedGenres = emptySet()
                    },
                    onApply = {
                        showMainFilterSheet = false
                    }
                )
            }
        }

        // Sub-bottom sheets
        activeSubSheet?.let { type ->
            ModalBottomSheet(
                onDismissRequest = { activeSubSheet = null },
                sheetState = rememberModalBottomSheetState()
            ) {
                SelectionSheetContent(
                    type = type,
                    onSelect = { value ->
                        when (type) {
                            SubSheetType.SORT -> selectedSort = value
                            SubSheetType.SEASON -> selectedSeason = value
                            SubSheetType.STATUS -> selectedStatus = value
                            SubSheetType.FORMAT -> selectedFormat = value
                        }
                        activeSubSheet = null
                    }
                )
            }
        }
    }
}

enum class SubSheetType { SORT, SEASON, STATUS, FORMAT }

@Composable
fun FilterSheetContent(
    selectedSort: String,
    selectedSeason: String,
    selectedStatus: String,
    selectedFormat: String,
    selectedGenres: Set<String>,
    onOpenSubSheet: (SubSheetType) -> Unit,
    onGenreToggle: (String) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
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
        FilterOptionCard(
            label = "SORT BY",
            value = selectedSort,
            icon = Icons.AutoMirrored.Filled.Sort,
            onClick = { onOpenSubSheet(SubSheetType.SORT) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // FILTERS Section
        SectionHeader(icon = Icons.AutoMirrored.Filled.Sort, title = "FILTERS") // Reusing icon for visual consistency
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            FilterOptionCard(
                label = "SEASON",
                value = selectedSeason,
                icon = Icons.Default.CalendarMonth,
                onClick = { onOpenSubSheet(SubSheetType.SEASON) },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            FilterOptionCard(
                label = "STATUS",
                value = selectedStatus,
                icon = Icons.Default.Info,
                onClick = { onOpenSubSheet(SubSheetType.STATUS) },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        FilterOptionCard(
            label = "FORMAT",
            value = selectedFormat,
            icon = Icons.Default.VideoLibrary,
            onClick = { onOpenSubSheet(SubSheetType.FORMAT) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // GENRES Section
        SectionHeader(icon = Icons.Default.Category, title = "GENRES")
        Spacer(modifier = Modifier.height(12.dp))
        val genres = listOf("Action", "Adventure", "Comedy", "Drama", "Fantasy", "Horror", "Mystery", "Romance", "Sci-Fi", "Slice of Life")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(genres) { genre ->
                GenreChip(
                    label = genre,
                    isSelected = selectedGenres.contains(genre),
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
                modifier = Modifier.weight(0.4f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("RESET", color = MaterialTheme.colorScheme.onSurface)
            }
            Button(
                onClick = onApply,
                modifier = Modifier.weight(0.6f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9EA7E5))
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
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
                Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
        }
    }
}

@Composable
fun GenreChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF9EA7E5) else Color.Gray.copy(alpha = 0.5f)),
        color = if (isSelected) Color(0xFF9EA7E5).copy(alpha = 0.2f) else Color.Transparent
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color(0xFF9EA7E5) else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SelectionSheetContent(type: SubSheetType, onSelect: (String) -> Unit) {
    val options = when (type) {
        SubSheetType.SORT -> listOf("Popularity", "Score", "Latest", "A-Z")
        SubSheetType.SEASON -> listOf("Winter", "Spring", "Summer", "Fall")
        SubSheetType.STATUS -> listOf("Airing", "Finished", "Upcoming", "Hiatus")
        SubSheetType.FORMAT -> listOf("TV", "Movie", "OVA", "Special", "ONA")
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Select ${type.name.lowercase().replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn {
            items(options) { option ->
                Text(
                    text = option,
                    modifier = Modifier
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
