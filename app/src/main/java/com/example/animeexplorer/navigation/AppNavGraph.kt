package com.example.animeexplorer.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.animeexplorer.features.collection.ui.AnimeLibrary
import com.example.animeexplorer.features.detail.ui.AnimeDetailScreen
import com.example.animeexplorer.features.episodes.ui.AnimeEpisodeListScreen
import com.example.animeexplorer.features.explorer.domain.ExplorerCategory
import com.example.animeexplorer.features.explorer.ui.ExplorerScreen
import com.example.animeexplorer.features.home.ui.HomeScreen
import com.example.animeexplorer.features.search.ui.SearchAnime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    sheetState: SheetState,
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    onShareActionChanged: ((() -> Unit)?) -> Unit,
    onFabVisibilityChanged: (Boolean) -> Unit,
    onScrollToTopChanged: ((() -> Unit)?) -> Unit
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = AppDestination.Home
        ) {
            navigation<AppDestination.Home>(startDestination = HomeDestination.AnimeList) {
                composable<HomeDestination.AnimeList> {
                    HomeScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable,
                        modifier = Modifier,
                        onAnimeClick = { malId ->
                            navController.navigate(HomeDestination.AnimeDetail(malId)) {
                                launchSingleTop = true
                            }
                        },
                        onExploreCategory = { categoryName ->
                            navController.navigate(HomeDestination.Explorer(categoryName)) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable<HomeDestination.AnimeDetail>(
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = "animeexplorer://animedetail?malId={malId}"
                        },
                        navDeepLink {
                            uriPattern = "https://ashwani-t.github.io/animedetail/anime/?malId={malId}"
                        }
                    )
                ) {
                    AnimeDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable,
                        sheetState = sheetState,
                        showSheet = showSheet,
                        onDismissSheet = onDismissSheet,
                        onNavigateToEpisodes = { malId ->
                            navController.navigate(HomeDestination.AnimeEpisodes(malId)) {
                                launchSingleTop = true
                            }
                        },
                        registerShareAction = onShareActionChanged
                    )
                }
                composable<HomeDestination.AnimeEpisodes> {
                    AnimeEpisodeListScreen()
                }
                composable<HomeDestination.Explorer> { backStackEntry ->
                    val explorer: HomeDestination.Explorer = backStackEntry.toRoute()
                    val category = try {
                        ExplorerCategory.valueOf(explorer.category)
                    } catch (_: Exception) {
                        ExplorerCategory.TRENDING
                    }
                    ExplorerScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable,
                        onAnimeClick = { malId ->
                            navController.navigate(HomeDestination.AnimeDetail(malId)) {
                                launchSingleTop = true
                            }
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
            composable<AppDestination.Search> {
                SearchAnime(
                    onAnimeClick = { malId ->
                        navController.navigate(HomeDestination.AnimeDetail(malId))
                    },
                    onFabVisibilityChanged = onFabVisibilityChanged,
                    registerScrollToTop = onScrollToTopChanged,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable
                )
            }
            composable<AppDestination.MyCollection> {
                AnimeLibrary(
                    onClick = { malId ->
                        navController.navigate(HomeDestination.AnimeDetail(malId))
                    }
                )
            }
        }
    }
}

