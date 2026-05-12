package com.example.animeexplorer.features.explorer.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.animeexplorer.core.components.AnimeItem
import com.example.animeexplorer.core.components.ArcLoader
import com.example.animeexplorer.core.components.PreviewSharedTransitionContainer
import com.example.animeexplorer.core.domain.AnimeUiModel
import com.example.animeexplorer.ui.theme.AppTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExplorerScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: ExplorerViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()

    LoadMoreOnScroll(
        gridState = gridState,
        animeCount = uiState.animeList.size,
        hasNextPage = uiState.hasNextPage,
        isLoading = uiState.isLoading,
        onLoadMore = viewModel::loadNextPage
    )

    ExplorerContent(
        categoryName = uiState.category.displayName,
        contentState = uiState.contentState,
        gridState = gridState,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        modifier = modifier,
        onAnimeClick = onAnimeClick,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ExplorerContent(
    categoryName: String,
    contentState: ExplorerContentState,
    gridState: LazyGridState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header with back button and title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = categoryName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        when (contentState) {
            ExplorerContentState.Loading -> ExplorerLoading()
            ExplorerContentState.Error -> ExplorerError()
            ExplorerContentState.Empty -> ExplorerEmpty()
            is ExplorerContentState.Content -> ExplorerGrid(
                contentState = contentState,
                gridState = gridState,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onAnimeClick = onAnimeClick
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ExplorerGrid(
    contentState: ExplorerContentState.Content,
    gridState: LazyGridState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onAnimeClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = contentState.animeList.size,
            key = { contentState.animeList[it].id }
        ) { index ->
            val anime = contentState.animeList[index]
            AnimeItem(
                anime = anime,
                onClick = { onAnimeClick(anime.id) },
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope
            )
        }

        if (contentState.isLoadingNextPage) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ArcLoader()
                }
            }
        }
    }
}

@Composable
private fun ExplorerLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ArcLoader()
    }
}

@Composable
private fun ExplorerError() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Unable to fetch anime",
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun ExplorerEmpty() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No anime found",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun LoadMoreOnScroll(
     gridState: LazyGridState,
    animeCount: Int,
     hasNextPage: Boolean,
    isLoading: Boolean,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(gridState, animeCount, hasNextPage, isLoading) {
        snapshotFlow {
            val lastVisibleItemIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            animeCount > 0 && lastVisibleItemIndex >= animeCount - 6 && hasNextPage && !isLoading
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadMore() }
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true, name = "Light")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, name = "Dark")
annotation class PreviewLightDarkWithBackground

@PreviewLightDarkWithBackground
@Composable
fun ExplorerContentPreview(modifier: Modifier = Modifier) {
    AppTheme {
        PreviewSharedTransitionContainer {
            ExplorerContent(
                categoryName = "Trending",
                contentState = ExplorerContentState.Content(
                    animeList = listOf(
                        AnimeUiModel(
                            id = 1,
                            title = "Attack on Titan",
                            description = "Sample Description",
                            imageUrl = "https://myanimelist.net/images/anime/4/19644l.jpg",
                            score = 3.0,
                            duration = "0"
                        ),
                        AnimeUiModel(
                            id = 2,
                            title = "Attack on Titan",
                            description = "Sample Description",
                            imageUrl = "https://myanimelist.net/images/anime/4/19644l.jpg",
                            score = 3.0,
                            duration = "0"
                        ),
                        AnimeUiModel(
                            id = 3,
                            title = "Attack on Titan",
                            description = "Sample Description",
                            imageUrl = "https://myanimelist.net/images/anime/4/19644l.jpg",
                            score = 3.0,
                            duration = "0"
                        ),AnimeUiModel(
                            id = 4,
                            title = "Attack on Titan",
                            description = "Sample Description",
                            imageUrl = "https://myanimelist.net/images/anime/4/19644l.jpg",
                            score = 3.0,
                            duration = "0"
                        ),
                        AnimeUiModel(
                            id = 5,
                            title = "Attack on Titan",
                            description = "Sample Description",
                            imageUrl = "https://myanimelist.net/images/anime/4/19644l.jpg",
                            score = 3.0,
                            duration = "0"
                        )

                    ),
                    isLoadingNextPage = false
                ),
                gridState = rememberLazyGridState(),
                sharedTransitionScope = this,
                animatedContentScope = it,
                modifier = modifier,
                onAnimeClick = {},
                onBackClick = {}
            )
        }
    }
}