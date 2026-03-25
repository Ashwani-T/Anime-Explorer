package com.example.animeexplorer.screens

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.animeexplorer.core.components.AnimeItem
import com.example.animeexplorer.core.components.ArcLoader
import com.example.animeexplorer.core.components.AutoAdvancePager
import com.example.animeexplorer.domain.AnimeUiModel
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit

) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    val listState = rememberLazyGridState()

    HomeScreenContent(
        modifier = modifier,
        state = state,
        onAnimeClick = onAnimeClick,
        listState = listState,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    onAnimeClick: (Int) -> Unit,
    listState: LazyGridState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        AnimeList(
            horizontalPagerList = state.horizontalPager,
            trending = state.trending.items,
            seasonAnime = state.currentSeason.items,
            top = state.top.items,
            upcoming = state.upcoming.items,
            favorite= state.favorites.items,
            listState = listState,
            onAnimeClick = onAnimeClick,
            isLoading = state.isRefreshing,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope
        )
    }
}

@OptIn(FlowPreview::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AnimeList(
    horizontalPagerList: List<AnimeUiModel>,
    seasonAnime: List<AnimeUiModel>,
    trending: List<AnimeUiModel>,
    top: List<AnimeUiModel>,
    upcoming: List<AnimeUiModel>,
    favorite: List<AnimeUiModel>,
    listState: LazyGridState,
    onAnimeClick: (Int) -> Unit,
    isLoading: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        if (horizontalPagerList.isNotEmpty() && trending.isNotEmpty() && top.isNotEmpty() && upcoming.isNotEmpty()) {

            item(key = "pager", span = { GridItemSpan(maxLineSpan) }) {
                AutoAdvancePager(
                    animeList = remember(horizontalPagerList) { horizontalPagerList.subList(0, minOf(10, horizontalPagerList.size)) },
                    onAnimeClick= onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }

            item(key = "header_season", span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader("This Season")
            }

            item(key = "list_season", span = { GridItemSpan(maxLineSpan) }) {
                HorizontalAnimeList(
                    animeList = seasonAnime,
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }
            item(key = "header_favorite", span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader("Favourite Anime")
            }

            item(key = "list_favorite", span = { GridItemSpan(maxLineSpan) }) {
                HorizontalAnimeList(
                    animeList = remember(favorite) { favorite.take(10) },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }

            item(key = "header_top", span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader("Top Anime")
            }

            item(key = "list_top", span = { GridItemSpan(maxLineSpan) }) {
                HorizontalAnimeList(
                    animeList = remember(top) { top.take(10) },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }

            item(key = "header_trending", span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader("Trending Animes")
            }

            item(key = "list_trending", span = { GridItemSpan(maxLineSpan) }) {
                HorizontalAnimeList(
                    animeList = remember(trending) { trending.take(10) },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }

            item(key = "header_upcoming", span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader("Upcoming Animes")
            }

            item(key = "list_upcoming", span = { GridItemSpan(maxLineSpan) }) {
                HorizontalAnimeList(
                    animeList = remember(upcoming) { upcoming.take(6) },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }
        } else if (isLoading) {
            item(key = "loader", span = { GridItemSpan(maxLineSpan) }) {
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HorizontalAnimeList(
    animeList: List<AnimeUiModel>,
    onAnimeClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = animeList, 
            key = { it.id },
            contentType = { "anime_item" }
        ) { anime ->
            AnimeItem(
                anime = anime,
                onClick = { onAnimeClick(anime.id) },
                modifier = Modifier.width(150.dp),
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope
            )
        }
    }
}
