package com.example.animeexplorer.features.search.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.animeexplorer.core.components.AnimeItem
import com.example.animeexplorer.core.components.ArcLoader
import com.example.animeexplorer.core.domain.enums.FormatType
import com.example.animeexplorer.core.domain.enums.RatingType
import com.example.animeexplorer.core.domain.enums.SortType
import com.example.animeexplorer.core.domain.enums.StatusType
import com.example.animeexplorer.features.search.ui.composables.ActiveFilterChips
import com.example.animeexplorer.features.search.ui.composables.EmptySheet
import com.example.animeexplorer.features.search.ui.composables.FilterSheetContent
import com.example.animeexplorer.features.search.ui.composables.SearchBar
import com.example.animeexplorer.features.search.ui.composables.SelectionSheetContent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

enum class SubSheetType { SORT, FORMAT, STATUS, RATING }

@Composable
fun SearchAnimeRoute(
    onAnimeClick: (Int) -> Unit,
    onFabVisibilityChanged: (Boolean) -> Unit,
    registerScrollToTop: (() -> Unit) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {

    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val actions = remember(viewModel, onAnimeClick) {
        SearchUiActions(
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onToggleSortOrder = viewModel::toggleSortOrder,
            onOpenAnime = onAnimeClick,
            onRemoveSort = { viewModel.onSortTypeChange(null) },
            onRemoveFormat = { viewModel.onFormatChange(null) },
            onRemoveStatus = { viewModel.onStatusChange(null) },
            onRemoveRating = { viewModel.onRatingChange(null) },
            onRemoveGenre = viewModel::removeGenre,
            onToggleGenre = viewModel::toggleGenre,
            onSortTypeChange = viewModel::onSortTypeChange,
            onFormatChange = viewModel::onFormatChange,
            onStatusChange = viewModel::onStatusChange,
            onRatingChange = viewModel::onRatingChange,
            onResetFilters = viewModel::resetFilters,
            onApplyFilter = viewModel::onApplyFilter,
            onLoadMore = viewModel::onLoadMore
        )
    }

    SearchAnimeContent(
        uiState = uiState,
        actions = actions,
        onFabVisibilityChanged = onFabVisibilityChanged,
        registerScrollToTop = registerScrollToTop,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope
    )
}

@Composable
fun SearchAnimeContent(
    uiState: SearchUiState,
    actions: SearchUiActions,
    onFabVisibilityChanged: (Boolean) -> Unit,
    registerScrollToTop: (() -> Unit) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {

    val lazyGridState = rememberLazyGridState()
    var showMainFilterSheet by remember { mutableStateOf(false) }


    RegisterScrollToTopEffect(
        lazyGridState = lazyGridState,
        onFabVisibilityChanged = onFabVisibilityChanged,
        registerScrollToTop = registerScrollToTop
    )
    RegisterMainFilterSheet(
        uiState = uiState,
        actions = actions,
        showMainFilterSheet = showMainFilterSheet,
        onDismissRequest = { showMainFilterSheet = false }
    )
    PaginationEffect(
        lazyGridState = lazyGridState,
        itemCount = uiState.animeList.size,
        onLoadMore = actions.onLoadMore
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .testTag(SearchTestTags.SCREEN)
    ) {
        SearchBar(
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = { actions.onSearchQueryChange(it) },
            sortOrder = uiState.sortOrder,
            toggleSortOrder = { actions.onToggleSortOrder() },
            showMainFilterSheet = { showMainFilterSheet = true }
        )

        ActiveFilterChips(
            uiState = uiState,
            onRemoveSort = { actions.onSortTypeChange(null) },
            onRemoveFormat = { actions.onFormatChange(null) },
            onRemoveStatus = { actions.onStatusChange(null) },
            onRemoveRating = { actions.onRatingChange(null) },
            onRemoveGenre = { actions.onRemoveGenre(it) },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (uiState.animeList.isEmpty() && uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(SearchTestTags.INITIAL_LOADING),
                contentAlignment = Alignment.Center
            ) {
                ArcLoader()
            }
        }


        AnimeGrid(
            lazyGridState = lazyGridState,
            uiState = uiState,
            actions = actions,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
        )




    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterMainFilterSheet(
    showMainFilterSheet: Boolean,
    onDismissRequest: () -> Unit,
    uiState: SearchUiState,
    actions: SearchUiActions
) {
    var activeSubSheet by remember { mutableStateOf<SubSheetType?>(null) }

    if (showMainFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            modifier = Modifier.testTag(SearchTestTags.MAIN_FILTER_SHEET),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            FilterSheetContent(
                uiState = uiState,
                onOpenSubSheet = { activeSubSheet = it },
                onGenreToggle = { actions.onToggleGenre(it) },
                onReset = { actions.onResetFilters() },
                onApply = {
                    onDismissRequest()
                    actions.onApplyFilter()
                }
            )
        }
    }

    // Filter options bottom sheets
    activeSubSheet?.let { type ->
        ModalBottomSheet(
            onDismissRequest = { activeSubSheet = null },
            modifier = Modifier.testTag(SearchTestTags.SUB_FILTER_SHEET),
            sheetState = rememberModalBottomSheetState()
        ) {
            SelectionSheetContent(
                type = type,
                onSelect = { value ->
                    when (type) {
                        SubSheetType.SORT -> actions.onSortTypeChange(value as SortType)
                        SubSheetType.FORMAT -> actions.onFormatChange(value as FormatType)
                        SubSheetType.STATUS -> actions.onStatusChange(value as StatusType)
                        SubSheetType.RATING -> actions.onRatingChange(value as RatingType)
                    }
                    activeSubSheet = null
                }
            )
        }
    }
}

@Composable
fun AnimeGrid(
    lazyGridState: LazyGridState,
    uiState: SearchUiState,
    actions: SearchUiActions,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .testTag(SearchTestTags.ANIME_GRID),
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
                Box(modifier = Modifier.testTag(SearchTestTags.animeItem(anime.id))) {
                    AnimeItem(
                        anime = anime,
                        onClick = { actions.onOpenAnime(anime.id) },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        if (uiState.isLoading && uiState.animeList.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .testTag(SearchTestTags.PAGINATION_LOADING),
                    contentAlignment = Alignment.Center
                ) {
                    ArcLoader()
                }
            }
        }

    }
}

@Composable
fun RegisterScrollToTopEffect(
    lazyGridState: LazyGridState,
    onFabVisibilityChanged: (Boolean) -> Unit,
    registerScrollToTop: (() -> Unit) -> Unit
) {
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
}

@OptIn(FlowPreview::class)
@Composable
private fun PaginationEffect(
    lazyGridState: LazyGridState,
    itemCount: Int,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(lazyGridState, itemCount) {
        var previousIndex = 0
        snapshotFlow {
            val current = lazyGridState.firstVisibleItemIndex
            val lastVisible = lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            current to lastVisible
        }
            .distinctUntilChanged()
            .filter { (_, lastVisible) -> shouldLoadMore(lastVisible, itemCount) }
            .collect { (current, _) ->
                if (current > previousIndex) onLoadMore()
                previousIndex = current
            }
    }
}

internal fun shouldLoadMore(lastVisibleIndex: Int, totalCount: Int, threshold: Int = 10): Boolean {
    if (totalCount <= 0) return false
    return lastVisibleIndex >= totalCount - threshold
}

