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

                Scaffold() { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "animeList",
                    ) {
                        composable("animeList") {
                            AnimeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onAnimeClick = { malId ->
                                    navController.navigate("animeDetail/$malId")
                                }
                            )
                        }

                        composable(
                            "animeDetail/{malId}",
                            arguments = listOf(
                                navArgument("malId") { type = NavType.IntType }
                            )
                        ) {
                            AnimeDetailScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

