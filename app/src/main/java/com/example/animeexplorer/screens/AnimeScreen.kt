package com.example.animeexplorer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.animeexplorer.components.ArcLoader


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeScreen(
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit = {}
) {
    val viewModel: AnimeScreenViewModel = hiltViewModel()
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
    ) {innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is AnimeUiState.Loading -> LoadingScreen()
                is AnimeUiState.Success -> AnimeListScreen(
                    viewModel::loadAnimeList,
                    state = state,
                    isLoadingMore = state.isLoadingMore,
                    onAnimeClick = onAnimeClick,
                    modifier = Modifier.fillMaxSize()
                )

                is AnimeUiState.Error -> ErrorScreen(animeList = state.animeUiModel, onRetry = viewModel::onRetry, message = state.message)
            }
        }
    }
}

@Composable
fun ErrorScreen(
    animeList: List<AnimeUiModel> = emptyList(),
    onRetry: () -> Unit = {},
    message: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Text(
            text = "Retry",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .clickable { onRetry() }
        )
        AnimeListScreen(
            loaderFunction = {},
            state = AnimeUiState.Success(animeUiModel = animeList, isLoadingMore = false),
            isLoadingMore = false,
            onAnimeClick = {},
            modifier = Modifier.weight(0.9f)
        )




    }
}

@Composable
fun LoadingScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        ArcLoader()
    }
}

@Composable
fun AnimeListScreen(
    loaderFunction: () -> Unit,
    modifier: Modifier = Modifier,
    state: AnimeUiState.Success,
    isLoadingMore: Boolean,
    onAnimeClick: (Int) -> Unit = {}
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.fillMaxSize(), state = listState
    ) {
        items(state.animeUiModel) { anime ->
            AnimeList(anime, onClick = { onAnimeClick(anime.id) })

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingScreen()
                }
            }
        }

    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }.collect { index ->
            val totalItems = listState.layoutInfo.totalItemsCount


            if (index == totalItems - 1) {
                loaderFunction()
            }

        }
    }
}

@Composable
fun AnimeList(
    anime: AnimeUiModel,
    modifier: Modifier = Modifier,
    onClick: (AnimeUiModel) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(anime) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = anime.imageUrl,
            contentDescription = anime.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = anime.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = anime.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = anime.duration,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}