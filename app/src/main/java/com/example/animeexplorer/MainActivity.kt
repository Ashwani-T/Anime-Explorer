package com.example.animeexplorer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Home
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.animeexplorer.data.ConnectivityObserver
import com.example.animeexplorer.screens.AnimeDetailScreen
import com.example.animeexplorer.screens.HomeScreen
import com.example.animeexplorer.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

// MainActivity.kt
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppScaffold(connectivityObserver)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppScaffold(
    connectivityObserver: ConnectivityObserver
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val isConnected by connectivityObserver
        .observer()
        .collectAsState(true)

    LaunchedEffect(isConnected) {
        if(!isConnected){
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Connection Lost"
                )
            }
        }
    }
    Log.d("TAG", "AppScaffold: $isConnected")


    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isAnimeList = navBackStackEntry?.destination?.hasRoute<HomeDestination.AnimeList>()?: false
    val isAnimeDetail = navBackStackEntry?.destination?.hasRoute<HomeDestination.AnimeDetail>()?: false
    Log.d("tag ","$isAnimeDetail")


    var showFab by rememberSaveable { mutableStateOf(false) }
    var scrollToTop by remember { mutableStateOf<(() -> Unit)?>(null) }
    Scaffold(
        topBar = {
            when{
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
                            IconButton(onClick = {navController.popBackStack() }) {
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
            BottomNavigationBar(navController = navController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (isAnimeList && showFab) {
                FloatingActionButton(
                    onClick = { scrollToTop?.invoke() }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Scroll to Top"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if(!isConnected){
                OfflineBanner()
            }

            NavHost(
                navController = navController,
                startDestination = AppDestination.Home,

                ) {
                navigation<AppDestination.Home>(startDestination = HomeDestination.AnimeList) {
                    composable<HomeDestination.AnimeList> {
                        HomeScreen(
                            modifier = Modifier,
                            onAnimeClick = { malId ->
                                navController.navigate(HomeDestination.AnimeDetail(malId))
                            },
                            onFabVisibilityChanged = { visible -> showFab = visible },
                            registerScrollToTop = { scroller -> scrollToTop = scroller }
                        )
                    }
                    composable<HomeDestination.AnimeDetail> {
                        AnimeDetailScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineBanner() {
    Box(
        modifier= Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ){
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
    NavigationBar {
        var selectedDestinationIdx by rememberSaveable { mutableIntStateOf(0) }

        val topLevelDestination = listOf(
            TopLevelDestination(
                route = AppDestination.Home,
                selectedIcon = Icons.Filled.Home,
                unSelectedIcon = Icons.Outlined.Home,
                label = "Home"
            ),
            TopLevelDestination(
                route = AppDestination.Category,
                selectedIcon = Icons.Filled.Category,
                unSelectedIcon = Icons.Outlined.Category,
                label = "Category"
            ),
            TopLevelDestination(
                route = AppDestination.MyCollection,
                selectedIcon =  Icons.Filled.Collections,
                unSelectedIcon = Icons.Outlined.Collections,
                label = "Collections"
            )
        )

        topLevelDestination.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = selectedDestinationIdx == index,
                onClick = {
                    navController.navigate(destination.route)
                    selectedDestinationIdx = index
                },
                icon = {
                    Icon(
                        imageVector = if(selectedDestinationIdx == index){
                            destination.selectedIcon
                        }else{
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

