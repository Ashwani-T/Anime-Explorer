package com.example.animeexplorer

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.remove
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.animeexplorer.core.data.connectivity.ConnectivityObserver
import com.example.animeexplorer.features.collection.ui.AnimeLibrary
import com.example.animeexplorer.features.detail.ui.AnimeDetailScreen
import com.example.animeexplorer.features.episodes.ui.AnimeEpisodeListScreen
import com.example.animeexplorer.features.explorer.domain.ExplorerCategory
import com.example.animeexplorer.features.explorer.ui.ExplorerScreen
import com.example.animeexplorer.features.home.ui.HomeScreen
import com.example.animeexplorer.features.search.ui.SearchAnime
import com.example.animeexplorer.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppScaffold(connectivityObserver)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun AppScaffold(
    connectivityObserver: ConnectivityObserver
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    // Checking the connectivity status
    val isConnected by connectivityObserver
        .observer()
        .collectAsState(true)

    LaunchedEffect(isConnected) {
        if (!isConnected) {
            snackbarHostState.showSnackbar(
                message = "Connection Lost"
            )
        }
    }
    Log.d("TAG", "AppScaffold: $isConnected")


    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    //checking current active destination
    val isAnimeList = currentDestination?.hasRoute<HomeDestination.AnimeList>() ?: false
    val isAnimeDetail =
        currentDestination?.hasRoute<HomeDestination.AnimeDetail>() ?: false
    val isAnimeEpisodes =
        currentDestination?.hasRoute<HomeDestination.AnimeEpisodes>() ?: false
     val isMyCollection =
        currentDestination?.hasRoute<AppDestination.MyCollection>() ?: false
    val isSearch = currentDestination?.hasRoute<AppDestination.Search>() ?: false

    val bottomBarVisibleRoutes: List<AppDestination> = listOf(
        HomeDestination.AnimeList,
        AppDestination.Search,
        AppDestination.MyCollection
    )
    val showBottomBar = bottomBarVisibleRoutes.any { route ->
        currentDestination?.hasRoute(route::class) == true
    }


    var showFab by rememberSaveable { mutableStateOf(false) }
    var scrollToTop by remember { mutableStateOf<(() -> Unit)?>(null) }

    val sheetState = rememberModalBottomSheetState()
    var showSheet by rememberSaveable { mutableStateOf(false) }
    Log.d("TAG", "showSheet:  $showSheet")

    Scaffold(
        topBar = {
            when {
                isAnimeList -> {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Anime Explorer",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    )
                }

                isAnimeDetail -> {
                    TopAppBar(
                        title = { Text("Anime Details") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }

                isAnimeEpisodes -> {
                    TopAppBar(
                        title = { Text("Episodes") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }

                isMyCollection -> {
                    TopAppBar(
                        title = {
                            Text(
                                text = "My Collection",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                    )
                }

                isSearch -> {
                    CenterAlignedTopAppBar(
                        title = { Text("Search Anime") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                BottomNavigationBar(navController)
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        floatingActionButton = {
            if (isSearch && showFab) {
                FloatingActionButton(
                    onClick = { scrollToTop?.invoke() }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Scroll to Top"
                    )
                }
            }
            if (isAnimeDetail) {
                FloatingActionButton(
                    onClick = {
                        showSheet = true
                        scope.launch {
                            sheetState.show()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Save to Collection"
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .animateContentSize()) {
            if (!isConnected) {
                OfflineBanner()
            }
            SharedTransitionLayout {
                NavHost(
                    navController = navController,
                    startDestination = AppDestination.Home,

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
                        composable<HomeDestination.AnimeDetail> {
                            AnimeDetailScreen(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedContentScope = this@composable,
                                sheetState = sheetState,
                                showSheet = showSheet,
                                onDismissSheet = { showSheet = false },
                                onNavigateToEpisodes = { malId ->
                                    navController.navigate(HomeDestination.AnimeEpisodes(malId)) {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable<HomeDestination.AnimeEpisodes> {
                            AnimeEpisodeListScreen()
                        }
                        composable<HomeDestination.Explorer> { backStackEntry ->
                            val explorer: HomeDestination.Explorer = backStackEntry.toRoute()
                            val category = try {
                                ExplorerCategory.valueOf(explorer.category)
                            } catch (e: Exception) {
                                ExplorerCategory.TRENDING
                            }
                            ExplorerScreen(
                                category = category,
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
                            onFabVisibilityChanged = { visible -> showFab = visible },
                            registerScrollToTop = { scroller -> scrollToTop = scroller },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@composable
                        )
                    }
                    composable<AppDestination.MyCollection> {
                        AnimeLibrary(
                            onClick = { malId ->
                                navController.navigate(
                                    HomeDestination.AnimeDetail(
                                        malId
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "You are Offline",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination
    Log.d("TAG", "BottomNavigationBar: $currentRoute")

    val topLevelDestination = listOf(
        TopLevelDestination(
            route = HomeDestination.AnimeList,
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home,
            label = "Home"
        ),
        TopLevelDestination(
            route = AppDestination.Search,
            selectedIcon = Icons.Filled.Search,
            unSelectedIcon = Icons.Outlined.Search,
            label = "Search"
        ),
        TopLevelDestination(
            route = AppDestination.MyCollection,
            selectedIcon = Icons.Filled.Collections,
            unSelectedIcon = Icons.Outlined.Collections,
            label = "Collections"
        )
    )

    NavigationBar {

        topLevelDestination.forEach { destination ->
            val selected = when (destination.route) {
                AppDestination.Home, HomeDestination.AnimeList ->
                    currentRoute?.hasRoute<HomeDestination>() == true

                AppDestination.Search ->
                    currentRoute?.hasRoute<AppDestination.Search>() == true

                AppDestination.MyCollection ->
                    currentRoute?.hasRoute<AppDestination.MyCollection>() == true

                else -> false
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true

                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unSelectedIcon
                        },
                        contentDescription = ""
                    )
                },
                label = { Text(destination.label) }
            )
        }
    }
}
