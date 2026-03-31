package com.example.animeexplorer.screens

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.animeexplorer.core.components.AnimeItem
import com.example.animeexplorer.core.components.ArcLoader
import com.example.animeexplorer.domain.enums.FormatType
import com.example.animeexplorer.domain.enums.RatingType
import com.example.animeexplorer.domain.enums.SortOrder
import com.example.animeexplorer.domain.enums.SortType
import com.example.animeexplorer.domain.enums.StatusType
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchAnime(
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit,
    onFabVisibilityChanged: (Boolean) -> Unit,
    registerScrollToTop: ((() -> Unit) -> Unit),
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {

    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()



//    val lifecycle = ProcessLifecycleOwner.get().lifecycle
//    var wasInBackground by remember{mutableStateOf(false)}
//
//    DisposableEffect(lifecycle) {
//        val observer = LifecycleEventObserver{_,event ->
//            Log.d("TAG", "EVENT TRIGGERRED: ${event.name}")
//            when(event){
//                Lifecycle.Event.ON_STOP -> {
//                    wasInBackground = true
//                }
//
//                Lifecycle.Event.ON_START ->{
//                    if(wasInBackground){
//                        //viewModel.resetAndReloadAnime()
//                        wasInBackground=false
//                    }
//                }
//                else -> Unit
//            }
//        }
//
//        lifecycle.addObserver(observer)
//
//        onDispose {
//            //lifecycle.removeObserver(observer)
//        }
//    }

    // Bottom Sheet
    var showMainFilterSheet by remember { mutableStateOf(false) }
    var activeSubSheet by remember { mutableStateOf<SubSheetType?>(null) }



    // Managing FAB Functionality
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    val showFab by remember {
        derivedStateOf { lazyGridState.firstVisibleItemIndex > 6 }
    }

    LaunchedEffect(lazyGridState) {
        snapshotFlow { showFab }
            .distinctUntilChanged()
            .collect { visible -> onFabVisibilityChanged(visible) }
    }

    LaunchedEffect(Unit) {
        registerScrollToTop { scope.launch { lazyGridState.animateScrollToItem(0) } }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        // Search Bar
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search anime...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    viewModel.onSearchQueryChange("")
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        IconButton(onClick = { showMainFilterSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter Icon"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(28.dp),
                singleLine = true
            )
            IconButton(
                onClick = viewModel::toggleSortOrder,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (uiState.sortOrder == SortOrder.ASC)
                        Icons.Default.ArrowUpward
                    else
                        Icons.Default.ArrowDownward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        if (uiState.animeList.isEmpty() && uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ArcLoader()
            }
        }

        // Anime Grid
        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            if (uiState.animeList.isEmpty() && !uiState.isLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptySheet()
                }

            } else {
                items(uiState.animeList, key = { it.id }) { anime ->
                    AnimeItem(
                        anime = anime,
                        onClick = { onAnimeClick(anime.id) },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope
                    )
                }
            }

        }


        // Main Filter Bottom Sheet
        if (showMainFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMainFilterSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                FilterSheetContent(
                    uiState = uiState,
                    onOpenSubSheet = { activeSubSheet = it },
                    onGenreToggle = { viewModel.toggleGenre(it) },
                    onReset = { viewModel.resetFilters() },
                    onApply = {
                        showMainFilterSheet = false
                        viewModel.onApplyFilter()
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
                            SubSheetType.SORT -> viewModel.onSortTypeChange(value as SortType)
                            SubSheetType.FORMAT -> viewModel.onFormatChange(value as FormatType)
                            SubSheetType.STATUS -> viewModel.onStatusChange(value as StatusType)
                            SubSheetType.RATING -> viewModel.onRatingChange(value as RatingType)
                        }
                        activeSubSheet = null
                    }
                )
            }
        }
    }


    LaunchedEffect(lazyGridState) {
        var previousIndex = 0

        snapshotFlow {
            val currentIndex = lazyGridState.firstVisibleItemIndex
            val lastVisibleIndex =
                lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            Pair(currentIndex, lastVisibleIndex)

        }
            .distinctUntilChanged()
            .debounce { 300 }
            .filter { (_, lastVisibleIndex) ->
                Log.d("TAG", "SearchAnime:  $lastVisibleIndex")
                lastVisibleIndex >= uiState.animeList.size - 10
            }
            .collect { (currentIndex, _) ->

                val isScrollingDown = currentIndex > previousIndex

                if (isScrollingDown) {
                    viewModel.onLoadMore()
                }

            }
    }

}

enum class SubSheetType { SORT, FORMAT, STATUS, RATING }

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
                modifier = Modifier.weight(1f)
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
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            FilterOptionCard(
                label = "STATUS",
                value = uiState.selectedStatus?.displayName ?: "Select Status",
                icon = Icons.Default.Info,
                onClick = { onOpenSubSheet(SubSheetType.STATUS) },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        FilterOptionCard(
            label = "RATING",
            value = uiState.selectedRating?.displayName ?: "Select Rating",
            icon = Icons.Default.Star,
            onClick = { onOpenSubSheet(SubSheetType.RATING) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // GENRES Section
        SectionHeader(icon = Icons.Default.Category, title = "GENRES")
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.availableGenres) { genre ->
                GenreChip(
                    label = genre.name,
                    isSelected = uiState.selectedGenres.any { it.malId == genre.malId },
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
                modifier = Modifier.weight(0.6f),
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
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

@Composable
fun GenreChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (isSelected) Color(0xFF9EA7E5) else Color.Gray.copy(alpha = 0.5f)
        ),
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
fun SelectionSheetContent(type: SubSheetType, onSelect: (Any) -> Unit) {
    val options: List<Any> = when (type) {
        SubSheetType.SORT -> SortType.entries
        SubSheetType.FORMAT -> FormatType.entries
        SubSheetType.STATUS -> StatusType.entries
        SubSheetType.RATING -> RatingType.entries
    }

    Column(modifier = Modifier.padding(16.dp)) {
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

@Preview(showBackground = true)
@Composable
fun EmptySheet() {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.SearchOff,
            contentDescription = "Search Icon"
        )
        Text(
            text = "No Result Found",
            fontWeight = FontWeight.Bold
        )

    }
}