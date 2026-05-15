package com.example.animeexplorer.core.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Share
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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.animeexplorer.core.data.connectivity.ConnectivityObserver
import com.example.animeexplorer.navigation.AppDestination
import com.example.animeexplorer.navigation.AppNavGraph
import com.example.animeexplorer.navigation.AppRouteUiState
import com.example.animeexplorer.navigation.HandleLaunchDeepLink
import com.example.animeexplorer.navigation.HomeDestination
import com.example.animeexplorer.navigation.TopLevelDestination
import com.example.animeexplorer.navigation.resolveRouteUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    connectivityObserver: ConnectivityObserver,
    launchIntent: Intent?
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isConnected by connectivityObserver.observer().collectAsState(true)

    LaunchedEffect(isConnected) {
        if (!isConnected) {
            snackbarHostState.showSnackbar(message = "Connection Lost")
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val routeUiState = resolveRouteUiState(navBackStackEntry?.destination)

    HandleLaunchDeepLink(launchIntent = launchIntent, navController = navController)

    var showFab by rememberSaveable { mutableStateOf(false) }
    var scrollToTop by remember { mutableStateOf<(() -> Unit)?>(null) }
    var shareAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val sheetState = rememberModalBottomSheetState()
    var showSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                routeUiState = routeUiState,
                shareAction = shareAction,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = routeUiState.showBottomBar,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                BottomNavigationBar(navController = navController)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AppFloatingActions(
                routeUiState = routeUiState,
                showFab = showFab,
                scrollToTop = scrollToTop,
                onSaveToCollection = {
                    showSheet = true
                    scope.launch { sheetState.show() }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .animateContentSize()
        ) {
            if (!isConnected) {
                OfflineBanner()
            }
            AppNavGraph(
                navController = navController,
                sheetState = sheetState,
                showSheet = showSheet,
                onDismissSheet = { showSheet = false },
                onShareActionChanged = { action -> shareAction = action },
                onFabVisibilityChanged = { visible -> showFab = visible },
                onScrollToTopChanged = { scroller -> scrollToTop = scroller }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AppTopBar(
    routeUiState: AppRouteUiState,
    shareAction: (() -> Unit)?,
    onBackClick: () -> Unit
) {
    when {
        routeUiState.isAnimeList -> {
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

        routeUiState.isAnimeDetail -> {
            TopAppBar(
                title = { Text("Anime Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    shareAction?.let { action ->
                        IconButton(onClick = action) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Anime"
                            )
                        }
                    }
                }
            )
        }

        routeUiState.isAnimeEpisodes -> {
            TopAppBar(
                title = { Text("Episodes") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }

        routeUiState.isMyCollection -> {
            TopAppBar(
                title = {
                    Text(
                        text = "My Collection",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }

        routeUiState.isSearch -> {
            CenterAlignedTopAppBar(
                title = { Text("Search Anime") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun AppFloatingActions(
    routeUiState: AppRouteUiState,
    showFab: Boolean,
    scrollToTop: (() -> Unit)?,
    onSaveToCollection: () -> Unit
) {
    if (routeUiState.isSearch && showFab) {
        FloatingActionButton(onClick = { scrollToTop?.invoke() }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Scroll to Top"
            )
        }
    }
    if (routeUiState.isAnimeDetail) {
        FloatingActionButton(onClick = onSaveToCollection) {
            Icon(
                imageVector = Icons.Filled.AddCircle,
                contentDescription = "Save to Collection"
            )
        }
    }
}

@Composable
private fun OfflineBanner() {
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
private fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination

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


