package com.example.animeexplorer.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.animeexplorer.components.AnimeItem
import com.example.animeexplorer.components.ArcLoader
import com.example.animeexplorer.components.AutoAdvancePager
import com.example.animeexplorer.domain.AnimeUiModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit,
    registerScrollToTop: ((() -> Unit) -> Unit),
    onFabVisibilityChanged: (Boolean) -> Unit

) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    val listState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    val showFab by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 3 }
    }

    LaunchedEffect(listState) {
        snapshotFlow { showFab }
            .distinctUntilChanged()
            .collect { visible -> onFabVisibilityChanged(visible) }
    }

    LaunchedEffect(Unit) {
        registerScrollToTop { scope.launch { listState.animateScrollToItem(0) } }
    }

    HomeScreenContent(
        modifier = modifier,
        state = state,
        onAnimeClick = onAnimeClick,
        listState = listState
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    onAnimeClick: (Int) -> Unit,
    listState: LazyGridState
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
//        AnimeSearchBar(
//            query = state.query,
//            onQueryChange = onQueryChange
//        )

        AnimeList(
            horizontalPagerList = state.horizontalPager,
            trending = state.trending.items,
            top = state.top.items,
            upcoming = state.upcoming.items,
            listState = listState,
            onAnimeClick = onAnimeClick,
            isLoading = state.isRefreshing
        )
    }
}

@Composable
fun AnimeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf(query) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onQueryChange(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Search Anime") },
        singleLine = true,
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
                keyboardController?.hide()
                onQueryChange(text)
            }
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            Icon(
                Icons.Default.Clear, contentDescription = "clear", modifier = Modifier.clickable(
                    onClick = {
                        text = ""
                        onQueryChange("")
                    }
                ))
        }
    )
}

@OptIn(FlowPreview::class)
@Composable
fun AnimeList(
    horizontalPagerList: List<AnimeUiModel>,
    trending: List<AnimeUiModel>,
    top: List<AnimeUiModel>,
    upcoming: List<AnimeUiModel>,
    listState: LazyGridState,
    onAnimeClick: (Int) -> Unit,
    isLoading: Boolean,
) {
//    val shouldLoadMore by remember {
//        derivedStateOf {
//            val layoutInfo = listState.layoutInfo
//            val totalItems = layoutInfo.totalItemsCount
//            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
//
//            totalItems > 0 && lastVisible >= totalItems - 2
//        }
//    }

//    LaunchedEffect(listState) {
//        snapshotFlow { shouldLoadMore }
//            .distinctUntilChanged()
//            .debounce(200)
//            .collect { atBottom ->
//                if (atBottom) {
//                    loadMore()
//                } else {
//                    cancelLoading()
//                }
//            }
//    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {

        if (horizontalPagerList.isNotEmpty() && trending.isNotEmpty() && top.isNotEmpty() && upcoming.isNotEmpty()) {

            item(span = { GridItemSpan(maxLineSpan) }) {
                AutoAdvancePager(
                    horizontalPagerList.subList(0, minOf(10, horizontalPagerList.size))
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader("Top Anime")
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                HorizontalAnimeList(
                    animeList = top.take(10),
                    onAnimeClick = onAnimeClick
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader("Trending Animes")
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                HorizontalAnimeList(
                    animeList = trending.take(10),
                    onAnimeClick = onAnimeClick
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader("Upcoming Animes")
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                HorizontalAnimeList(
                    animeList = upcoming.take(6),
                    onAnimeClick = onAnimeClick
                )
            }
        }
//        items(
//            items = animeList,
//            key = { it.id }
//        ) { anime ->
//            AnimeItem(
//                anime,
//                onClick = { onAnimeClick(anime.id) }
//            )
//        }

        if (isLoading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ArcLoader()
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun HorizontalAnimeList(
    animeList: List<AnimeUiModel>,
    onAnimeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(animeList, key = { it.id }) { anime ->
            AnimeItem(
                anime = anime,
                onClick = { onAnimeClick(anime.id) },
                modifier = Modifier
                    .width(150.dp)
            )
        }
    }
}

