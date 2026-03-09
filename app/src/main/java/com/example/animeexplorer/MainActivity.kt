package com.example.animeexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.animeexplorer.screens.AnimeDetailScreen
import com.example.animeexplorer.screens.HomeScreen
import com.example.animeexplorer.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = AppDestination.Home
                ) {
                    navigation<AppDestination.Home>(startDestination = HomeDestination.AnimeList) {
                        composable<HomeDestination.AnimeList> {
                            HomeScreen(
                                onAnimeClick = { malId ->
                                    navController.navigate(HomeDestination.AnimeDetail(malId))
                                }
                            )
                        }
                        composable<HomeDestination.AnimeDetail> {
                            AnimeDetailScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}


