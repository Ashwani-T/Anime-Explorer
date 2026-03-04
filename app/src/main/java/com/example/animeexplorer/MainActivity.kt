package com.example.animeexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.animeexplorer.screens.AnimeDetailScreen
import com.example.animeexplorer.screens.AnimeScreen
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
                    startDestination = AppDestination.AnimeList
                ) {
                    composable<AppDestination.AnimeList> {
                        AnimeScreen(
                            onAnimeClick = { malId ->
                                navController.navigate(AppDestination.AnimeDetail(malId))
                            }
                        )
                    }
                    composable<AppDestination.AnimeDetail> {
                        AnimeDetailScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}


