package com.example.animeexplorer.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.animeexplorer.components.AnimeItem
import com.example.animeexplorer.components.ArcLoader
import com.example.animeexplorer.domain.AnimeUiModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

// HomeScreen.kt
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit,
    onFabVisibilityChanged: (Boolean) -> Unit,           // NEW
    registerScrollToTop: ((() -> Unit) -> Unit)          // NEW
) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    // The listState lives here; FAB depends on it
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Derived FAB visibility from scroll position
    val showFab by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 3 }
    }

    // Notify the app shell when visibility changes
    LaunchedEffect(listState) {
        snapshotFlow { showFab }
            .distinctUntilChanged()
            .collect { visible -> onFabVisibilityChanged(visible) }
    }

    // Give MainActivity a way to scroll to the top
    LaunchedEffect(Unit) {
        registerScrollToTop {
            scope.launch { listState.animateScrollToItem(0) }
        }
    }

    HomeScreenContent(
        modifier = modifier,
        state = state,
        onQueryChange = viewModel::onQueryChange,
        loadMore = viewModel::loadNextPage,
        onAnimeClick = onAnimeClick,
        cancelLoading = viewModel::cancelLoading,
        listState = listState // pass it down
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    onQueryChange: (String) -> Unit,
    loadMore: () -> Unit,
    onAnimeClick: (Int) -> Unit,
    cancelLoading: () -> Unit,
    listState: LazyListState
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        AnimeSearchBar(
            query = state.query,
            onQueryChange = onQueryChange
        )

        AnimeList(
            animeList = state.animeList,
            listState = listState,
            isLoading = state.isLoading,
            loadMore = loadMore,
            onAnimeClick = onAnimeClick,
            cancelLoading = cancelLoading
        )
    }
}

// AnimeSearchBar and AnimeList remain the same as your version (no Scaffold).
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
        trailingIcon = {Icon(Icons.Default.Clear, contentDescription = "clear", modifier = Modifier.clickable(
            onClick = {
                text = ""
                onQueryChange("")
            }
        ))}
    )
}

@OptIn(FlowPreview::class)
@Composable
fun AnimeList(
    animeList: List<AnimeUiModel>,
    listState: LazyListState,
    onAnimeClick: (Int) -> Unit,
    isLoading: Boolean,
    loadMore: () -> Unit,
    cancelLoading: () -> Unit
) {
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            totalItems > 0 && lastVisible >= totalItems - 2
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore }
            .distinctUntilChanged()
            .debounce(200)
            .collect { atBottom ->
                if (atBottom) {
                    loadMore()
                } else {
                    cancelLoading()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = animeList,
            key = {it.id }
        ) { anime ->
            AnimeItem(
                anime,
                onClick = { onAnimeClick(anime.id) }
            )
        }

        if (isLoading) {
            item {
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