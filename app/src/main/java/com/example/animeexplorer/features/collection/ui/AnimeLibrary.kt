package com.example.animeexplorer.features.collection.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.animeexplorer.core.components.ArcLoader
import com.example.animeexplorer.core.domain.AnimeCollectionUiModel
import com.example.animeexplorer.core.components.LinearProgressCustom
import com.example.animeexplorer.core.domain.enums.SortOrder
import com.example.animeexplorer.features.collection.data.local.entity.LibraryStatus
import com.example.animeexplorer.features.search.ui.SearchBar
import com.example.animeexplorer.ui.theme.AppTheme


@Composable
fun AnimeLibrary(
    onClick: (Int) -> Unit,
    viewModel: AnimeLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyGridState()
    
    var visibleLibraryFilterRow by remember{ mutableStateOf(true) }

    Box(modifier = Modifier.padding(6.dp)) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    ArcLoader()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    state = listState,
                    contentPadding = PaddingValues(6.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SearchBar(
                                searchQuery = uiState.searchQuery,
                                onSearchQueryChange = {viewModel.onSearchQueryChange(it)},
                                sortOrder = SortOrder.ASC,
                                toggleSortOrder = {},
                                showMainFilterSheet = {},
                            )
                            LibraryFilterRow(
                                selectedStatus = uiState.selectedFilter,
                                onStatusSelected = { status -> viewModel.filterByStatus(status) },
                                onClearFilter = { viewModel.clearFilter() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    if (uiState.collections.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "No anime in your library",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = "Add anime from the home screen",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                    }
                    items(
                        items = uiState.collections,
                        key = { it.malId }
                    ) { item ->
                        AnimeCollectionCard(
                            item = item,
                            onClick = { onClick(item.malId) }
                        )
                    }
                }
            }
        }
    }
}
//
//@Composable
//fun SearchBar(
//    query: String,
//    onQueryChange: (String) -> Unit,
//    selectedPreset: String,
//    onClear: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    OutlinedTextField(
//        value = query,
//        onValueChange = onQueryChange,
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(4.dp),
//        placeholder = { Text("Search in ${selectedPreset}...") },
//        leadingIcon = {
//            Icon(imageVector = Icons.Default.Search, contentDescription = null)
//        },
//        trailingIcon = {
//            IconButton(onClick = onClear) {
//                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
//            }
//        },
//        singleLine = true,
//        shape = RoundedCornerShape(12.dp),
//        colors = OutlinedTextFieldDefaults.colors(
//            focusedBorderColor = Color.Transparent,
//            unfocusedBorderColor = Color.Transparent,
//        )
//    )
//}

@Composable
fun LibraryFilterRow(
    selectedStatus: LibraryStatus?,
    onStatusSelected: (LibraryStatus) -> Unit,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            FilterChip(
                selected = selectedStatus == null,
                onClick = { onClearFilter() },
                label = { Text("All") },
                leadingIcon = null,
            )
        }
        items(LibraryStatus.entries.size) { index ->
            val status = LibraryStatus.entries[index]
            if (status != LibraryStatus.UNLISTED) {
                val statusLabel = status.name.replace("_", " ")
                    .lowercase()
                    .replaceFirstChar { it.uppercase() }

                FilterChip(
                    selected = selectedStatus == status,
                    onClick = { onStatusSelected(status) },
                    label = { Text(statusLabel) },
                    leadingIcon = null,
                )
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeCollectionCard(
    item: AnimeCollectionUiModel,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = { onClick(item.malId) },
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
    ) {
        Column(Modifier.fillMaxSize()) {
            // Card image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {

                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = item.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Title and progress info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))

                LinearProgressCustom(
                    progress = item.progress,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimeLibraryPreview(modifier: Modifier = Modifier) {
    AppTheme {
        AnimeLibrary(
            onClick = { },

            )
    }
}