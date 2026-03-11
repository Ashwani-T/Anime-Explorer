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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
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


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit
) {
    val viewModel: HomeScreenViewModel = hiltViewModel()

    val state by viewModel.uiState.collectAsState()

    HomeScreenContent(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        loadMore = viewModel::loadingNextPage,
        onAnimeClick = onAnimeClick,
        cancelLoading = viewModel::cancelLoading
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onQueryChange: (String) -> Unit,
    loadMore: () -> Unit,
    onAnimeClick: (Int) -> Unit,
    cancelLoading: () -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showFab = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 3
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Anime Explorer",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
        floatingActionButton = {
            if (showFab.value) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Scroll to Top"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
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
            onQueryChange(text)
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
                Icons.Default.Cancel,
                contentDescription = "clear",
                modifier = Modifier.clickable(

                    onClick = {
                        text = ""
                        onQueryChange(text)
                    }
                )
            )
        }
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
            key = { it.id }
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