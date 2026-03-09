package com.example.animeexplorer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.animeexplorer.components.AnimeList
import com.example.animeexplorer.components.ArcLoader
import com.example.animeexplorer.domain.AnimeUiModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit = {}
) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Anime Explorer",
                        textAlign = TextAlign.Center,
                        modifier = modifier.fillMaxWidth()
                    )
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LaunchedEffect(Unit) {
                viewModel.runAnimeListJob()
            }

            when (val state = uiState) {
                is HomeUiState.Loading -> LoadingScreen()
                is HomeUiState.Success -> AnimeListScreen(
                    modifier = Modifier.fillMaxSize(),
                    loaderFunction = viewModel::runAnimeListJob,
                    cancelFunction = viewModel::stopAnimeListJob,
                    state = state,
                    onRetry = { viewModel.runAnimeListJob() },
                    onAnimeClick = onAnimeClick,
                    animeList = state.homeUiModel
                )

                is HomeUiState.Error -> AnimeListScreen(
                    modifier = Modifier.fillMaxSize(),
                    loaderFunction = viewModel::runAnimeListJob,
                    cancelFunction = viewModel::stopAnimeListJob,
                    onRetry = { viewModel.runAnimeListJob() },
                    state = state,
                    animeList = state.homeUiModel
                )
            }
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun AnimeListScreen(
    loaderFunction: () -> Unit,   // fetch next page
    cancelFunction: () -> Unit,   // cancel in-flight pagination job
    modifier: Modifier = Modifier,
    animeList: List<AnimeUiModel>,
    onRetry: () -> Unit = {},
    state: HomeUiState,
    onAnimeClick: (Int) -> Unit = {}
) {
    val listState = rememberLazyListState()

    Box(modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(
                items = animeList,
                key = { it.id }
            ) { anime ->
                AnimeList(anime, onClick = { onAnimeClick(anime.id) })

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            item(key = "bottom_spacer") {
                Spacer(Modifier.height(48.dp))
            }

            if (state is HomeUiState.Success && state.isLoadingMore) {
                item(key = "loading_footer") {
                    LoadingScreen(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp))
                }
            }

        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            totalItems > 0 && lastVisible >= totalItems - 4
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore }
            .distinctUntilChanged()
            .debounce(200)
            .collect { atBottom ->
                if (atBottom) {
                    loaderFunction()
                } else {
                    cancelFunction()
                }
            }
    }
}



@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        ArcLoader()
    }
}