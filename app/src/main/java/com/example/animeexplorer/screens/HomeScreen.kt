package com.example.animeexplorer.screens

import android.R
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.animeexplorer.core.components.AnimeItem
import com.example.animeexplorer.core.components.ArcLoader
import com.example.animeexplorer.core.components.AutoAdvancePager
import com.example.animeexplorer.domain.AnimeUiModel
import kotlinx.coroutines.FlowPreview
import kotlin.math.abs

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
    //Handling Activity Lifecycle and Triggering Refresh
    var wasInBackground by rememberSaveable { mutableStateOf(false) }

    LifecycleEventEffect(
        event = Lifecycle.Event.ON_STOP,
        lifecycleOwner = ProcessLifecycleOwner.get()
    ) {
        Log.d("TAG", "App went to background")
        wasInBackground = true
    }

    LifecycleEventEffect(
        event = Lifecycle.Event.ON_START,
        lifecycleOwner = ProcessLifecycleOwner.get()
    ) {
        // To avoid the initial trigger when the screen first loads,
        // you can check the 'currentState' or use a 'LaunchedEffect' with a flag
        if (wasInBackground) {
            Log.d("TAG", "App returned to foreground")
            viewModel.refresh()
        }
        wasInBackground = false
//
    }

    val listState = rememberLazyListState()

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
    listState: LazyListState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    AnimeList(
        horizontalPagerList = state.horizontalPager,
        trending = state.trending.items,
        seasonAnime = state.currentSeason.items,
        top = state.top.items,
        upcoming = state.upcoming.items,
        favorite = state.favorites.items,
        listState = listState,
        onAnimeClick = onAnimeClick,
        isLoading = state.isRefreshing,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope
    )
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
    listState: LazyListState,
    onAnimeClick: (Int) -> Unit,
    isLoading: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        if (horizontalPagerList.isNotEmpty() && trending.isNotEmpty() && top.isNotEmpty() && upcoming.isNotEmpty()) {

            item(key = "pager") {
                AutoAdvancePager(
                    animeList = remember(horizontalPagerList) {
                        horizontalPagerList.subList(
                            0,
                            minOf(10, horizontalPagerList.size)
                        )
                    },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }

            item(key = "header_season") {
                SectionHeader("This Season")
            }

            item(key = "list_season") {
                HorizontalAnimeList(
                    animeList = seasonAnime,
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }
            item(key = "header_favorite") {
                SectionHeader("Favourite Anime")
            }

            item(key = "list_favorite") {
                HorizontalAnimeList(
                    animeList = remember(favorite) { favorite.take(10) },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }

            item(key = "header_top") {
                SectionHeader("Top Anime")
            }

            item(key = "list_top") {
                HorizontalAnimeList(
                    animeList = remember(top) { top.take(10) },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }

            item(key = "header_trending") {
                SectionHeader("Trending Anime")
            }

            item(key = "list_trending") {
                HorizontalAnimeList(
                    animeList = remember(trending) { trending.take(10) },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }

            item(key = "header_upcoming") {
                SectionHeader("Upcoming Anime")
            }

            item(key = "list_upcoming") {
                HorizontalAnimeList(
                    animeList = remember(upcoming) { upcoming.take(6) },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }
        } else if (isLoading) {
            item(key = "loader") {
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

    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            items = animeList,
            key = { it.id }
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
