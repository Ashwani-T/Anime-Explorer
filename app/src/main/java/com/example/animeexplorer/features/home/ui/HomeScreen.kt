package com.example.animeexplorer.features.home.ui

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.animeexplorer.core.components.AnimeItem
import com.example.animeexplorer.core.components.AutoAdvancePager
import com.example.animeexplorer.core.components.PreviewSharedTransitionContainer
import com.example.animeexplorer.core.domain.AnimeUiModel
import com.example.animeexplorer.features.explorer.ui.PreviewLightDarkWithBackground
import com.example.animeexplorer.ui.theme.AppTheme
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit,
    onExploreCategory: (String) -> Unit = {}

) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    //Handling Activity Lifecycle and Triggering Refresh
    HomeLifecycleRefresh(onRefresh = viewModel::refresh)

    HomeScreenContent(
        uiState = state,
        onRefresh = { viewModel.forceRefresh(true) },
        modifier = modifier,
        onAnimeClick = onAnimeClick,
        onExploreCategory = onExploreCategory,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope
    )
}

@Composable
private fun HomeLifecycleRefresh(onRefresh: () -> Unit) {
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
        if (wasInBackground) {
            Log.d("TAG", "App returned to foreground")
            onRefresh()
        }
        wasInBackground = false
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onAnimeClick: (Int) -> Unit,
    onExploreCategory: (String) -> Unit = {}
) {
    val refreshState = rememberPullToRefreshState()


    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        state = refreshState

    ) {
        Log.d("TAG", "HomeScreen: ${uiState.isRefreshing} ")
        AnimeList(
            horizontalPagerList = uiState.horizontalPager,
            trending = uiState.trending.items,
            seasonAnime = uiState.currentSeason.items,
            top = uiState.top.items,
            upcoming = uiState.upcoming.items,
            favorite = uiState.favorites.items,
            listState = listState,
            isRefreshing = uiState.isRefreshing,
            onAnimeClick = onAnimeClick,
            onExploreCategory = onExploreCategory,
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
    listState: LazyListState,
    isRefreshing: Boolean,
    onAnimeClick: (Int) -> Unit,
    onExploreCategory: (String) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier
) {

    Log.d("TAG", "AnimeList: $isRefreshing")
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .testTag("home_anime_list")
    ) {
        if (isRefreshing) {
            item(key = "shimmer_pager") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(16.dp)
                        .testTag("home_shimmer_pager")
                        .shimmer()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(16.dp)
                        )
                )
            }
            repeat(3) { index ->
                item(key = "shimmer_header_$index") {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .width(150.dp)
                            .height(24.dp)
                            .testTag("home_shimmer_header_$index")
                            .shimmer()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
                item(key = "shimmer_list_$index") {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_shimmer_list_$index")
                    ) {
                        items(5) {
                            AnimeItemShimmer()
                        }
                    }
                }
            }
        }else{
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
                    animatedContentScope = animatedContentScope,
                    modifier = Modifier.testTag("home_hero_pager")
                )
            }

            item(key = "header_season") {
                SectionHeader(
                    title = "This Season",
                    testTag = "home_section_season_header",
                    onExploreClick = { onExploreCategory("SEASON") }
                )
            }

            item(key = "list_season") {
                HorizontalAnimeList(
                    animeList = seasonAnime,
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    testTag = "home_section_season_list"
                )
            }
            item(key = "header_favorite") {
                SectionHeader(
                    title = "Favourite Anime",
                    testTag = "home_section_favorite_header",
                    onExploreClick = { onExploreCategory("FAVORITE") }
                )
            }

            item(key = "list_favorite") {
                HorizontalAnimeList(
                    animeList = remember(favorite) { favorite.take(10) },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    testTag = "home_section_favorite_list"
                )
            }

            item(key = "header_top") {
                SectionHeader(
                    title = "Top Anime",
                    testTag = "home_section_top_header",
                    onExploreClick = { onExploreCategory("TOP") }
                )
            }

            item(key = "list_top") {
                HorizontalAnimeList(
                    animeList = remember(top) { top.take(10) },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    testTag = "home_section_top_list"
                )
            }

            item(key = "header_trending") {
                SectionHeader(
                    title = "Trending Anime",
                    testTag = "home_section_trending_header",
                    onExploreClick = { onExploreCategory("TRENDING") }
                )
            }

            item(key = "list_trending") {
                HorizontalAnimeList(
                    animeList = remember(trending) { trending.take(10) },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    testTag = "home_section_trending_list"
                )
            }

            item(key = "header_upcoming") {
                SectionHeader(
                    title = "Upcoming Anime",
                    testTag = "home_section_upcoming_header",
                    onExploreClick = { onExploreCategory("UPCOMING") }
                )
            }

            item(key = "list_upcoming") {
                HorizontalAnimeList(
                    animeList = remember(upcoming) { upcoming.take(6) },
                    onAnimeClick = onAnimeClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    testTag = "home_section_upcoming_list"
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    testTag: String = "section_header",
    onExploreClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onExploreClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        IconButton(
            onClick = onExploreClick,
            modifier = Modifier
                .width(40.dp)
                .testTag("${testTag}_button")
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Explore $title",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HorizontalAnimeList(
    animeList: List<AnimeUiModel>,
    onAnimeClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    testTag: String = "horizontal_anime_list"
) {

    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
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
                modifier = Modifier
                    .width(150.dp)
                    .testTag("${testTag}_item_${anime.id}"),
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope
            )
        }
    }
}

@Composable
fun AnimeItemShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(150.dp)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .shimmer()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(12.dp)
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(20.dp)
                .shimmer()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(4.dp)
                )
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun HomeScreenContentFullPreview() {
    PreviewSharedTransitionContainer { animatedContentScope ->
        HomeScreenContent(
            uiState = HomeUiState(
                horizontalPager = previewAnime,
                currentSeason = HomeSection(items = previewAnime),
                favorites = HomeSection(items = previewAnime.take(4)),
                top = HomeSection(items = previewAnime),
                trending = HomeSection(items = previewAnime),
                upcoming = HomeSection(items = previewAnime.take(6))
            ),
            onRefresh = {},
            sharedTransitionScope = this,
            animatedContentScope = animatedContentScope,
            onAnimeClick = {},
            onExploreCategory = {}
        )
    }
}

private val previewAnime = listOf(
    AnimeUiModel(
        id = 1,
        title = "Attack on Titan",
        description = "Humanity fights for survival behind towering walls.",
        duration = "24 min",
        imageUrl = "https://cdn.myanimelist.net/images/anime/10/47347l.jpg",
        score = 9.1
    ),
    AnimeUiModel(
        id = 2,
        title = "Frieren: Beyond Journey's End",
        description = "An elf mage reflects on time, friendship, and adventure.",
        duration = "24 min",
        imageUrl = "https://cdn.myanimelist.net/images/anime/1015/138006l.jpg",
        score = 9.3
    ),
    AnimeUiModel(
        id = 3,
        title = "Fullmetal Alchemist: Brotherhood",
        description = "Two brothers search for the Philosopher's Stone.",
        duration = "24 min",
        imageUrl = "https://cdn.myanimelist.net/images/anime/1223/96541l.jpg",
        score = 9.1
    ),
    AnimeUiModel(
        id = 4,
        title = "Demon Slayer",
        description = "A kindhearted boy becomes a demon slayer.",
        duration = "24 min",
        imageUrl = "https://cdn.myanimelist.net/images/anime/1286/99889l.jpg",
        score = 8.5
    ),
    AnimeUiModel(
        id = 5,
        title = "Jujutsu Kaisen",
        description = "A student joins a secret organization of sorcerers.",
        duration = "24 min",
        imageUrl = "https://cdn.myanimelist.net/images/anime/1171/109222l.jpg",
        score = 8.6
    ),
    AnimeUiModel(
        id = 6,
        title = "Spy x Family",
        description = "A spy builds a pretend family for a mission.",
        duration = "24 min",
        imageUrl = "https://cdn.myanimelist.net/images/anime/1441/122795l.jpg",
        score = 8.5
    )
)
